package com.vissoft.vn.dbdocs.infrastructure.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vissoft.vn.dbdocs.application.service.SocialLoginService;
import com.vissoft.vn.dbdocs.infrastructure.config.JwtConfig;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.TokenResponseDto;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SocialLoginService socialLoginService;
    private final JwtConfig jwtConfig;
    private final ObjectMapper objectMapper;
    
    @Value("${cors.allowed-origins:http://localhost:4200,http://localhost:3000}")
    private String allowedOrigins;

    private static final String GOOGLE_PROVIDER = "google";
    private static final String GITHUB_PROVIDER = "github";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = determineProvider(attributes);
        String socialId = determineSocialId(attributes, provider);
        
        // Đối với GitHub, nếu không có email, sử dụng username làm email
        String email;
        if (GITHUB_PROVIDER.equals(provider) && (attributes.get("email") == null || ((String)attributes.get("email")).isEmpty())) {
            email = (String) attributes.get("login");
            log.info("GitHub login without email, using login name as email: {}", email);
        } else {
            email = (String) attributes.get("email");
        }
        
        String name = determineName(attributes, provider);
        String pictureUrl = determinePictureUrl(attributes, provider);

        log.info("OAuth2 login success - provider: {}, socialId: {}, email/username: {}, name: {}", 
                provider, socialId, email, name);

        String token = socialLoginService.handleSocialLogin(
                socialId,
                email,
                name,
                pictureUrl,
                GOOGLE_PROVIDER.equals(provider) ? 1 : 2
        );

        TokenResponseDto tokenResponse = new TokenResponseDto(
                token,
                "Bearer",
                jwtConfig.getExpiration()
        );

        // Lấy redirect_uri từ request parameters
        String redirectUri = request.getParameter("redirect_uri");
        String targetOrigin = determineTargetOrigin(redirectUri);
        
        log.info("OAuth2 login success for provider: {}, redirectUri: {}, targetOrigin: {}", 
                provider, redirectUri, targetOrigin);

        String html = "<!DOCTYPE html>\n" +
                  "<html>\n" +
                  "<head>\n" +
                  "    <title>Authentication Success</title>\n" +
                  "</head>\n" +
                  "<body>\n" +
                  "    <h3>Authentication Successful!</h3>\n" +
                  "    <p>This window will close automatically.</p>\n" +
                  "    <script>\n" +
                  "        try {\n" +
                  "            // Lấy target origin từ query parameter hoặc referer\n" +
                  "            let targetOrigin = '" + targetOrigin + "';\n" +
                  "            console.log('Target origin:', targetOrigin);\n" +
                  "            \n" +
                  "            // Gửi token về cửa sổ cha qua postMessage\n" +
                  "            if (window.opener) {\n" +
                  "                const tokenData = " + objectMapper.writeValueAsString(tokenResponse) + ";\n" +
                  "                console.log('Sending token data to parent window');\n" +
                  "                window.opener.postMessage(tokenData, targetOrigin);\n" +
                  "                \n" +
                  "                // Đóng cửa sổ sau 1 giây\n" +
                  "                setTimeout(() => window.close(), 1000);\n" +
                  "            } else {\n" +
                  "                console.log('No opener window found, saving token to localStorage');\n" +
                  "                // Nếu không có cửa sổ cha (trường hợp không dùng popup)\n" +
                  "                localStorage.setItem('token', '" + token + "');\n" +
                  "                localStorage.setItem('tokenType', 'Bearer');\n" +
                  "                localStorage.setItem('expiresIn', '" + jwtConfig.getExpiration() + "');\n" +
                  "                \n" +
                  "                // Chuyển hướng về trang chủ hoặc redirect_uri nếu có\n" +
                  "                window.location.href = '" + (redirectUri != null ? redirectUri : "/") + "';\n" +
                  "            }\n" +
                  "        } catch (e) {\n" +
                  "            console.error('Error saving token:', e);\n" +
                  "            document.body.innerHTML += '<p>Error: ' + e.message + '</p>';\n" +
                  "        }\n" +
                  "    </script>\n" +
                  "</body>\n" +
                  "</html>";

        response.setContentType(MediaType.TEXT_HTML_VALUE);
        response.getWriter().write(html);
    }
    
    private String determineTargetOrigin(String redirectUri) {
        // Mặc định là "*" nếu allowedOrigins là "*"
        if ("*".equals(allowedOrigins.trim())) {
            return "*";
        }
        
        // Nếu có redirect_uri, lấy origin từ đó
        if (redirectUri != null && !redirectUri.isEmpty()) {
            try {
                java.net.URL url = new java.net.URL(redirectUri);
                return url.getProtocol() + "://" + url.getAuthority();
            } catch (Exception e) {
                log.error("Invalid redirect_uri: {}", redirectUri, e);
            }
        }
        
        // Nếu không có redirect_uri hợp lệ, sử dụng origin đầu tiên trong danh sách allowed origins
        String[] origins = allowedOrigins.split(",");
        if (origins.length > 0) {
            String origin = origins[0].trim();
            // Đảm bảo origin có protocol
            if (!origin.startsWith("http://") && !origin.startsWith("https://")) {
                origin = "http://" + origin;
            }
            return origin;
        }
        
        // Fallback cuối cùng
        return "http://localhost:4200";
    }

    private String determineProvider(Map<String, Object> attributes) {
        if (attributes.containsKey("sub")) {
            return GOOGLE_PROVIDER;
        } else if (attributes.containsKey("id")) {
            return GITHUB_PROVIDER;
        }
        throw new IllegalArgumentException("Unknown OAuth2 provider");
    }

    private String determineSocialId(Map<String, Object> attributes, String provider) {
        return GOOGLE_PROVIDER.equals(provider)
                ? (String) attributes.get("sub")
                : String.valueOf(attributes.get("id"));
    }

    private String determineName(Map<String, Object> attributes, String provider) {
        if (GOOGLE_PROVIDER.equals(provider)) {
            return (String) attributes.get("name");
        } else {
            // GitHub trả về login là username và name là fullName
            return (String) attributes.get("name");
        }
    }

    private String determinePictureUrl(Map<String, Object> attributes, String provider) {
        if (GOOGLE_PROVIDER.equals(provider)) {
            return (String) attributes.get("picture");
        } else {
            return (String) attributes.get("avatar_url");
        }
    }
} 