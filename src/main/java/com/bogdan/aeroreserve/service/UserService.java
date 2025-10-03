package com.bogdan.aeroreserve.service;

import com.bogdan.aeroreserve.entity.UserEntity;
import com.bogdan.aeroreserve.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    //private final PasswordEncoder passwordEncoder;

    public UserEntity registerUser(UserEntity user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with this email already exists");
        }

        //user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Optional<UserEntity> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}