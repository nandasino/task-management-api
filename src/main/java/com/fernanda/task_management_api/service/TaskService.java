package com.fernanda.task_management_api.service;

import com.fernanda.task_management_api.dto.TaskRequest;
import com.fernanda.task_management_api.dto.TaskResponse;
import com.fernanda.task_management_api.entity.Task;
import com.fernanda.task_management_api.entity.TaskStatus;
import com.fernanda.task_management_api.entity.User;
import com.fernanda.task_management_api.exception.DuplicateResourceException;
import com.fernanda.task_management_api.exception.ResourceNotFoundException;
import com.fernanda.task_management_api.repository.TaskRepository;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TaskService {

  private final TaskRepository taskRepository;

  public TaskService(TaskRepository taskRepository) {
    this.taskRepository = taskRepository;
  }

  public Page<TaskResponse> listTasks(User user, Pageable pageable) {
    return taskRepository.findByAssignedTo(user, pageable).map(TaskResponse::from);
  }

  public Page<TaskResponse> filterTasks(User user, TaskStatus status, Pageable pageable) {
    return taskRepository
        .findByAssignedToAndStatus(user, status, pageable)
        .map(TaskResponse::from);
  }

  public TaskResponse getTask(UUID id, User user) {
    return TaskResponse.from(findOwnedTask(id, user));
  }

  public TaskResponse createTask(TaskRequest request, User user) {
    if (taskRepository.existsByAssignedToAndTitle(user, request.title())) {
      throw new DuplicateResourceException("A task with this title already exists");
    }

    Task task = new Task();
    task.setTitle(request.title());
    task.setDescription(request.description());
    task.setStatus(request.status());
    task.setDeadline(request.deadline());
    task.setAssignedTo(user);

    return TaskResponse.from(taskRepository.save(task));
  }

  public TaskResponse updateTask(UUID id, TaskRequest request, User user) {
    Task task = findOwnedTask(id, user);

    if (taskRepository.existsByAssignedToAndTitleAndIdNot(user, request.title(), id)) {
      throw new DuplicateResourceException("A task with this title already exists");
    }

    task.setTitle(request.title());
    task.setDescription(request.description());
    task.setStatus(request.status());
    task.setDeadline(request.deadline());

    return TaskResponse.from(taskRepository.save(task));
  }

  public void deleteTask(UUID id, User user) {
    taskRepository.delete(findOwnedTask(id, user));
  }

  private Task findOwnedTask(UUID id, User user) {
    Task task =
        taskRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Task not found"));

    if (!task.getAssignedTo().getId().equals(user.getId())) {
      throw new ResourceNotFoundException("Task not found");
    }

    return task;
  }
}
