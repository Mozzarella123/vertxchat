package vertx.chat.server.http.routers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import vertx.chat.server.database.DatabaseService;

public class UserRouter {
    private final Router router;
    private final JDBCAuth auth;
    private DatabaseService dbService;
    public UserRouter(Router router, Vertx vertx, JDBCAuth auth, DatabaseService dbService) {
        this.router = router;
        this.auth = auth;
        this.dbService = dbService;
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(UserSessionHandler.create(auth));
        AuthHandler authHandler = RedirectAuthHandler.create(auth, "/login");
        router.route("/").handler(authHandler);
        router.route("/chat/*").handler(authHandler);
        this.router.post("/login-auth").handler(FormLoginHandler.create(this.auth));
        this.router.post("/register").handler(this::register);
        this.router.get("/logout").handler(context -> {
            context.clearUser();
            context.response()
                    .setStatusCode(302)
                    .putHeader("Location", "/login")
                    .end();
        });
    }
    private void register(RoutingContext context) {
        JsonObject body = context.getBodyAsJson();
        String username = body.getString("username");
        String password = body.getString("password");
        String email = body.getString("email");
        String salt = this.auth.generateSalt();
        String passHash = this.auth.computeHash(password, salt);
        dbService.createUser(username, passHash, salt, email, (result) -> {
            if (result.succeeded()) {
                context.response()
                        .setStatusCode(302)
                        .putHeader("Location", "/")
                        .end();
            }
        });
    }

}
