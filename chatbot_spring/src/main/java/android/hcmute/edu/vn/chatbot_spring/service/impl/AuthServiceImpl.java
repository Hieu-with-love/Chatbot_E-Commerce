package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.dto.request.RegisterRequest;
import android.hcmute.edu.vn.chatbot_spring.enums.USER_ROLE;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import android.hcmute.edu.vn.chatbot_spring.repository.UserRepository;
import android.hcmute.edu.vn.chatbot_spring.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public boolean login(String username, String password) {
        return false;
    }

    @Override
    public boolean register(RegisterRequest req) {
        try{
            USER_ROLE role;
            if (req.getRole()==null){
                role = USER_ROLE.USER;
            }else {
                role = USER_ROLE.valueOf(req.getRole());
            }

            User user = new User();
            user.setEmail(req.getEmail());
            user.setFullName(req.getFullName());
            user.setPhone(req.getPhone());
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            user.setRole(role.getRole());

            userRepository.save(user);

            return true;
        }catch (Exception e){
            return false;
        }
    }
}
