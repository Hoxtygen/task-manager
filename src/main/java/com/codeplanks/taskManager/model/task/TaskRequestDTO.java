package com.codeplanks.taskManager.model.task;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class TaskRequestDTO {

  private String title;
  private String description;
  private Status status;

  @JsonFormat(
    shape = JsonFormat.Shape.STRING,
    pattern = "yyyy-MM-dd'T'HH:mm:ss"
  )
  private LocalDateTime dueDate;

  public TaskRequestDTO() {}

  public TaskRequestDTO(
    String title,
    String description,
    Status status,
    LocalDateTime dueDate
  ) {
    this.title = title;
    this.description = description;
    this.status = status;
    this.dueDate = dueDate;
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
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }

  public LocalDateTime getDueDate() {
    return dueDate;
  }

  public void setDueDate(LocalDateTime dueDate) {
    this.dueDate = dueDate;
  }

  @Override
  public String toString() {
    return (
      "Task{" +
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
      '}'
    );
  }
}
