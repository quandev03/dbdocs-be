package com.vissoft.vn.dbdocs.infrastructure.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vissoft.vn.dbdocs.application.service.SocialLoginService;
import com.vissoft.vn.dbdocs.infrastructure.config.JwtConfig;
import com.vissoft.vn.dbdocs.interfaces.rest.dto.TokenResponseDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SocialLoginService socialLoginService;
    private final JwtConfig jwtConfig;
    private final ObjectMapper objectMapper;

    private static final String GOOGLE_PROVIDER = "google";
    private static final String GITHUB_PROVIDER = "github";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                      Authentication authentication) throws IOException {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String provider = determineProvider(attributes);
        String socialId = determineSocialId(attributes, provider);
        String email = (String) attributes.get("email");
        String name = determineName(attributes, provider);
        String pictureUrl = determinePictureUrl(attributes, provider);

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

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(tokenResponse));
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
            return (String) attributes.get("login");
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