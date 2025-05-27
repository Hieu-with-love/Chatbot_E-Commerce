package android.hcmute.edu.vn.chatbot_spring.configuration;

import android.hcmute.edu.vn.chatbot_spring.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtUtil jwtUtil;

    public String generateToken(String email, String role){
        JwtBuilder jwtBuilder = Jwts.builder()
                .setSubject(email)
                .setIssuer("devzeus.com")
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtUtil.getExpirationTime() * 1000))
                .claim("role", role);

        // When we sign the toke, we use algorithm HS256 (Header: Algorithm: HS512 + Payload + Signature)
        return jwtBuilder.signWith(jwtUtil.getDecodeKey(), SignatureAlgorithm.HS512).compact();
    }

    public String getEmailFromJwtToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(jwtUtil.getDecodeKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Claims extractClaimFromToken(String token){
        return Jwts.parserBuilder()
                .setSigningKey(jwtUtil.getDecodeKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
