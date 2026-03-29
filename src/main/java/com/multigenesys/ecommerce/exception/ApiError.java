package com.multigenesys.ecommerce.exception;

import java.time.Instant;

public class ApiError {

    public Instant timestamp = Instant.now();
    public int status;
    public String message;
    public String path;

    public ApiError(int status, String message, String path) {
        this.status = status;
        this.message = message;
        this.path = path;
    }
}
