package com.fernanda.task_management_api.controller;

import com.fernanda.task_management_api.dto.LoginRequest;
import com.fernanda.task_management_api.dto.LoginResponse;
import com.fernanda.task_management_api.dto.RegisterRequest;
import com.fernanda.task_management_api.entity.User;
import com.fernanda.task_management_api.infra.security.TokenService;
import com.fernanda.task_management_api.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenService tokenService;
  private final AuthenticationManager authenticationManager;

  public AuthController(
      UserRepository userRepository,
      PasswordEncoder passwordEncoder,
      TokenService tokenService,
      AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenService = tokenService;
    this.authenticationManager = authenticationManager;
  }

  @PostMapping("/register")
  public ResponseEntity<LoginResponse> register(@RequestBody @Valid RegisterRequest request) {

    if (userRepository.existsByEmail(request.email())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    User user = new User();
    user.setUsername(request.username());
    user.setEmail(request.email());
    user.setPassword(passwordEncoder.encode(request.password()));

    userRepository.save(user);

    String token = tokenService.generateToken(user);

    return ResponseEntity.status(HttpStatus.CREATED).body(new LoginResponse(token));
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {

    var authenticationToken =
        new UsernamePasswordAuthenticationToken(request.email(), request.password());

    authenticationManager.authenticate(authenticationToken);

    User user =
        userRepository
            .findByEmail(request.email())
            .orElseThrow(() -> new RuntimeException("User not found"));

    String token = tokenService.generateToken(user);

    return ResponseEntity.ok(new LoginResponse(token));
  }
}
