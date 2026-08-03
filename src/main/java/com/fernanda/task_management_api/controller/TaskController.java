package com.fernanda.task_management_api.controller;

import com.fernanda.task_management_api.dto.TaskRequest;
import com.fernanda.task_management_api.dto.TaskResponse;
import com.fernanda.task_management_api.entity.Task;
import com.fernanda.task_management_api.entity.TaskStatus;
import com.fernanda.task_management_api.entity.User;
import com.fernanda.task_management_api.repository.TaskRepository;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/tasks")
public class TaskController {

  private final TaskRepository taskRepository;

  public TaskController(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  @GetMapping
  public ResponseEntity<Page<TaskResponse>> listTasks(
      @AuthenticationPrincipal User user,
      @PageableDefault(sort = "deadline", direction = Sort.Direction.ASC) Pageable pageable) {

    Page<TaskResponse> tasks =
        taskRepository.findByAssignedTo(user, pageable).map(TaskResponse::from);

    return ResponseEntity.ok(tasks);
  }

  @GetMapping("/filter")
  public ResponseEntity<Page<TaskResponse>> filterTasks(
      @RequestParam TaskStatus status,
      @AuthenticationPrincipal User user,
      @PageableDefault(sort = "deadline", direction = Sort.Direction.ASC) Pageable pageable) {

    Page<TaskResponse> tasks =
        taskRepository.findByAssignedToAndStatus(user, status, pageable).map(TaskResponse::from);

    return ResponseEntity.ok(tasks);
  }

  @GetMapping("/{id}")
  public ResponseEntity<TaskResponse> getTask(
      @PathVariable UUID id, @AuthenticationPrincipal User user) {

    Task task = findOwnedTask(id, user);

    return ResponseEntity.ok(TaskResponse.from(task));
  }

  @PostMapping
  public ResponseEntity<TaskResponse> createTask(
      @RequestBody @Valid TaskRequest request, @AuthenticationPrincipal User user) {

    if (taskRepository.existsByAssignedToAndTitle(user, request.title())) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    Task task = new Task();
    task.setTitle(request.title());
    task.setDescription(request.description());
    task.setStatus(request.status());
    task.setDeadline(request.deadline());
    task.setAssignedTo(user);

    taskRepository.save(task);

    return ResponseEntity.status(HttpStatus.CREATED).body(TaskResponse.from(task));
  }

  @PutMapping("/{id}")
  public ResponseEntity<TaskResponse> updateTask(
      @PathVariable UUID id,
      @RequestBody @Valid TaskRequest request,
      @AuthenticationPrincipal User user) {

    Task task = findOwnedTask(id, user);

    if (taskRepository.existsByAssignedToAndTitleAndIdNot(user, request.title(), id)) {
      return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    task.setTitle(request.title());
    task.setDescription(request.description());
    task.setStatus(request.status());
    task.setDeadline(request.deadline());

    taskRepository.save(task);

    return ResponseEntity.ok(TaskResponse.from(task));
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteTask(
      @PathVariable UUID id, @AuthenticationPrincipal User user) {
    Task task = findOwnedTask(id, user);

    taskRepository.delete(task);

    return ResponseEntity.noContent().build();
  }

  private Task findOwnedTask(UUID id, User user) {
    Task task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    if (!task.getAssignedTo().getId().equals(user.getId())) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND);
    }

    return task;
  }
}
