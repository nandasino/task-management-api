package com.fernanda.task_management_api.repository;

import com.fernanda.task_management_api.entity.Task;
import com.fernanda.task_management_api.entity.TaskStatus;
import com.fernanda.task_management_api.entity.User;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaskRepository extends JpaRepository<Task, UUID> {

  Page<Task> findByAssignedTo(User assignedTo, Pageable pageable);

  Page<Task> findByAssignedToAndStatus(User assignedTo, TaskStatus status, Pageable pageable);
}
