package com.fernanda.task_management_api.dto;

import java.util.UUID;

public record UserResponse(UUID id, String username, String email) {}
