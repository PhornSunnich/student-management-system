package nichy.studentapp.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // Secret key (Should be at least 256 bits)
    private final String jwtSecret = "myFirstProjectAboutStudentManagementSystemSpringFramework123";
    private final int jwtExpirationMs = 86400000; // 24 ម៉ោង

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // បង្កើត Token ពី Username ឬ Email
    public String generateToken(String username) {
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // ទាញយក Username ពី Token
    public String getUsernameFromJwtToken(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // ពិនិត្យភាពត្រឹមត្រូវរបស់ Token
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        } catch (JwtException e) {
            System.err.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }
}
