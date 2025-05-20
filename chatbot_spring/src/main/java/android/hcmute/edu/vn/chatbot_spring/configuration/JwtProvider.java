package android.hcmute.edu.vn.chatbot_spring.configuration;

import android.hcmute.edu.vn.chatbot_spring.util.JwtUtil;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtProvider {
    public String generateToken(String email, String role){
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(email)
                .setIssuer("devzeus.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + JwtUtil.EXPIRATION_TIME))
                .claim("role", role);

        // When we sign the toke, we use algorithm HS256 (Header: Algorithm: HS512 + Payload + Signature)
        return jwtBuilder.signWith(JwtUtil.getDecodeKey(), SignatureAlgorithm.HS512).compact();
    }

    public String getEmailFromJwtToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(JwtUtil.getDecodeKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}
