package vertx.chat.server.http.routers;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.mail.*;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.*;
import io.vertx.ext.web.sstore.LocalSessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import vertx.chat.server.database.DatabaseService;

public class UserRouter {
    private final Router router;
    private final JDBCAuth auth;
    private final DatabaseService dbService;
    private final MailClient mailClient;
    private final Logger LOGGER = LoggerFactory.getLogger(UserRouter.class);
    public UserRouter(Router router, Vertx vertx, JDBCAuth auth, DatabaseService dbService) {
        this.router = router;
        this.auth = auth;
        this.dbService = dbService;

        MailConfig config = new MailConfig();
        config.setHostname("smtp.mail.ru");
        config.setPort(465);
        config.setLogin(LoginOption.REQUIRED);
        config.setSsl(true);
        config.setUsername("chel12331@mail.ru");
        config.setPassword("12345qweasd");
        this.mailClient = MailClient.createNonShared(vertx, config);
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
        MultiMap body = context.request().formAttributes();
        String username = body.get("username");
        String password = body.get("password");
        String email = body.get("email");
        String salt = this.auth.generateSalt();
        String passHash = this.auth.computeHash(password, salt);
        dbService.createUser(username, passHash, salt, email, (result) -> {
            if (result.succeeded()) {
                MailMessage message = new MailMessage();
                message.setFrom("Another User <chel12331@mail.ru>");
                message.setTo(email);
                message.setText("You've created account: "+ username);
                mailClient.sendMail(message, res -> {
                    if (!res.succeeded()) {
                        LOGGER.error("Mail sending failed",res.cause());
                    }
                    else {
                        LOGGER.error("Mail sent ",res.result());

                    }

                });
                LOGGER.info("User created" + username);
                context.response()
                        .setStatusCode(302)
                        .putHeader("Location", "/")
                        .end();
            }
            else {

            }
        });
    }

}
