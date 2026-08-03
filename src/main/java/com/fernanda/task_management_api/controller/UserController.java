package com.fernanda.task_management_api.controller;

import com.fernanda.task_management_api.dto.UserResponse;
import com.fernanda.task_management_api.entity.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/user")
public class UserController {

  @GetMapping
  public ResponseEntity<UserResponse> getCurrentUser(@AuthenticationPrincipal User user) {
    UserResponse response = new UserResponse(user.getId(), user.getUsername(), user.getEmail());

    return ResponseEntity.ok(response);
  }
}
