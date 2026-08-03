-- Script de criacao do banco de dados para o Sistema de Gerenciamento de Tarefas
-- Compativel com MySQL 8+

CREATE DATABASE IF NOT EXISTS task_manager_system
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;

USE task_manager_system;

CREATE TABLE IF NOT EXISTS users (
  id CHAR(36) NOT NULL,
  username VARCHAR(255) NOT NULL,
  email VARCHAR(255) NOT NULL,
  password VARCHAR(255) NOT NULL,
  PRIMARY KEY (id),
  UNIQUE KEY uk_users_email (email)
);

CREATE TABLE IF NOT EXISTS tasks (
  id CHAR(36) NOT NULL,
  title VARCHAR(255) NOT NULL,
  description TEXT,
  status VARCHAR(20) NOT NULL,
  created_on DATE NOT NULL,
  deadline DATE NOT NULL,
  assigned_to CHAR(36) NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT fk_tasks_assigned_to FOREIGN KEY (assigned_to) REFERENCES users (id),
  CONSTRAINT chk_tasks_status CHECK (status IN ('PENDING', 'IN_PROGRESS', 'COMPLETED'))
);

CREATE INDEX idx_tasks_assigned_to ON tasks (assigned_to);
CREATE INDEX idx_tasks_deadline ON tasks (deadline);
