package com.ipap.springboot3springsecurity6jwt.service;

import com.ipap.springboot3springsecurity6jwt.dto.AuthenticationRequest;
import com.ipap.springboot3springsecurity6jwt.dto.AuthenticationResponse;
import com.ipap.springboot3springsecurity6jwt.dto.RegisterRequest;
import com.ipap.springboot3springsecurity6jwt.entity.Role;
import com.ipap.springboot3springsecurity6jwt.entity.User;
import com.ipap.springboot3springsecurity6jwt.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationResponse register(RegisterRequest request) {
        var user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();
        // Save user to DB
        repository.save(user);
        // Generate token
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        // If not authenticated, the authenticate method will throw an exception
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        // If authenticated then:
        var user = repository.findByEmail(request.getEmail()).orElseThrow();
        // Generate token
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .token(jwtToken)
                .build();
    }
}
