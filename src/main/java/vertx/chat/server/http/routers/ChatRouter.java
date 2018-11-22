package vertx.chat.server.http.routers;

import io.vertx.core.Vertx;
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

public class ChatRouter {
    private final Vertx vertx;
    private final String inBoundAdress = "client.to.chat";
    private final String outBoundAdress = "chat.to.client";
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatRouter.class);

    private final Map<String, User> users;
    public ChatRouter(Router router, Vertx vertx) {
        this.vertx = vertx;
        this.users = new HashMap<>();
        router.route("/chat/users").handler(this::getUsers);
        SockJSHandler sockJSHandler = SockJSHandler.create(vertx);
        BridgeOptions bridgeOptions = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress(inBoundAdress))
                .addOutboundPermitted(new PermittedOptions().setAddress(outBoundAdress))
                .addInboundPermitted(new PermittedOptions().setAddressRegex("user.\\.+"));

        sockJSHandler.bridge(bridgeOptions,event -> {
            switch (event.type()) {
                case PUBLISH: this.publishEvent(event);break;
                case REGISTER: this.registerEvent(event);break;
                case SOCKET_CLOSED: this.closeEvent(event);break;
            }
            event.complete(true);
        });
        router.route("/eventbus/*").handler(sockJSHandler);
    }

    private void closeEvent(BridgeEvent event) {
    }

    private void registerEvent(BridgeEvent event) {
        LOGGER.info("user connected");
        User user = event.socket().webUser();
        String username = user.principal().getString("name");
        this.users.put(username, user);
        this.vertx.eventBus().consumer("user."+username, data -> {

        });
    }

    private void publishEvent(BridgeEvent event) {
    }


    public void getUsers(RoutingContext context) {

    }

}
