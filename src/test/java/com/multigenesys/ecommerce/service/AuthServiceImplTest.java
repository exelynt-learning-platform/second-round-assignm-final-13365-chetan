package com.multigenesys.ecommerce.service;

import com.multigenesys.ecommerce.dto.auth.JwtResponse;
import com.multigenesys.ecommerce.dto.auth.LoginRequest;
import com.multigenesys.ecommerce.dto.auth.RegisterRequest;
import com.multigenesys.ecommerce.entity.Role;
import com.multigenesys.ecommerce.entity.User;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.impl.AuthServiceImpl;
import com.multigenesys.ecommerce.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUpJwtUtil() throws Exception {
        JwtUtil jwtUtil = new JwtUtil();

        Field secretField = JwtUtil.class.getDeclaredField("secretValue");
        secretField.setAccessible(true);
        secretField.set(jwtUtil, "change-this-before-production-change-this-before-production");

        Field expirationField = JwtUtil.class.getDeclaredField("expirationMsValue");
        expirationField.setAccessible(true);
        expirationField.set(jwtUtil, 86_400_000L);

        Method init = JwtUtil.class.getDeclaredMethod("init");
        init.setAccessible(true);
        init.invoke(jwtUtil);
    }

    @Test
    void registerEncodesPasswordAndReturnsToken() {
        RegisterRequest request = new RegisterRequest();
        request.name = "Test User";
        request.email = "test@example.com";
        request.password = "secret123";

        when(userRepository.findByEmail(request.email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(request.password)).thenReturn("hashed-password");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        JwtResponse response = authService.register(request);

        assertNotNull(response.token);

        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertEquals("Test User", captor.getValue().getName());
        assertEquals("test@example.com", captor.getValue().getEmail());
        assertEquals("hashed-password", captor.getValue().getPassword());
        assertEquals(Role.USER, captor.getValue().getRole());
    }

    @Test
    void loginReturnsTokenForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.email = "test@example.com";
        request.password = "secret123";

        User user = new User();
        user.setEmail(request.email);
        user.setPassword("hashed-password");
        user.setRole(Role.USER);

        when(userRepository.findByEmail(request.email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(request.password, user.getPassword())).thenReturn(true);

        JwtResponse response = authService.login(request);

        assertNotNull(response.token);
        verify(userRepository).findByEmail(request.email);
    }
}
