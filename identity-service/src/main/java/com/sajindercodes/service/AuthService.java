package com.sajindercodes.service;

import com.netflix.discovery.converters.Auto;
import com.sajindercodes.dto.AuthRequest;
import com.sajindercodes.entity.UserCredential;
import com.sajindercodes.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public String saveUser(UserCredential userCredential) {
        userCredential.setPassword(passwordEncoder.encode(userCredential.getPassword()));
        userCredentialRepository.save(userCredential);
        return "User saved successfully";
    }

    public String getToken(AuthRequest authRequest) {
        return jwtService.generateToken(authRequest.getUsername());
    }

    public void validateToken(String token) {
        jwtService.validateToken(token);
    }
}
