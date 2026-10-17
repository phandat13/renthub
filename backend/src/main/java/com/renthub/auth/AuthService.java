package com.renthub.auth;

import com.renthub.auth.dto.*;
import com.renthub.common.exception.ConflictException;
import com.renthub.security.JwtService;
import com.renthub.security.UserPrincipal;
import com.renthub.user.User;
import com.renthub.user.UserRepository;
import com.renthub.user.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse register(RegisterRequest req) {
        String email = req.email().trim().toLowerCase();
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Email đã được sử dụng");
        }
        User user = new User();
        user.setEmail(email);
        user.setPasswordHash(passwordEncoder.encode(req.password()));
        user.setFullName(req.fullName().trim());
        user.setPhone(req.phone() == null || req.phone().isBlank() ? null : req.phone().trim());
        userRepository.save(user);
        return toResponse(new UserPrincipal(user), user);
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest req) {
        Authentication auth = authenticationManager.authenticate(
                UsernamePasswordAuthenticationToken.unauthenticated(
                        req.email().trim().toLowerCase(), req.password()));
        UserPrincipal principal = (UserPrincipal) auth.getPrincipal();
        User user = userRepository.findById(principal.getId()).orElseThrow();
        return toResponse(principal, user);
    }

    private AuthResponse toResponse(UserPrincipal principal, User user) {
        return new AuthResponse(jwtService.generateToken(principal), "Bearer",
                jwtService.getExpirationMs() / 1000, UserResponse.from(user));
    }
}
