
package com.multigenesys.ecommerce.service;

import com.multigenesys.ecommerce.dto.auth.JwtResponse;
import com.multigenesys.ecommerce.dto.auth.LoginRequest;
import com.multigenesys.ecommerce.dto.auth.RegisterRequest;

public interface AuthService {

    JwtResponse register(RegisterRequest request);

    JwtResponse login(LoginRequest request);
}
