package vertx.chat.server;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.auth.jdbc.JDBCAuth;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import vertx.chat.server.database.DatabaseService;
import vertx.chat.server.database.DatabaseVerticle;

@RunWith(VertxUnitRunner.class)
public class DatabaseVerticleTest {

    private Vertx vertx;

    private DatabaseService service;
    private JDBCAuth auth;

    @Before
    public void prepare(TestContext context) throws InterruptedException {
        vertx = Vertx.vertx();

        JsonObject conf = new JsonObject()
                .put(DatabaseConstants.CONFIG_CHATDB_JDBC_URL, "jdbc:mysql://localhost:3306/chat")
                .put(DatabaseConstants.CONFIG_CHATDB_JDBC_MAX_POOL_SIZE, 4);
        JsonObject mySQLClientConfig = new JsonObject()
                .put("user", "root")
                .put("password", "root")
                .put("url", "jdbc:mysql://localhost:3306/chat")
                .put("driver_class", "com.mysql.jdbc.Driver")
                .put("max_pool_size", 30);
        JDBCClient dbClient = JDBCClient.createShared(vertx, mySQLClientConfig);
        this.auth = JDBCAuth.create(vertx, dbClient);

        vertx.deployVerticle(new DatabaseVerticle(), new DeploymentOptions().setConfig(conf),
                context.asyncAssertSuccess(id ->
                        service = DatabaseService.createProxy(vertx, DatabaseConstants.CONFIG_CHATDB_QUEUE)));
    }


    @After
    public void tearDown(TestContext tc) {
        vertx.close(tc.asyncAssertSuccess());
    }

    @Test
    public void testThatTheDatabaseConnetcted(TestContext tc) {
        Async async = tc.async();
        async.complete();
        vertx.close();
    }

    @Test
    public void fetchUser(TestContext tc) {
        Async async = tc.async();
        this.service.fetchUser("1", (result) -> {
            if (result.succeeded()) {
                tc.assertTrue(result.result().getBoolean("found"));

            } else {
                tc.fail();
            }
            async.complete();
        });
    }

    @Test
    public void createUser(TestContext tc) {
        String salt = this.auth.generateSalt();
        String hash = this.auth.computeHash("123", salt);
        Async async = tc.async();
        this.service.createUser("fool",hash, salt,"1@mail.ru", result-> {
            if (result.succeeded()) {
                tc.assertTrue(result.result().getBoolean("created"));
            }
            else {
                tc.fail();
            }
            async.complete();
        });

    }

}