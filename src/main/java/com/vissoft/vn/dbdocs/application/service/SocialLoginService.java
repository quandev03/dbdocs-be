package com.vissoft.vn.dbdocs.application.service;

import com.vissoft.vn.dbdocs.domain.entity.Users;
import com.vissoft.vn.dbdocs.domain.repository.UserRepository;
import com.vissoft.vn.dbdocs.infrastructure.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SocialLoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String handleSocialLogin(String socialId, String email, String fullName, String avatarUrl, Integer provider) {
        Users user = userRepository.findBySocialId(socialId)
                .orElseGet(() -> {
                    Users newUser = Users.builder()
                            .userId(UUID.randomUUID().toString())
                            .socialId(socialId)
                            .email(email)
                            .fullName(fullName)
                            .avatarUrl(avatarUrl)
                            .provider(provider)
                            .build();
                    return userRepository.save(newUser);
                });
        return jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
    }
} 