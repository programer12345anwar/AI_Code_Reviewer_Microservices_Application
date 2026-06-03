package com.aicodereviewer.auth.service;

import com.aicodereviewer.auth.dto.AuthResponse;
import com.aicodereviewer.auth.dto.LoginRequest;
import com.aicodereviewer.auth.dto.SignupRequest;
import com.aicodereviewer.auth.entity.UserAccount;
import com.aicodereviewer.auth.repository.UserAccountRepository;
import com.aicodereviewer.auth.security.JwtService;
import com.aicodereviewer.common.enums.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthBusinessService {

    private final UserAccountRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthResponse signup(SignupRequest request) {
        if (repository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("Email is already registered");
        }

        UserAccount account = new UserAccount();
        account.setEmail(request.email().toLowerCase());
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setRole(request.role() == null ? Role.USER : request.role());

        UserAccount saved = repository.save(account);
        String token = jwtService.generateToken(saved.getEmail(), saved.getRole().name());
        return new AuthResponse(token, saved.getId(), saved.getEmail(), saved.getRole());
    }

    public AuthResponse login(LoginRequest request) {
        UserAccount account = repository.findByEmail(request.email().toLowerCase())
            .orElseThrow(() -> new BadCredentialsException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        String token = jwtService.generateToken(account.getEmail(), account.getRole().name());
        return new AuthResponse(token, account.getId(), account.getEmail(), account.getRole());
    }
}
