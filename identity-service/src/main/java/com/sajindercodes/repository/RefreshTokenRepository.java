package com.sajindercodes.repository;

import com.sajindercodes.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Integer> {
    Optional<RefreshToken> findByTokenId(String tokenId);
    Optional<RefreshToken> findByUserCredential_Name(String username);
}
