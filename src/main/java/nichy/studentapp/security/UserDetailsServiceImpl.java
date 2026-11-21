package nichy.studentapp.security;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.entity.User;
import nichy.studentapp.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));

        // Normalize: "student", "STUDENT" and "ROLE_STUDENT" all become "STUDENT"
        String role = user.getRole() == null ? "STUDENT" : user.getRole().trim().toUpperCase();
        if (role.startsWith("ROLE_")) {
            role = role.substring(5);
        }

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password(user.getPassword()) // must be BCrypt-encoded in the database
                .roles(role)                  // grants ROLE_STUDENT, ROLE_INSTRUCTOR, ...
                .build();
    }
}