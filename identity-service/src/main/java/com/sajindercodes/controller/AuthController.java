package com.sajindercodes.controller;

import com.sajindercodes.dto.AuthRequest;
import com.sajindercodes.dto.JwtResponse;
import com.sajindercodes.dto.RefreshTokenRequest;
import com.sajindercodes.entity.RefreshToken;
import com.sajindercodes.entity.UserCredential;
import com.sajindercodes.service.AuthService;
import com.sajindercodes.service.JwtService;
import com.sajindercodes.service.RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.sql.Ref;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RefreshTokenService refreshTokenService;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody UserCredential userCredential) {
        return authService.saveUser(userCredential);
    }

    @PostMapping("/token")
    public JwtResponse getToken(@RequestBody AuthRequest authRequest) {
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if(authenticate.isAuthenticated()) {
            Optional<RefreshToken> refreshTokenOptional = refreshTokenService.findRefreshTokenByUserName(authRequest.getUsername());
            refreshTokenOptional.ifPresent(refreshToken -> refreshTokenService.deleteRefreshToken(refreshToken));
            RefreshToken refreshToken = refreshTokenService.saveRefreshToken(authRequest.getUsername());
            return JwtResponse.builder()
                    .tokenId(refreshToken.getTokenId())
                    .accessToken(jwtService.generateToken(authRequest.getUsername())).build();
        } else {
            throw new RuntimeException("Unauthorized user");
        }
    }

    @GetMapping("/validate")
    public String validateToken(@RequestParam("token") String token) {
        authService.validateToken(token);
        return "Token is valid";
    }

    @PostMapping("/refreshToken")
    public JwtResponse refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        System.out.println("In refreshToken method");
        return refreshTokenService.findByTokenId(refreshTokenRequest.getTokenId())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUserCredential)
                .map(userInfo -> {
                    String accessToken = jwtService.generateToken(userInfo.getName());
                    return JwtResponse.builder()
                            .accessToken(accessToken)
                            .tokenId(refreshTokenRequest.getTokenId())
                            .build();
                }).orElseThrow(() -> new RuntimeException("Invalid refresh token"));
    }

}
