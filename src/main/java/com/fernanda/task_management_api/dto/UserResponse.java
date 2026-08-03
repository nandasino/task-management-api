package com.fernanda.task_management_api.dto;

import com.fernanda.task_management_api.entity.User;
import java.util.UUID;

public record UserResponse(UUID id, String username, String email) {

  public static UserResponse from(User user) {
    return new UserResponse(user.getId(), user.getUsername(), user.getEmail());
  }
}
