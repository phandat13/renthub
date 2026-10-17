package com.renthub.auth.dto;
import com.renthub.user.dto.UserResponse;
public record AuthResponse(String accessToken, String tokenType, long expiresIn, UserResponse user) {}