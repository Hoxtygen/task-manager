package com.codeplanks.taskManager.utils;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import java.util.NoSuchElementException;

public class ResponseUtils {

  private static final String CONTENT_TYPE_HEADER = "Content-Type";
  private static final String APPLICATION_JSON = "application/json";

  private ResponseUtils() {}

  /**
   * Build success response using 200 OK as its status code and response as its body
   *
   * @param rc       Routing context
   * @param response Response body
   */
  public static void buildOkResponse(RoutingContext rc, Object response) {
    JsonObject jsonResponseObject = new JsonObject()
      .put("status", 200)
      .put("message", "Data fetched successfully")
      .put("data", JsonObject.mapFrom(response));
    rc
      .response()
      .setStatusCode(200)
      .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
      .end(jsonResponseObject.encodePrettily());
  }

  /**
   * Build success response using 201 Created as its status code and response as its body
   *
   * @param rc       Routing context
   * @param response Response body
   */
  public static void buildCreatedResponse(RoutingContext rc, Object response) {
    JsonObject jsonResponse = new JsonObject()
      .put("status", 201)
      .put("message", "Resource created successfully")
      .put("data", JsonObject.mapFrom(response));
    rc
      .response()
      .setStatusCode(201)
      .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
      .end(jsonResponse.encodePrettily());
  }

  /**
   * Build success response using 204 No Content as its status code and no body
   *
   * @param rc Routing context
   */
  public static void buildNoContentResponse(RoutingContext rc) {
    rc.response().setStatusCode(204).end();
  }

  /**
   * Build success response using 200  as its status code
   * when a resource is deleted successfully
   *
   * @param rc Routing context
   */
  public static void buildDeletedSuccessResponse(
    RoutingContext rc,
    Object response
  ) {
    JsonObject jsonResponse = new JsonObject()
      .put("status", 200)
      .put("message", "Item deleted successfully")
      .put("data", JsonObject.mapFrom(response));

    rc
      .response()
      .setStatusCode(200)
      .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
      .end(jsonResponse.encodePrettily());
  }

  /**
   * Build error response using 400 Bad Request, 404 Not Found or 500 Internal Server Error
   * as its status code and throwable as its body
   *
   * @param rc        Routing context
   * @param throwable Throwable
   */
  public static void buildErrorResponse(
    RoutingContext rc,
    Throwable throwable
  ) {
    final int status;
    final String message;
    if (
      throwable instanceof IllegalArgumentException ||
      throwable instanceof IllegalStateException ||
      throwable instanceof NullPointerException
    ) {
      // Bad Request
      status = 400;
      message = throwable.getMessage();
    } else if (throwable instanceof NoSuchElementException) {
      // Not Found
      status = 404;
      message = throwable.getMessage();
    } else {
      // Internal Server Error
      status = 500;
      message = "Internal Server Error";
    }

    JsonObject errorResponse = new JsonObject()
      .put("status", status)
      .put("message", message)
      .put("error", throwable.getMessage());
    System.out.println("Throwable:" + throwable.getStackTrace().toString());

    rc
      .response()
      .setStatusCode(status)
      .putHeader(CONTENT_TYPE_HEADER, APPLICATION_JSON)
      .end(errorResponse.encodePrettily());
  }
}
