package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.configuration.JwtProvider;
import android.hcmute.edu.vn.chatbot_spring.dto.request.RegisterRequest;
import android.hcmute.edu.vn.chatbot_spring.enums.USER_ROLE;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import android.hcmute.edu.vn.chatbot_spring.repository.UserRepository;
import android.hcmute.edu.vn.chatbot_spring.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;

    @Override
    public String login(String username, String password) {
        Authentication auth = validate(username, password);
        SecurityContextHolder.getContext().setAuthentication(auth);
        return jwtProvider.generateToken(auth.getName(), auth.getAuthorities().iterator().next().getAuthority());
    }

    private Authentication validate(String username, String password) {
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        if (userDetails==null){
            throw new BadCredentialsException("User not found");
        }

        if (!userDetails.getPassword().equals(passwordEncoder.encode(password))) {
            throw new BadCredentialsException("Password is incorrect");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        if (!user.isVerified()){
            throw new BadCredentialsException("User not verified");
        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
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
