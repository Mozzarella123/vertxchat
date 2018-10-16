package io.vertx.starter;

import java.util.Map;

public class ChatServerFactory extends HandlerFactory {
  Map<String, IHandler> handlers;

  public ChatServerFactory(Map<String, IHandler> handlers) {
    this.handlers = handlers;
  }

  @Override
  IHandler getHandler(String name) {
    return handlers.get(name);
  }
}
