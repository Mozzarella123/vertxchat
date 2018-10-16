package io.vertx.starter;

public abstract class HandlerFactory {
  abstract IHandler getHandler(String name);
}
