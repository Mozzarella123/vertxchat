package vertx.chat.server.http.routers;

import com.google.gson.Gson;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.User;
import io.vertx.ext.bridge.PermittedOptions;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.sockjs.BridgeEvent;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ChatRouter {
    private final Vertx vertx;
    private final String inBoundAdress = "client.to.chat";
    private final String outBoundAdress = "chat.to.client";
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatRouter.class);
    private final Gson gson = new Gson();
    private final Map<String, User> users;

    public ChatRouter(Router router, Vertx vertx) {
        this.vertx = vertx;
        this.users = new HashMap<>();
        router.get("/chat/token").handler(this::getToken);
        router.route("/chat/users").handler(this::getUsers);
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions bridgeOptions = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress(inBoundAdress))
                .addOutboundPermitted(new PermittedOptions().setAddress(outBoundAdress))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("user\\..+"))
                .addInboundPermitted(new PermittedOptions().setAddressRegex("user\\..+"));

        sockJSHandler.bridge(bridgeOptions, event -> {
            switch (event.type()) {
                case SEND:
                    this.publishEvent(event);
                    break;
                case REGISTER:
                    this.registerEvent(event);
                    break;
                case SOCKET_CLOSED:
                    this.closeEvent(event);
                    break;
            }
            event.complete(true);
        });
        vertx.eventBus().consumer(inBoundAdress, msg -> {
            switch (msg.headers().get("action")) {
                case "users":
                    msg.reply(gson.toJson(this.users.keySet()));
                    break;
            }
        });
        router.route("/eventbus/*").handler(sockJSHandler);
    }

    private void closeEvent(BridgeEvent event) {
        User user = event.socket().webUser();
        String username = user.principal().getString("name");
        LOGGER.info("user " + username + " left");
//        this.vertx.eventBus().publish(outBoundAdress, new Gson().toJson());
    }

    private void registerEvent(BridgeEvent event) {
        User user = event.socket().webUser();
        String username = user.principal().getString("username");
        if (event.getRawMessage().getString("address").equals(outBoundAdress)) {
            LOGGER.info("user " + username + " connected");
            this.users.put(username, user);
            Map<String, Object> notice = new TreeMap<>();
            notice.put("type", "connect");
            notice.put("user", username);
            this.vertx.eventBus().publish(outBoundAdress, gson.toJson(notice));
        }

    }

    private void publishEvent(BridgeEvent event) {
        if (event.getRawMessage().getJsonObject("headers").getString("action").equals("message")) {
            JsonObject body = event.getRawMessage().getJsonObject("body");
            Map<String, Object> notice = new TreeMap<>();
            notice.put("from", event.socket().webUser().principal().getString("username"));
            notice.put("type", "message");
            notice.put("body", body.getString("body"));
            this.vertx.eventBus().publish("user." + body.getString("to"), gson.toJson(notice));

        }
    }

    public void getToken(RoutingContext context) {
        String token = context.user().principal().getString("username");
        context.response().headers().set("Content-type", "text/plain");
        context.response().headers().set("Content-length", token.length() + "");
        context.response().write(token);
    }

    public void getUsers(RoutingContext context) {

    }

}
