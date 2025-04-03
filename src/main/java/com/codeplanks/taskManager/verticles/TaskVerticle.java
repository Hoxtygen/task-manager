package com.codeplanks.taskManager.verticles;

import com.codeplanks.taskManager.controller.TaskController;
import com.codeplanks.taskManager.repository.TaskRepository;
import com.codeplanks.taskManager.router.TaskRouter;
import com.codeplanks.taskManager.service.TaskService;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.sqlclient.Pool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TaskVerticle extends AbstractVerticle {

  private final TaskRouter taskRouter;
  private final Pool dbPool;

  private static final Logger logger = LoggerFactory.getLogger(
    TaskVerticle.class
  );

  public TaskVerticle(Pool dbPool, TaskRepository taskRepository) {
    if (dbPool == null) {
      throw new IllegalArgumentException("Database pool cannot be null");
    }
    this.dbPool = dbPool;
    TaskService taskService = new TaskService(dbPool, taskRepository);
    TaskController taskController = new TaskController(taskService);
    this.taskRouter = new TaskRouter(vertx, taskController);
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    try {
      logger.info("TaskVerticle started successfully");
      startPromise.complete();
  } catch (Exception e) {
      logger.error("Error starting TaskVerticle", e);
      startPromise.fail(e);
  }
  }

  public void setRoute(Router mainRouter) {
    taskRouter.setRouter(mainRouter);
  }
}
