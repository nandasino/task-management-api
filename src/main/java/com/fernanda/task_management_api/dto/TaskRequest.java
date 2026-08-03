package com.fernanda.task_management_api.dto;

import com.fernanda.task_management_api.entity.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record TaskRequest(
    @NotBlank String title,
    String description,
    @NotNull TaskStatus status,
    @NotNull @FutureOrPresent LocalDate deadline) {}
