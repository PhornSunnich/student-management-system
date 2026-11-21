package nichy.studentapp.controller;

import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.AuthRequest;
import nichy.studentapp.dto.AuthResponse;
import nichy.studentapp.dto.RegisterRequest;
import nichy.studentapp.entity.User;
import nichy.studentapp.repository.UserRepository;
import nichy.studentapp.security.JwtUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    // Roles a person may choose for themselves. ADMIN is never self-service.
    private static final Set<String> SELF_SERVICE_ROLES = Set.of("STUDENT", "INSTRUCTOR");

    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        String email = normalize(request.getEmail());
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, request.getPassword()));

            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String jwt = jwtUtils.generateToken(userDetails.getUsername());

            // "ROLE_STUDENT" -> "STUDENT"
            String role = userDetails.getAuthorities().stream()
                    .findFirst()
                    .map(a -> a.getAuthority().replaceFirst("^ROLE_", ""))
                    .orElse("STUDENT");

            return ResponseEntity.ok(new AuthResponse(jwt, userDetails.getUsername(), role));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("message", "Invalid email or password"));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        String email = normalize(request.getEmail());
        String password = request.getPassword();

        if (!email.matches("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$")) {
            return badRequest("Enter a valid email");
        }
        if (password == null || password.length() < 8) {
            return badRequest("Password must be at least 8 characters");
        }

        String role = request.getRole() == null ? "STUDENT"
                : request.getRole().trim().toUpperCase().replaceFirst("^ROLE_", "");
        if (!SELF_SERVICE_ROLES.contains(role)) {
            return badRequest("Invalid role");
        }

        if (userRepository.existsByEmail(email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("message", "An account with this email already exists"));
        }

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // BCrypt hash
        user.setRole(role);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }

    private static String normalize(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }

    private static ResponseEntity<Map<String, String>> badRequest(String message) {
        return ResponseEntity.badRequest().body(Map.of("message", message));
    }
}