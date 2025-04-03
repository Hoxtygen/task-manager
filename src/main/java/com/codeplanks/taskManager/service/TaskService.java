package com.codeplanks.taskManager.service;

import com.codeplanks.taskManager.model.task.AllTaskResponse;
import com.codeplanks.taskManager.model.task.TaskRequestDTO;
import com.codeplanks.taskManager.model.task.TaskResponseDTO;
import com.codeplanks.taskManager.repository.TaskRepository;
import com.codeplanks.taskManager.utils.LogUtils;
import com.codeplanks.taskManager.utils.QueryUtils;
import io.vertx.core.Future;
import io.vertx.sqlclient.Pool;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskService {

  private static final Logger logger = LoggerFactory.getLogger(
    TaskService.class
  );

  private final Pool dbClient;
  private final TaskRepository taskRepository;

  public TaskService(Pool dbClientPool, TaskRepository taskRepository) {
    this.dbClient = dbClientPool;
    this.taskRepository = taskRepository;
  }

  /**
   * Get all tasks
   *
   * @param page
   * @param limit
   * @return {@link AllTaskResponse}
   */
  public Future<AllTaskResponse> getAllTasks(String page, String limit) {
    return dbClient
      .withTransaction(connection -> {
        final int pageNumber = QueryUtils.getPage(page);
        final int limitNumber = QueryUtils.getLimit(limit);
        final int offset = QueryUtils.getOffset(pageNumber, limitNumber);
        return taskRepository
          .count(connection)
          .flatMap(total ->
            taskRepository
              .getAllTasks(connection, limitNumber, offset)
              .map(result -> {
                final List<TaskResponseDTO> tasks = result
                  .stream()
                  .map(TaskResponseDTO::new)
                  .collect(Collectors.toList());

                return new AllTaskResponse(
                  total,
                  limitNumber,
                  pageNumber,
                  tasks
                );
              })
          );
      })
      .onSuccess(success ->
        logger.info(
          LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage(
            "Get all tasks",
            success.getTasks()
          )
        )
      )
      .onFailure(failure ->
        logger.error(
          LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage(
            "Get all tasks",
            failure.getMessage()
          )
        )
      );
  }

  /**
   * Get task by id
   *
   * @param taskId
   * @return {@link TaskResponseDTO}
   */
  public Future<TaskResponseDTO> getTaskById(int taskId) {
    return dbClient
      .withTransaction(connection -> {
        return taskRepository
          .getByTaskId(connection, taskId)
          .map(TaskResponseDTO::new);
      })
      .onSuccess(success ->
        logger.info(
          LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage(
            "Get one task",
            success
          )
        )
      )
      .onFailure(throwable ->
        logger.error(
          LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage(
            "Get one task",
            throwable.getMessage()
          )
        )
      );
  }

  public Future<TaskResponseDTO> createTask(TaskRequestDTO taskRequestDTO) {
    return dbClient
      .withTransaction(connection ->
        taskRepository.insert(connection, taskRequestDTO)
      )
      .onSuccess(success ->
        logger.info(
          LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage(
            "Insert task",
            success
          )
        )
      )
      .onFailure(throwable ->
        logger.error(
          LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage(
            "Insert task",
            throwable.getMessage()
          )
        )
      );
  }

  public Future<Void> deleteTask(int taskId) {
    return dbClient.withTransaction(connection -> {
      return taskRepository
        .deleteTask(connection, taskId)
        .onSuccess(succes ->
          logger.info(
            LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage(
              "Delete a task",
              taskId
            )
          )
        )
        .onFailure(throwable ->
          logger.error(
            LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage(
              "Delete a task",
              throwable.getMessage()
            )
          )
        );
    });
  }
}
