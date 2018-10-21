package vertx.chat.server;

public abstract class HandlerFactory {
  abstract IHandler getHandler(String name);
}
