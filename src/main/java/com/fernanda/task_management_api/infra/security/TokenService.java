package com.fernanda.task_management_api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.fernanda.task_management_api.entity.User;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {
  @Value("${api.security.token.issuer}")
  private String issuer;

  @Value("${api.security.token.secret}")
  private String secret;

  public String generateToken(User user) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);

      String token =
          JWT.create()
              .withIssuer(issuer)
              .withSubject(user.getEmail())
              .withExpiresAt(generateExpirationDate())
              .sign(algorithm);
      return token;
    } catch (JWTCreationException e) {
      throw new RuntimeException("Error generating token", e);
    }
  }

  public String validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm).withIssuer(issuer).build().verify(token).getSubject();
    } catch (JWTVerificationException e) {
      return null;
    }
  }

  private Instant generateExpirationDate() {
    return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
  }
}
