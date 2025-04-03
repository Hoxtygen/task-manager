package com.codeplanks.taskManager.repository;

import com.codeplanks.taskManager.model.task.Status;
import com.codeplanks.taskManager.model.task.Task;
import com.codeplanks.taskManager.model.task.TaskRequestDTO;
import com.codeplanks.taskManager.model.task.TaskResponseDTO;
import com.codeplanks.taskManager.utils.LogUtils;
import io.vertx.core.Future;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Tuple;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRepository {

  private static final Logger logger = LoggerFactory.getLogger(
    TaskRepository.class
  );

  private static final String SQL_SELECT_ALL =
    "SELECT id, title, description, status, due_date, created_at, updated_at FROM tasks LIMIT $1 OFFSET $2";

  private static final String SQL_SELECT_BY_ID =
    "SELECT id, title, description, status, due_date, created_at, updated_at FROM tasks WHERE id = $1";

  private static final String SQL_INSERT =
    "INSERT INTO tasks (title, description, status, due_date) " +
    "VALUES ($1, $2, $3, $4) RETURNING id, title, description, status, due_date, created_at, updated_at";

  private static final String SQL_UPDATE =
    "UPDATE tasks SET ... WHERE id = @id";

  private static final String SQL_DELETE = "DELETE FROM tasks WHERE id = $1";

  private static final String SQL_COUNT = "SELECT COUNT(*) AS total FROM tasks";

  public TaskRepository() {}

  /**
   * Get all tasks using pagination
   * @param connection
   * @param limit
   * @param offset
   * @return
   */
  public Future<List<Task>> getAllTasks(
    SqlConnection connection,
    int limit,
    int offset
  ) {
    return connection
      .preparedQuery(SQL_SELECT_ALL)
      .execute(Tuple.of(limit, offset))
      .map(rowSet -> {
        final List<Task> tasks = new ArrayList<Task>();
        for (Row row : rowSet) {
          Task task = new Task();
          task.setId(row.getInteger("id"));
          task.setTitle(row.getString("title"));
          task.setDescription(row.getString("description"));
          task.setStatus(Status.valueOf(row.getString("status")));
          task.setDueDate(row.getLocalDateTime("due_date"));
          task.setCreatedAt(row.getLocalDateTime("created_at"));
          task.setUpdatedAt(row.getLocalDateTime("updated_at"));
          tasks.add(task);
        }
        return tasks;
      });
  }

  public Future<Integer> count(SqlConnection connection) {
    return connection
      .preparedQuery(SQL_COUNT)
      .execute()
      .map(rowSet -> {
        if (rowSet.iterator().hasNext()) {
          Row row = rowSet.iterator().next();
          return row.getInteger("total");
        } else {
          return 0;
        }
      });
  }

  public Future<Task> getByTaskId(SqlConnection connection, int taskId) {
    return connection
      .preparedQuery(SQL_SELECT_BY_ID)
      .execute(Tuple.of(taskId))
      .map(rowSet -> {
        final RowIterator<Row> iterator = rowSet.iterator();
        if (iterator.hasNext()) {
          Row row = iterator.next();
          Task task = new Task();
          task.setId(row.getInteger("id"));
          task.setTitle(row.getString("title"));
          task.setDescription(row.getString("description"));
          task.setStatus(Status.valueOf(row.getString("status")));
          task.setDueDate(row.getLocalDateTime("due_date"));
          task.setCreatedAt(row.getLocalDateTime("created_at"));
          task.setUpdatedAt(row.getLocalDateTime("updated_at"));
          return task;
        } else {
          throw new NoSuchElementException(
            LogUtils.NO_TASK_WITH_ID_MESSAGE.buildMessage(taskId)
          );
        }
      });
  }

  public Future<TaskResponseDTO> insert(
    SqlConnection connection,
    TaskRequestDTO task
  ) {
    return connection
      .preparedQuery(SQL_INSERT)
      .execute(
        Tuple.of(
          task.getTitle(),
          task.getDescription(),
          task.getStatus().toString(),
          task.getDueDate()
        )
      )
      .map(rowSet -> {
        final RowIterator<Row> iterator = rowSet.iterator();
        System.out.println("iterator:" + iterator.toString());
        if (iterator.hasNext()) {
          Row row = iterator.next();
          return new TaskResponseDTO(
            row.getInteger("id"),
            task,
            row.getLocalDateTime("created_at"),
            row.getLocalDateTime("updated_at")
          );
        } else {
          throw new IllegalStateException(
            LogUtils.CANNOT_CREATE_TASK_MESSAGE.buildMessage(null)
          );
        }
      });
  }

  public Future<Void> deleteTask(SqlConnection connection, int taskId) {
    return connection
      .preparedQuery(SQL_DELETE)
      .execute(Tuple.of(taskId))
      .flatMap(rowSet -> {
        if (rowSet.rowCount() > 0) {
          logger.info(
            LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage(
              "Delete task",
              "Task Id",
              SQL_DELETE
            )
          );
          return Future.succeededFuture();
        } else {
          String errorMessage = LogUtils.NO_TASK_WITH_ID_MESSAGE.buildMessage(
            taskId
          );

          logger.error(
            LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage(
              "Delete task",
              errorMessage
            )
          );
           return Future.failedFuture(new NoSuchElementException(errorMessage));
        }
      });
  }
}
