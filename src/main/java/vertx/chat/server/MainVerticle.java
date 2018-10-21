package vertx.chat.server;

import com.google.gson.*;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.chat.server.http.HttpServerVerticle;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;

public class MainVerticle extends AbstractVerticle {
  private static final String SQL_CREATE_PAGES_TABLE = "CREATE TABLE IF NOT EXISTS Pages (Id int NOT NULL, Name varchar(255), Content LONGBLOB, UNIQUE(Name),PRIMARY KEY (Id))";
  private static final Logger LOGGER = LoggerFactory.getLogger(MainVerticle.class);
  //todo: pass through arguments
  private static final String pathToServerConfig = "src/main/resources/config.json";
  private SQLClient dbClient;
  private HttpServer httpServer;
  private final String inBoundAdress = "chat.to.server";
  private final String outBoundAdress = "chat.to.server";

  public static void main(String[] args) {
    new MainVerticle().start(Future.future());
  }
  @Override
  public void start(Future<Void> startFuture) {
    ConfigStoreOptions fileStore = new ConfigStoreOptions()
      .setType("file")
      .setConfig(new JsonObject().put("path", pathToServerConfig));
    ConfigRetrieverOptions options = new ConfigRetrieverOptions().addStore(fileStore);
    ConfigRetriever retriever = ConfigRetriever.create(vertx, options);
    retriever.getConfig(ar -> {
      if (ar.failed()) {
        LOGGER.error("Could not read configuration");
      } else {
        DeploymentOptions deploymentOptions = new DeploymentOptions();
        deploymentOptions.setConfig(ar.result());
        vertx.deployVerticle(HttpServerVerticle.class.getName(), deploymentOptions);
      }
    });
    startFuture.complete();

  }

  private Future<Void> prepareDatabase() {
    Future<Void> future = Future.future();
    JsonObject mySQLClientConfig = new JsonObject()
      .put("host", "localhost")
      .put("username", "root")
      .put("password", "root");
    dbClient = JDBCClient.createShared(vertx, mySQLClientConfig);
    dbClient.getConnection(ar -> {
      if (ar.failed()) {
        LOGGER.error("Could not open a database connection", ar.cause());
        future.fail(ar.cause());
      } else {
        SQLConnection connection = ar.result();
        connection.execute(SQL_CREATE_PAGES_TABLE, create -> {
          connection.close();
          if (create.failed()) {
            LOGGER.error("Database preparation error", create.cause());
            future.fail(create.cause());
          } else {
            future.complete();
          }
        });
      }
    });

    return future;
  }

  private boolean publishEvent(BridgeEvent event) {
    if (event.getRawMessage() != null
      && event.getRawMessage().getString("address").equals(inBoundAdress)) {
      String message = event.getRawMessage().getString("body");
      String host = event.socket().remoteAddress().host();
      int port = event.socket().remoteAddress().port();

      Map<String, Object> publicNotice = createPublicNotice(host, port, message);
      vertx.eventBus().publish("chat.to.client", new Gson().toJson(publicNotice));
      return true;
    } else
      return false;
  }

  private Map<String, Object> createPublicNotice(String host, int port, String message) {
    Date time = Calendar.getInstance().getTime();

    Map<String, Object> notice = new TreeMap<>();
    notice.put("type", "publish");
    notice.put("time", time.toString());
    notice.put("host", host);
    notice.put("port", port);
    notice.put("message", message);
    return notice;
  }

}
