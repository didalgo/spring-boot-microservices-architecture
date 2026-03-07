package com.idalgo.daniel.authservice.repository;

import com.idalgo.daniel.authservice.entity.RefreshToken;
import com.idalgo.daniel.authservice.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);
}