
package com.multigenesys.ecommerce.service.impl;

import com.multigenesys.ecommerce.dto.auth.JwtResponse;
import com.multigenesys.ecommerce.dto.auth.LoginRequest;
import com.multigenesys.ecommerce.dto.auth.RegisterRequest;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.entity.Role;
import com.multigenesys.ecommerce.exception.BadRequestException;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.AuthService;
import com.multigenesys.ecommerce.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public JwtResponse register(RegisterRequest request) {
        userRepository.findByEmail(request.email).ifPresent(user -> {
            throw new BadRequestException("Email already exists");
        });

        User user = new User();
        user.setName(request.name);
        user.setEmail(request.email);
        user.setPassword(passwordEncoder.encode(request.password));
        user.setRole(Role.USER);

        User savedUser = userRepository.save(user);
        return new JwtResponse(JwtUtil.generateToken(savedUser.getEmail()));
    }

    @Override
    public JwtResponse login(LoginRequest request) {

        User user = userRepository.findByEmail(request.email)
                .orElseThrow(() -> new BadRequestException("User not found"));

        if (!passwordEncoder.matches(request.password, user.getPassword())) {
            throw new BadRequestException("Invalid password");
        }

        return new JwtResponse(JwtUtil.generateToken(user.getEmail()));
    }
}
