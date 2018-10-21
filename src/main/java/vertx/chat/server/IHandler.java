package vertx.chat.server;

public interface IHandler {
  boolean handle(Message message);
}
