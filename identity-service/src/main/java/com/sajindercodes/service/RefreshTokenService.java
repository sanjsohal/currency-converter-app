package com.sajindercodes.service;

import com.sajindercodes.entity.RefreshToken;
import com.sajindercodes.repository.RefreshTokenRepository;
import com.sajindercodes.repository.UserCredentialRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;
import java.util.Optional;

@Service
public class RefreshTokenService {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    public RefreshToken saveRefreshToken(String username) {
        RefreshToken refreshToken = RefreshToken.builder()
                .tokenId(UUID.randomUUID().toString())
                .userCredential(userCredentialRepository.findByName(username).get())
                .expiryDate(Instant.now().plusMillis(600000))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByTokenId(String tokenId) {
        return refreshTokenRepository.findByTokenId(tokenId);
    }

    public Optional<RefreshToken> findRefreshTokenByUserName(String username) {
        return refreshTokenRepository.findByUserCredential_Name(username);
    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {
        if(refreshToken.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Token expired");
        }
        return refreshToken;
    }

    public void deleteRefreshToken(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }
}
