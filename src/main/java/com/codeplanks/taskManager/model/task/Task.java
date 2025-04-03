package com.codeplanks.taskManager.model.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

public class Task implements Serializable {

  private static final long serialVersionUID = 1169010391380979103L;

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



  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Status getStatus() {
    return this.status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public LocalDateTime getDueDate() {
    return this.dueDate;
  }

  public void setDueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
  }

  public LocalDateTime getCreatedAt() {
    return this.createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return this.updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Task task = (Task) o;
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
