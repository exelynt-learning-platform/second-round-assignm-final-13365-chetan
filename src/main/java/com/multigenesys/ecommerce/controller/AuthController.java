
package com.multigenesys.ecommerce.controller;

import com.multigenesys.ecommerce.dto.auth.JwtResponse;
import com.multigenesys.ecommerce.dto.auth.LoginRequest;
import com.multigenesys.ecommerce.dto.auth.RegisterRequest;
import com.multigenesys.ecommerce.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public JwtResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public JwtResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.login(request);
    }
}
