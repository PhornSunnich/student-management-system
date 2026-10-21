package nichy.studentapp.service;

import lombok.RequiredArgsConstructor;
import nichy.studentapp.dto.UserResponse;
import nichy.studentapp.entity.User;
import nichy.studentapp.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public List<UserResponse> getAllUsers(String role) {
        List<User> users;

        // ប្រសិនបើមានបញ្ជូន ?role=INSTRUCTOR ឬ ?role=STUDENT វានឹង Filter តាម Role
        if (role != null && !role.isEmpty()) {
            users = userRepository.findByRole(role);
        } else {
            users = userRepository.findAll();
        }

        return users.stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return mapToUserResponse(user);
    }

    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}