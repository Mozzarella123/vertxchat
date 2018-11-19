package vertx.chat.server.http.routers;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import vertx.chat.server.database.DatabaseService;

public class UserRouter {
    private  final  Router router;
    private final JDBCAuth auth;
    public UserRouter(Router router,Vertx vertx, JDBCAuth auth,  DatabaseService dbService) {
        this.router = router;
        this.auth = auth;
        router.route().handler(CookieHandler.create());
        router.route().handler(BodyHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(UserSessionHandler.create(auth));
        AuthHandler authHandler = RedirectAuthHandler.create(auth, "/");
        router.route("/account/*").handler(authHandler);
        this.router.post("/login-auth").handler(FormLoginHandler.create(this.auth));
        this.router.get("/logout").handler(context -> {
            context.clearUser();
            context.response()
                    .setStatusCode(302)
                    .putHeader("Location", "/")
                    .end();
        });
        this.router.post("/register").handler((r) -> {
           JsonObject body = r.getBodyAsJson();
           String username = body.getString("username");
           String password = body.getString("password");
           String email = body.getString("email");
        });
    }

}
