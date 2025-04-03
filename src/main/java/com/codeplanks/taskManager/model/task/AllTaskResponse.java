package com.codeplanks.taskManager.model.task;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

public class AllTaskResponse implements Serializable {

  private static final long serialVersionUID = -8964658883487451260L;

  @JsonProperty(value = "total")
  private final int total;

  @JsonProperty(value = "limit")
  private final int limit;

  @JsonProperty(value = "page")
  private final int page;

  @JsonProperty(value = "tasks")
  private final List<TaskResponseDTO> tasks;

  public AllTaskResponse(int total, int limit, int page, List<TaskResponseDTO> tasks) {
    this.total = total;
    this.limit = limit;
    this.page = page;
    this.tasks = tasks;
  }

  public int getTotal() {
    return total;
  }

  public int getLimit() {
    return limit;
  }

  public int getPage() {
    return page;
  }

  public List<TaskResponseDTO> getTasks() {
    return tasks;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AllTaskResponse that = (AllTaskResponse) o;
    return (
      total == that.total &&
      limit == that.limit &&
      page == that.page &&
      tasks.equals(that.tasks)
    );
  }

  @Override
  public int hashCode() {
    return Objects.hash(total, limit, page, tasks);
  }

  @Override
  public String toString() {
    return (
      "AllTaskResponse{" +
      "total=" +
      total +
      ", limit=" +
      limit +
      ", page=" +
      page +
      ", tasks=" +
      tasks +
      '}'
    );
  }
}
