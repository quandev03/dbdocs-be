package com.vissoft.vn.dbdocs.application.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vissoft.vn.dbdocs.domain.entity.Users;
import com.vissoft.vn.dbdocs.domain.repository.UserRepository;
import com.vissoft.vn.dbdocs.infrastructure.security.JwtTokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class SocialLoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public String handleSocialLogin(String socialId, String email, String fullName, String avatarUrl, Integer provider) {
        log.info("Handling social login - socialId: {}, email: {}, fullName: {}, provider: {}", 
                socialId, email, fullName, provider);
        
        Users user = userRepository.findBySocialId(socialId)
                .orElseGet(() -> {
                    log.info("Creating new user with socialId: {}", socialId);
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
        
        // Cập nhật thông tin người dùng nếu cần
        boolean needsUpdate = false;
        
        if (email != null && !email.equals(user.getEmail())) {
            user.setEmail(email);
            needsUpdate = true;
        }
        
        if (fullName != null && !fullName.equals(user.getFullName())) {
            user.setFullName(fullName);
            needsUpdate = true;
        }
        
        if (avatarUrl != null && !avatarUrl.equals(user.getAvatarUrl())) {
            user.setAvatarUrl(avatarUrl);
            needsUpdate = true;
        }
        
        if (needsUpdate) {
            log.info("Updating user information for socialId: {}", socialId);
            userRepository.save(user);
        }
        
        return jwtTokenProvider.generateToken(user.getUserId(), user.getEmail());
    }
} 