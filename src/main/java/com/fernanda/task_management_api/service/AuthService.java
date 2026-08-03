package com.fernanda.task_management_api.service;

import com.fernanda.task_management_api.dto.LoginRequest;
import com.fernanda.task_management_api.dto.LoginResponse;
import com.fernanda.task_management_api.dto.RegisterRequest;
import com.fernanda.task_management_api.entity.User;
import com.fernanda.task_management_api.exception.DuplicateResourceException;
import com.fernanda.task_management_api.infra.security.TokenService;
import com.fernanda.task_management_api.repository.UserRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final AuthenticationManager authenticationManager;

  public AuthService(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      TokenService tokenService,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.authenticationManager = authenticationManager;
  }

  public LoginResponse register(RegisterRequest request) {
    if (userRepository.existsByEmail(request.email())) {
      throw new DuplicateResourceException("Email is already registered");
    }

    User user = new User();
    user.setUsername(request.username());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));

    userRepository.save(user);

    return new LoginResponse(tokenService.generateToken(user));
  }

  public LoginResponse login(LoginRequest request) {
    var authenticationToken =
        new UsernamePasswordAuthenticationToken(request.email(), request.password());

    authenticationManager.authenticate(authenticationToken);

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new IllegalStateException("Authenticated user not found"));

    return new LoginResponse(tokenService.generateToken(user));
  }
}
