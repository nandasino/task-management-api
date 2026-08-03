package com.fernanda.task_management_api.service;

import com.fernanda.task_management_api.dto.UserResponse;
import com.fernanda.task_management_api.entity.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  public UserResponse getCurrentUser(User user) {
    return UserResponse.from(user);
  }
}
