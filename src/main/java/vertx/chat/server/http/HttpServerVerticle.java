package vertx.chat.server.http;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerOptions;
import io.vertx.core.json.JsonObject;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.chat.server.database.DatabaseService;
import vertx.chat.server.http.routers.ChatRouter;
import vertx.chat.server.http.routers.UserRouter;

import static vertx.chat.server.database.DatabaseVerticle.*;

public class HttpServerVerticle extends AbstractVerticle {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpServerVerticle.class);

    private HttpServer httpServer;
    private DatabaseService dbService;

    @Override
    public void start(Future<Void> startFuture) throws Exception {
        SelfSignedCertificate certificate = SelfSignedCertificate.create();
        String wikiDbQueue = config().getString(CONFIG_DB_QUEUE, "db.queue");
        dbService = DatabaseService.createProxy(vertx, wikiDbQueue);
        //todo pass through config file
        httpServer = vertx.createHttpServer(
                new HttpServerOptions()
                        .setSsl(true)
                        .setKeyCertOptions(certificate.keyCertOptions())
                        .setTrustOptions(certificate.trustOptions()));


        JDBCClient dbClient = JDBCClient.createShared(vertx,
                new JsonObject().put("url", config().getString(CONFIG_DB_JDBC_URL, "jdbc:mysql://localhost:3306/testdb"))
                .put("driver_class", config().getString(CONFIG_DB_JDBC_DRIVER_CLASS, "com.mysql.jdbc.Driver"))
                .put("max_pool_size", config().getInteger(CONFIG_DB_JDBC_MAX_POOL_SIZE, 30)));
        JDBCAuth auth = JDBCAuth.create(vertx, dbClient);
        Router router = Router.router(vertx);
        UserRouter userRouter = new UserRouter(router, vertx, auth, dbService);
        ChatRouter chatRouter = new ChatRouter(router, vertx);
        StaticHandler staticHandler =  StaticHandler.create().setCachingEnabled(false);
        router.get("/*").handler(staticHandler);
        router.get("/login").handler(staticHandler);
        router.get("/register").handler(staticHandler);
        router.get("/account").handler((context)-> {
            context.response().putHeader("Content-type","text/html");
            context.response().putHeader("Content-Length","1000");
            context.response().write(context.user().principal().toString());
            context.response().end();

        });

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
