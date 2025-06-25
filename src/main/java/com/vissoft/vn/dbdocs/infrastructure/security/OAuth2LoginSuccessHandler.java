package com.vissoft.vn.dbdocs.infrastructure.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.vissoft.vn.dbdocs.application.service.SocialLoginService;
import com.vissoft.vn.dbdocs.infrastructure.config.JwtConfig;

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

    @Value("${domain.frontend.url}")
    private String frontendDomainUrl;

    private static final String GOOGLE_PROVIDER = "google";
    private static final String GITHUB_PROVIDER = "github";
    private static final String TEXT_EMAIL = "email";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = determineProvider(attributes);
        String socialId = determineSocialId(attributes, provider);
        
        // Đối với GitHub, nếu không có email, sử dụng username làm email
        String email;
        if (GITHUB_PROVIDER.equals(provider) && (attributes.get(TEXT_EMAIL) == null || ((String)attributes.get(TEXT_EMAIL)).isEmpty())) {
            email = (String) attributes.get("login");
            log.info("GitHub login without email, using login name as email: {}", email);
        } else {
            email = (String) attributes.get(TEXT_EMAIL);
        }
        
        String name = determineName(attributes);
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

        // Tạo redirect URL về frontend với token
        String frontendUrl = frontendDomainUrl;
        if (!frontendUrl.endsWith("/")) {
            frontendUrl += "/";
        }
        
        // Redirect về frontend với token trong query parameters
        String redirectUrl = frontendUrl + "auth/callback?token=" + token + 
                           "&tokenType=Bearer&expiresIn=" + jwtConfig.getExpiration() +
                           "&provider=" + provider;
        
        log.info("OAuth2 login success for provider: {}, redirecting to: {}", provider, redirectUrl);
        
        response.sendRedirect(redirectUrl);
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

    private String determineName(Map<String, Object> attributes) {
            return (String) attributes.get("name");
    }

    private String determinePictureUrl(Map<String, Object> attributes, String provider) {
        if (GOOGLE_PROVIDER.equals(provider)) {
            return (String) attributes.get("picture");
        } else {
            return (String) attributes.get("avatar_url");
        }
    }
} 