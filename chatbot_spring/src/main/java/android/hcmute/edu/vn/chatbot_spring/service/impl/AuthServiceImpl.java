package android.hcmute.edu.vn.chatbot_spring.service.impl;

import android.hcmute.edu.vn.chatbot_spring.configuration.JwtProvider;
import android.hcmute.edu.vn.chatbot_spring.dto.request.RegisterRequest;
import android.hcmute.edu.vn.chatbot_spring.enums.USER_ROLE;
import android.hcmute.edu.vn.chatbot_spring.model.User;
import android.hcmute.edu.vn.chatbot_spring.model.VerificationCode;
import android.hcmute.edu.vn.chatbot_spring.repository.UserRepository;
import android.hcmute.edu.vn.chatbot_spring.repository.VerificationCodeRepository;
import android.hcmute.edu.vn.chatbot_spring.service.AuthService;
import android.hcmute.edu.vn.chatbot_spring.service.EmailService;
import android.hcmute.edu.vn.chatbot_spring.util.EmailUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;
    private final JwtProvider jwtProvider;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

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

        if (!passwordEncoder.matches(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Password is incorrect");
        }

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new BadCredentialsException("User not found"));
//        if (!user.isVerified()){
//            throw new BadCredentialsException("User not verified");
//        }

        return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
    }

    @Override
    public User register(RegisterRequest req) throws IllegalAccessException {
        try{
            USER_ROLE role;
            if (req.getRole()==null){
                role = USER_ROLE.USER;
            }else {
                role = USER_ROLE.valueOf(req.getRole());
            }

            // check valid requirement
            if (userRepository.existsByEmail(req.getEmail())) {
                throw new BadCredentialsException("Email already in use");
            }
            if (userRepository.existsByPhone(req.getPhone())){
                throw new BadCredentialsException("Phone already in use");
            }
            // Check password as strong password
            if (!isStrongPassword(req.getPassword())){
                throw new BadCredentialsException("Password is weak.");
            }

            User user = new User();
            user.setEmail(req.getEmail());
            user.setFullName(req.getFullName());
            user.setPhone(req.getPhone());
            user.setPassword(passwordEncoder.encode(req.getPassword()));
            user.setRole(role.getRole());

            saveCodeAndSendEmail(user);

            User savedUser = userRepository.save(user);

            return savedUser;
        }catch (Exception e){
            throw new IllegalAccessException("Failed to register user: " + e.getMessage());
        }
    }

    private void saveCodeAndSendEmail(User user) {

        // Send otp to verify account
        String otp = EmailUtil.generateOtp();
        String subject = "Verify your account";
        String body = "Hello " + user.getFullName() + ", you just registered an account on our system.\n"
                + "To use the system, please verify your account by entering the OTP code below:\n"
                + "Thanks for using our service.\n"
                + otp + "\n\n"
                + "Supported by Devzeus.\n";

        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setCode(otp);
        verificationCode.setUser(user);
        verificationCode.setCreatedAt(LocalDate.now());
        verificationCode.setExpiresAt(LocalDate.now().plusDays(15));
        verificationCodeRepository.save(verificationCode);

        emailService.sendEmail(user.getEmail(), subject, body);
    }

    private boolean isStrongPassword(String password){
        // Password must be at least 6 characters and
        // at least on digit
        // at least on UPPER CASE
        // at least on lower case
        // at least on special character
        String specialRegex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{6,}$";
        return password.matches(specialRegex);
    }

    @Override
    public boolean verifyOtp(String otp) {
         VerificationCode code = verificationCodeRepository.findByCode(otp)
                .orElseThrow(() -> new BadCredentialsException("Invalid verification code"));

        // If has verification code -> existing user
        User user = userRepository.findByEmail(code.getUser().getEmail())
                .orElseThrow(() -> new BadCredentialsException("User not found"));
        user.setVerified(true);
        userRepository.save(user);
        // delete code to save space
        verificationCodeRepository.delete(code);

        return true;
    }

    @Override
    public boolean resetPassword(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("User not found"));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Password is incorrect");
        }

        String newPassword = EmailUtil.generatePassword(8);
        user.setPassword(passwordEncoder.encode(newPassword));
        return true;
    }

    @Override
    public boolean logout(String token) {


        return false;
    }
}
