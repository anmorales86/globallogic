package com.anderson.globallogic.service.impl;

import com.anderson.globallogic.model.entities.UserEntity;
import com.anderson.globallogic.repositories.IUserRepository;
import com.anderson.globallogic.service.IJwtService;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Date;
import java.util.Optional;

@Service
public class JwtServiceImpl implements IJwtService {

    private static final String SECRET_KEY = "secret";
    private final IUserRepository iUserRepository;

    public JwtServiceImpl(IUserRepository iUserRepository) {
        this.iUserRepository = iUserRepository;
    }

    public String generateToken(String email) {
        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60)) // 1 hora
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(SECRET_KEY).parseClaimsJws(token);
            return true;
        } catch (JwtException e) {
            return false;
        }
    }

    public UserEntity getInfoUserFromToken(String token) {
        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        }

        // Validar el token
        if (!validateToken(token)) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Invalid or expired token");
        }

        // Obtener email desde el token

        Optional<UserEntity> userOptional = iUserRepository.findByToken(token);

        // Validar existencia del token en la base de datos
        if (userOptional.isEmpty()) {
            throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "Token not found or mismatched");
        }
        return userOptional.get();
    }


}
