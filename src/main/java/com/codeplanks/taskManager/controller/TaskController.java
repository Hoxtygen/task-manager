package com.codeplanks.taskManager.controller;

import com.codeplanks.taskManager.model.task.AllTaskResponse;
import com.codeplanks.taskManager.model.task.TaskRequestDTO;
import com.codeplanks.taskManager.model.task.TaskResponseDTO;
import com.codeplanks.taskManager.service.TaskService;
import com.codeplanks.taskManager.utils.ResponseUtils;
import io.vertx.core.Future;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskController {

  private static final Logger logger = LoggerFactory.getLogger(
    TaskController.class
  );

  private static final String ID_PARAMETER = "taskId";
  private static final String PAGE_PARAMETER = "page";
  private static final String LIMIT_PARAMETER = "limit";
  private final TaskService taskService;

  public TaskController(TaskService taskService) {
    this.taskService = taskService;
  }

  /**
   * Get all tasks
   * It should return 200 OK when successful
   * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
   *
   * @param routingContext
   * @return TasksResponse
   */
  public Future<AllTaskResponse> getAllTasks(RoutingContext routingContext) {
    final String page = routingContext.queryParams().get(PAGE_PARAMETER);
    final String limit = routingContext.queryParams().get(LIMIT_PARAMETER);
    return taskService
      .getAllTasks(page, limit)
      .onSuccess(success ->
        ResponseUtils.buildOkResponse(routingContext, success)
      )
      .onFailure(throwable ->
        ResponseUtils.buildErrorResponse(routingContext, throwable)
      );
  }

  /**
   * Get  task by Id
   * It should return 200 OK when successful
   * It should return 400 Bad Request, 404 Not Found or 500 Internal Server Error in case of failure
   *
   * @param routingContext
   * @return {@link TaskResponseDTO}
   */
  public Future<TaskResponseDTO> getTaskById(RoutingContext context) {
    final String taskId = context.pathParam(ID_PARAMETER);

    return taskService
      .getTaskById(Integer.parseInt(taskId))
      .onSuccess(success -> ResponseUtils.buildOkResponse(context, success))
      .onFailure(throwable ->
        ResponseUtils.buildErrorResponse(context, throwable)
      );
  }

  public Future<TaskResponseDTO> createNewTask(RoutingContext context) {
    final TaskRequestDTO requestDTO = context
      .body()
      .asJsonObject()
      .mapTo(TaskRequestDTO.class);

    return taskService
      .createTask(requestDTO)
      .onSuccess(success -> ResponseUtils.buildCreatedResponse(context, success)
      )
      .onFailure(throwable ->
        ResponseUtils.buildErrorResponse(context, throwable)
      );
  }

  public Future<Void> deleteTask(RoutingContext context) {
    final String taskId = context.pathParam("taskId");
    return taskService
      .deleteTask(Integer.parseInt(taskId))
      .onSuccess(success ->
        ResponseUtils.buildDeletedSuccessResponse(context, success)
      )
      .onFailure(throwable ->
        ResponseUtils.buildErrorResponse(context, throwable)
      );
  }
}
