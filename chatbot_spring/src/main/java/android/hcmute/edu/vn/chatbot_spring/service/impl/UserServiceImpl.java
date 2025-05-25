package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.configuration.JwtProvider;

import android.hcmute.edu.vn.chatbot_spring.dto.response.ChatSessionResponse;

import android.hcmute.edu.vn.chatbot_spring.dto.response.UserDetailResponse;

import android.hcmute.edu.vn.chatbot_spring.dto.response.UserResponse;
import android.hcmute.edu.vn.chatbot_spring.mapper.UserMapper;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import android.hcmute.edu.vn.chatbot_spring.repository.ChatSessionRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.UserRepository;
import android.hcmute.edu.vn.chatbot_spring.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final JwtProvider jwtProvider;
    private final UserRepository userRepository;
    private final ChatSessionRepository chatSessionRepository;

    @Override
    public UserResponse getChatSessionByEmail(String token) {
        String email = jwtProvider.getEmailFromJwtToken(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (chatSessionRepository.existsAnyChatSessionByUserId(user.getId())) {
            return UserMapper.toResponse(user);
        }

        return UserResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .role(user.getRole())
                .build();
    }

    @Override
    public UserDetailResponse getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return this.convertToDetailResponse(user);
    }

    private UserDetailResponse convertToDetailResponse(User user) {
        UserDetailResponse response = new UserDetailResponse();
        response.setId(user.getId());
        response.setFullName(user.getFullName());
        response.setPhone(user.getPhone());
        response.setEmail(user.getEmail());
        response.setAvatarUrl(user.getAvatarUrl());
        response.setRole(user.getRole());
        response.setVerified(user.isVerified());
        return response;
    }
}

