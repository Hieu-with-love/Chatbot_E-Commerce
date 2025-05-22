package android.hcmute.edu.vn.chatbot_spring.configuration;

import android.hcmute.edu.vn.chatbot_spring.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class PreFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {
        log.info("{} {}", req.getMethod(), req.getRequestURI());
        if (byPassPath(req.getRequestURI())) {
            filterChain.doFilter(req, res);
            return;
        }

        String token = req.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")){
            try{
                String jwt = token.substring(7);
                // decode jwt. Then we get claims
                Claims claims = Jwts.parserBuilder()
                        .setSigningKey(jwtUtil.getDecodeKey())
                        .build()
                        .parseClaimsJws(jwt)
                        .getBody();

                String email = String.valueOf(claims.getSubject());
                String role = (String) claims.get("role");

                // save email and authorities to security context
                List<? extends GrantedAuthority> listAuth = AuthorityUtils.commaSeparatedStringToAuthorityList(role);
                Authentication auth = new UsernamePasswordAuthenticationToken(email, null, listAuth);
                SecurityContextHolder.getContext().setAuthentication(auth);

            }catch (Exception ex){
                log.error("Invalid token: {}", ex.getMessage());
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token");
                return;
            }
        }

        filterChain.doFilter(req, res);
    }

    private boolean byPassPath(String path) {

        String[] byPassPaths = {
                "/api/v1/auth/login",
                "/api/v1/auth/register",
                "/api/v1/auth/verify-account",
                "/api/v1/customer/hotels",
                "/api/v1/customer/hotels/{hotelId}",
                "/api/v1/customer/rooms/{roomId}",
                "/api/v1/customer/rooms"
        };

        for (String byPassPath : byPassPaths) {
            if (path.startsWith(byPassPath)) {
                return true;
            }
        }
        return false;
    }

}