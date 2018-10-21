package vertx.chat.server.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.auth.KeyStoreOptions;
import io.vertx.ext.auth.jwt.JWTAuth;
import io.vertx.ext.auth.jwt.JWTAuthOptions;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.JWTAuthHandler;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.chat.server.ChatServerFactory;
import vertx.chat.server.HandlerFactory;

public class HttpServerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);
    private final String inBoundAdress = "chat.to.server";
    private final String outBoundAdress = "server.to.chat";
    HandlerFactory factory;
    private HttpServer httpServer;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        factory = new ChatServerFactory(null);
        SelfSignedCertificate certificate = SelfSignedCertificate.create();

        //todo pass through config file
        httpServer = vertx.createHttpServer(
                new HttpServerOptions()
                        .setSsl(true)
                        .setKeyCertOptions(certificate.keyCertOptions())
                        .setTrustOptions(certificate.trustOptions()));
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions bridgeOptions = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress(inBoundAdress))
                .addOutboundPermitted(new PermittedOptions().setAddress(outBoundAdress));
        sockJSHandler.bridge(bridgeOptions, event -> {
//      if (event.type() == BridgeEventType.PUBLISH)
//        publishEvent(event);
        });
        JWTAuth jwtAuth = JWTAuth.create(vertx, new JWTAuthOptions()
                .setKeyStore(new KeyStoreOptions()
                        .setPath("src/main/resources/keystore.jceks")
                        .setType("jceks")
                        .setPassword("secret")));

        Router router = Router.router(vertx);
        router.route().handler(JWTAuthHandler.create(jwtAuth, "/api/token"));
        router.get("/token").handler(context -> {
                });
        router.get("/*").handler(StaticHandler.create());
        router.route("/eventbus/*").handler(sockJSHandler);
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
    @Override
    public void stop(Future<Void> future) {
        httpServer.close(ar -> {
            if (ar.succeeded()) {
                LOGGER.info("HTTP server successfully stopped on port {}", config().getInteger("http.port"));
                future.complete();
            } else {
                future.fail(ar.cause());
            }
        });
    }
}
