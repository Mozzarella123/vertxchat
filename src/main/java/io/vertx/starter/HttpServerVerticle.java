package io.vertx.starter;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpServerVerticle extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
  private final String inBoundAdress = "chat.to.server";
  private final String outBoundAdress = "server.to.chat";
  HandlerFactory factory;
  private HttpServer httpServer;

  @Override
  public void start(Future<Void> startFuture) throws Exception {
    factory = new ChatServerFactory(null);
    httpServer = vertx.createHttpServer();
    SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
    BridgeOptions bridgeOptions = new BridgeOptions()
      .addInboundPermitted(new PermittedOptions().setAddress(inBoundAdress))
      .addOutboundPermitted(new PermittedOptions().setAddress(outBoundAdress));
    sockJSHandler.bridge(bridgeOptions, event -> {
//      if (event.type() == BridgeEventType.PUBLISH)
//        publishEvent(event);
    });
    httpServer = vertx.createHttpServer();
    Router router = Router.router(vertx);
    router.get("/*").handler(StaticHandler.create().setCachingEnabled(false));
    router.route("/eventbus/*").handler(sockJSHandler);
    LOGGER.info(config().toString());
    httpServer
      .requestHandler(router::accept)
      .listen(config().getInteger("http.port"), ar -> {
        if (ar.succeeded()) {
          LOGGER.info("HTTP server running on port {}", config().getInteger("http.port"));
          startFuture.complete();
        } else {

          LOGGER.error("Could not start a HTTP server", ar.cause());
          startFuture.fail(ar.cause());
        }
      });
  }
}
