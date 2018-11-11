
package vertx.chat.server.database;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.ResultSet;
import io.vertx.ext.sql.SQLConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseServiceImpl implements DatabaseService {
    private final JDBCClient dbClient;
    private static final Logger LOGGER = LoggerFactory.getLogger(DatabaseServiceImpl.class);
    DatabaseServiceImpl(JDBCClient dbClient, Handler<AsyncResult<DatabaseService>> readyHandler) {
        this.dbClient = dbClient;
        dbClient.getConnection(ar -> {
            if (ar.failed()) {
                LOGGER.error("Could not open a database connection", ar.cause());
                readyHandler.handle(Future.failedFuture(ar.cause()));
            } else {
                //todo SQL queries
                SQLConnection connection = ar.result();
                connection.execute("CREATE TABLE IF NOT EXISTS messages\n" +
                        "(\n" +
                        "  content varchar(255) null,\n" +
                        "  id      int auto_increment,\n" +
                        "  `from`  int          null,\n" +
                        "  `to`    int          null,\n" +
                        "  constraint messages_id_uindex\n" +
                        "  unique (id),\n" +
                        "  constraint messages_user_id_fk\n" +
                        "  foreign key (`from`) references user (id),\n" +
                        "  constraint messages_user_id_fk_2\n" +
                        "  foreign key (`to`) references user (id)\n" +
                        ");", create -> {
                    connection.close();
                    if (create.failed()) {
                        LOGGER.error("Database preparation error", create.cause());
                        readyHandler.handle(Future.failedFuture(create.cause()));
                    } else {
                        readyHandler.handle(Future.succeededFuture(this));
                    }
                });
            }
        });
    }

    @Override
    public DatabaseService fetchUser(String id, Handler<AsyncResult<JsonObject>> resultHandler) {
        dbClient.query("",res-> {
           if (res.succeeded()) {
               JsonObject response = new JsonObject();
               ResultSet resultSet = res.result();
               if (resultSet.getNumRows() == 0) {
                   response.put("found", false);
               }
               else {
                   response.put("found",true);
               }
               resultHandler.handle(Future.succeededFuture(response));
           }
           else {
               LOGGER.error("Database query error", res.cause());
           }
        });
        return this;
    }

    @Override
    public DatabaseService createUser(String username, String password, String email) {

        return this;
    }
}