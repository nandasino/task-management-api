package com.fernanda.task_management_api.controller;

import com.fernanda.task_management_api.dto.TaskRequest;
import com.fernanda.task_management_api.dto.TaskResponse;
import com.fernanda.task_management_api.entity.TaskStatus;
import com.fernanda.task_management_api.entity.User;
import com.fernanda.task_management_api.service.TaskService;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping
  public ResponseEntity<Page<TaskResponse>> listTasks(
      @AuthenticationPrincipal User user,
      @PageableDefault(sort = "deadline", direction = Sort.Direction.ASC) Pageable pageable) {

    return ResponseEntity.ok(taskService.listTasks(user, pageable));
  }

  @GetMapping("/filter")
  public ResponseEntity<Page<TaskResponse>> filterTasks(
      @RequestParam TaskStatus status,
      @AuthenticationPrincipal User user,
      @PageableDefault(sort = "deadline", direction = Sort.Direction.ASC) Pageable pageable) {

    return ResponseEntity.ok(taskService.filterTasks(user, status, pageable));
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskResponse> getTask(
      @PathVariable UUID id, @AuthenticationPrincipal User user) {

    return ResponseEntity.ok(taskService.getTask(id, user));
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTask(
      @RequestBody @Valid TaskRequest request, @AuthenticationPrincipal User user) {

    return ResponseEntity.status(HttpStatus.CREATED).body(taskService.createTask(request, user));
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskResponse> updateTask(
      @PathVariable UUID id,
      @RequestBody @Valid TaskRequest request,
      @AuthenticationPrincipal User user) {

    return ResponseEntity.ok(taskService.updateTask(id, request, user));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(
      @PathVariable UUID id, @AuthenticationPrincipal User user) {

    taskService.deleteTask(id, user);

    return ResponseEntity.noContent().build();
  }
}
