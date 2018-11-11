package vertx.chat.server;

import com.google.gson.Gson;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.sql.SQLClient;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.chat.server.database.DatabaseVerticle;
import vertx.chat.server.http.AuthInitializerVerticle;
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
        Future<String> dbVerticleDeployment = Future.future();
        vertx.deployVerticle(new DatabaseVerticle(), dbVerticleDeployment);

        dbVerticleDeployment.compose(id -> {
          Future<String> authInitDeployment = Future.future();
          vertx.deployVerticle(new AuthInitializerVerticle(), authInitDeployment);
          return authInitDeployment;
        }).compose(id -> {
          Future<String> httpVerticleDeployment = Future.future();
          vertx.deployVerticle(
                  HttpServerVerticle.class.getName(),
                  deploymentOptions.setInstances(2),
                  httpVerticleDeployment.completer());
          return httpVerticleDeployment;
        }).setHandler(ares -> {
          if (ares.succeeded()) {
            startFuture.complete();
          } else {
            startFuture.fail(ares.cause());
          }
        });

      }
    });

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
