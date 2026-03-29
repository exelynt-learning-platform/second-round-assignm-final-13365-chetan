package com.multigenesys.ecommerce.dto.auth;

public class JwtResponse {

    public String token;
    public String tokenType = "Bearer";

    public JwtResponse() {
    }

    public JwtResponse(String token) {
        this.token = token;
    }
}
