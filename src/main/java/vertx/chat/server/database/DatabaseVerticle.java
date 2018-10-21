package vertx.chat.server.database;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.serviceproxy.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseVerticle extends AbstractVerticle {
    private JDBCClient dbClient;
    public static final String CONFIG_DB_JDBC_URL = "db.jdbc.url";
    public static final String CONFIG_DB_JDBC_DRIVER_CLASS = "db.jdbc.driver_class";
    public static final String CONFIG_DB_JDBC_MAX_POOL_SIZE = "db.jdbc.max_pool_size";
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseVerticle.class);
    public static final String CONFIG_DB_QUEUE = "db.queue";


    @Override
    public void start(Future<Void> startFuture) throws Exception {
        JsonObject mySQLClientConfig = new JsonObject()
                .put("user", "root")
                .put("password", "root")
                .put("url", config().getString(CONFIG_DB_JDBC_URL, "jdbc:mysql://localhost:3306/testdb"))
                .put("driver_class", config().getString(CONFIG_DB_JDBC_DRIVER_CLASS, "com.mysql.jdbc.Driver"))
                .put("max_pool_size", config().getInteger(CONFIG_DB_JDBC_MAX_POOL_SIZE, 30));
        dbClient = JDBCClient.createShared(vertx, mySQLClientConfig);
        DatabaseService.create(dbClient, ready -> {
            if (ready.succeeded()) {
                ServiceBinder binder = new ServiceBinder(vertx);
                binder
                        .setAddress(CONFIG_DB_QUEUE)
                        .register(DatabaseService.class, ready.result());
                startFuture.complete();
            } else {
                startFuture.fail(ready.cause());
            }
        });
    }
}
