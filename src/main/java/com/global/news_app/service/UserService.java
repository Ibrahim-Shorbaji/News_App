package com.global.news_app.service;

import com.global.news_app.models.*;
import com.global.news_app.payload.request.UserRequest;
import com.global.news_app.payload.response.UserResponse;
import com.global.news_app.mapper.UserMapper;
import com.global.news_app.repository.RoleRepository;
import com.global.news_app.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserResponse createUser(UserRequest request) {
        User user = UserMapper.toEntity(request);

        Set<Role> roles = request.getRoles().stream()
                .map(erole -> roleRepository.findByName(erole)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found: " + erole)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return UserMapper.toDto(userRepository.save(user));
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User updateUser(Long id, UserRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }
        user.setDateOfBirth(request.getDateOfBirth());

        Set<Role> roles = request.getRoles().stream()
                .map(erole -> roleRepository.findByName(erole)
                        .orElseThrow(() -> new RuntimeException("Error: Role not found: " + erole)))
                .collect(Collectors.toSet());
        user.setRoles(roles);

        return userRepository.save(user);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
