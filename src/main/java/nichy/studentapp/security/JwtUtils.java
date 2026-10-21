package nichy.studentapp.security;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtils {
    // Secret let ( should be 256 bits )
    private final String jwtSecret = "myFirstProjectAboutStudentManagementSystemSpringFramework123";
    private final int jwtExpirationMs = 86400000;

    private Key getSigningKey(){
        return Keys.hmacShaKeyFor(jwtSecret.getBytes());
    }

    // create Token from Username or Email
    public String generateToken(String username){
        return Jwts.builder()
                .subject(username)
                .issuedAt(new Date())
                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(getSigningKey())
                .compact();
    }

    // get Username form Token
    public String getUsernameFromJwtToken(String token){
        return Jwts.parser()
                .verifyWith((javax.crypto.SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    // check correct Token
    public boolean validateJwtToken(String authToken){
        try {
            Jwts.parser()
                    .verifyWith((javax.crypto.SecretKey) getSigningKey())
                    .build()
                    .parseSignedClaims(authToken);
            return true;
        }catch (JwtException e){
            System.err.println("Invalid JWT token: " + e.getMessage());
        }
        return false;
    }
}
