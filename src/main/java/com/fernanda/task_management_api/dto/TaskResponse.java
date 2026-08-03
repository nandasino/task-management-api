package com.fernanda.task_management_api.dto;

import com.fernanda.task_management_api.entity.Task;
import com.fernanda.task_management_api.entity.TaskStatus;
import com.fernanda.task_management_api.entity.User;
import java.time.LocalDate;
import java.util.UUID;

public record TaskResponse(
    UUID id,
    String title,
    String description,
    TaskStatus status,
    LocalDate createdOn,
    LocalDate deadline,
    UserResponse assignedTo) {

  public static TaskResponse from(Task task) {
    User assignee = task.getAssignedTo();

    return new TaskResponse(
        task.getId(),
        task.getTitle(),
        task.getDescription(),
        task.getStatus(),
        task.getCreatedOn(),
        task.getDeadline(),
        new UserResponse(assignee.getId(), assignee.getUsername(), assignee.getEmail()));
  }
}
