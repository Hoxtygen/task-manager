package com.codeplanks.taskManager.repository;

import com.codeplanks.taskManager.model.task.Task;
import com.codeplanks.taskManager.utils.LogUtils;
import io.vertx.core.Future;
import io.vertx.sqlclient.RowIterator;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.templates.RowMapper;
import io.vertx.sqlclient.templates.SqlTemplate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskRepository {

  private static final Logger logger = LoggerFactory.getLogger(
    TaskRepository.class
  );

  // private static final String SQL_SELECT_ALL =
  //   "SELECT * FROM tasks LIMIT @limit OFFSET @offset";
  private static final String SQL_SELECT_ALL =
    "SELECT * FROM tasks LIMIT #{limit} OFFSET #{offset}";
  private static final String SQL_SELECT_BY_ID =
    "SELECT * FROM tasks WHERE id = #{id}";
  private static final String SQL_INSERT =
    "INSERT INTO tasks (...) VALUES (...) RETURNING id";
  private static final String SQL_UPDATE =
    "UPDATE tasks SET ... WHERE id = @id";
  private static final String SQL_DELETE = "DELETE FROM tasks WHERE id = @id";
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
    return SqlTemplate
      .forQuery(connection, SQL_SELECT_ALL)
      .mapTo(Task.class)
      .execute(Map.of("limit", limit, "offset", offset))
      .map(rowSet -> {
        final List<Task> tasks = new ArrayList<Task>();
        rowSet.forEach(tasks::add);
        return tasks;
      });
  }

  public Future<Integer> count(SqlConnection connection) {
    final RowMapper<Integer> ROW_MAPPER = row -> row.getInteger("total");

    return SqlTemplate
      .forQuery(connection, SQL_COUNT)
      .mapTo(ROW_MAPPER)
      .execute(Collections.emptyMap())
      .map(rowSet -> rowSet.iterator().hasNext() ? rowSet.iterator().next() : 0
      );
  }

 public Future<Task> getByTaskId(SqlConnection connection, int taskId) {
    return SqlTemplate
      .forQuery(connection, SQL_SELECT_BY_ID)
      .mapTo(Task.class)
      .execute(Collections.singletonMap("id", taskId))
      .map(rowSet -> {
        final RowIterator<Task> iterator = rowSet.iterator();
        if (iterator.hasNext()) {
          return iterator.next();
        } else {
          throw new NoSuchElementException(
            LogUtils.NO_BOOK_WITH_ID_MESSAGE.buildMessage(taskId)
          );
        }
      })
      .onSuccess(success ->
        logger.info(
          LogUtils.REGULAR_CALL_SUCCESS_MESSAGE.buildMessage(
            "Get task by id",
            SQL_SELECT_BY_ID
          )
        )
      )
      .onFailure(err ->
        logger.error(
          LogUtils.REGULAR_CALL_ERROR_MESSAGE.buildMessage(
            "Get book by id",
            err.getMessage()
          )
        )
      );
  }
}
