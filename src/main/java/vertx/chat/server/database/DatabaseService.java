package vertx.chat.server.database;

import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;

@ProxyGen
@VertxGen
public interface DatabaseService {
    static DatabaseService create(JDBCClient dbClient, Handler<AsyncResult<DatabaseService>> readyHandler) {
        return new DatabaseServiceImpl(dbClient, readyHandler);
    }
    @Fluent
    DatabaseService fetchUser(String id,Handler<AsyncResult<JsonObject>> resultHandler);

    static DatabaseService createProxy(Vertx vertx,
                                       String address) {
        return new DatabaseServiceVertxEBProxy(vertx, address);
    }
}
