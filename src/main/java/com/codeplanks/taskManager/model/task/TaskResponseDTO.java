package com.codeplanks.taskManager.model.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.Objects;

public class TaskResponseDTO {

  @JsonProperty(value = "id")
  private Integer id;

  @JsonProperty(value = "title")
  private String title;

  @JsonProperty(value = "description")
  private String description;

  @JsonProperty(value = "status")
  private Status status;

  @JsonProperty(value = "due_date")
  private LocalDateTime dueDate;

  @JsonProperty(value = "created_at")
  private LocalDateTime createdAt;

  @JsonProperty(value = "updated_at")
  private LocalDateTime updatedAt;

  public TaskResponseDTO(Task task) {
    this.id = task.getId();
    this.title = task.getTitle();
    this.description = task.getDescription();
    this.status = task.getStatus();
    this.dueDate = task.getDueDate();
    this.createdAt = task.getCreatedAt();
    this.updatedAt = task.getUpdatedAt();
  }

  public TaskResponseDTO(Integer id, TaskRequestDTO task, LocalDateTime createdAt, LocalDateTime updatedAt) {
    this.id = id;
    this.title = task.getTitle();
    this.description = task.getDescription();
    this.status = task.getStatus();
    this.dueDate = task.getDueDate();
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
}


  public Integer getId() {
    return id;
  }

  public String getTitle() {
    return title;
  }

  public String getDescription() {
    return description;
  }

  public Status getStatus() {
    return this.status;
  }

  public LocalDateTime getDueDate() {
    return this.dueDate;
  }

  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return this.updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    TaskResponseDTO task = (TaskResponseDTO) o;
    return id == task.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }

  @Override
  public String toString() {
    return (
      "Task{" +
      "id=" +
      id +
      ", title='" +
      title +
      '\'' +
      ", description='" +
      description +
      '\'' +
      ", status='" +
      status +
      '\'' +
      ", dueDate='" +
      dueDate +
      '\'' +
      ", createdAt='" +
      createdAt +
      '\'' +
      ", updatedAt=" +
      updatedAt +
      '}'
    );
  }
}
