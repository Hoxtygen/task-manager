package com.codeplanks.taskManager.router;

import com.codeplanks.taskManager.controller.TaskController;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.LoggerFormat;
import io.vertx.ext.web.handler.LoggerHandler;

public class TaskRouter {

  private final Vertx vertx;
  private final TaskController taskController;

  public TaskRouter(Vertx vertx, TaskController taskController) {
    this.vertx = vertx;
    this.taskController = taskController;
  }

  public void setRouter(Router mainRouter) {
    mainRouter.mountSubRouter("/api/v1", buildTaskRouter());
  }

  private Router buildTaskRouter() {
    final Router taskRouter = Router.router(vertx);

    taskRouter
      .route("/tasks*")
      .handler(LoggerHandler.create(LoggerFormat.DEFAULT));
    taskRouter.route("/tasks*").handler(BodyHandler.create());
    taskRouter
      .get("/tasks")
      .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
      .handler(taskController::getAllTasks);
    taskRouter
      .get("/tasks/:taskId")
      .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
      .handler(taskController::getTaskById);

    taskRouter
      .post("/tasks")
      .handler(LoggerHandler.create(LoggerFormat.DEFAULT))
      .handler(taskController::createNewTask);

    return taskRouter;
  }
}
