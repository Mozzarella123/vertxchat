package vertx.chat.server;

import io.vertx.core.MultiMap;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpClient;
import io.vertx.core.http.HttpClientOptions;
import io.vertx.core.net.SelfSignedCertificate;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.HttpResponse;
import io.vertx.ext.web.client.WebClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(VertxUnitRunner.class)
public class MainVerticleTest {

    private Vertx vertx;

    @Before
    public void setUp(TestContext tc) {
        vertx = Vertx.vertx();
        vertx.deployVerticle(MainVerticle.class.getName(), tc.asyncAssertSuccess());
    }

    @After
    public void tearDown(TestContext tc) {
        vertx.close(tc.asyncAssertSuccess());
    }

    @Test
    public void testThatTheServerIsStarted(TestContext tc) {
        Async async = tc.async();
        SelfSignedCertificate certificate = SelfSignedCertificate.create();
        HttpClientOptions options = new HttpClientOptions()
                .setSsl(true).setTrustAll(true).setVerifyHost(false);
        vertx.createHttpClient(options).getNow(8082, "localhost", "/", response -> {
            tc.assertEquals(response.statusCode(), 200);
            response.bodyHandler(body -> {
                tc.assertTrue(body.length() > 0);
                async.complete();
            });
        });
    }

    @Test
    public void testLogin(TestContext tc) {
        Async async = tc.async();
        HttpClientOptions options = new HttpClientOptions()
                .setSsl(true).setTrustAll(true).setVerifyHost(false);
        HttpClient client = vertx.createHttpClient(options);
        WebClient webClient = WebClient.wrap(client);
        MultiMap form = MultiMap.caseInsensitiveMultiMap();
        form.set("username", "fool");
        form.set("password", "123");
        form.set("return_url", "/");
        webClient.post(8082, "localhost","/login-auth")
                .sendForm(form, ar -> {
                    if (ar.succeeded()) {
                        HttpResponse result = ar.result();
                            tc.assertEquals(200,result.statusCode());
                    }
                    else {
                        tc.fail();
                    }
                    async.complete();
                });
    }

    @Test
    public void testLogout(TestContext tc) {
        Async async = tc.async();
        HttpClientOptions options = new HttpClientOptions()
                .setSsl(true).setTrustAll(true).setVerifyHost(false);
        HttpClient client = vertx.createHttpClient(options);
        WebClient webClient = WebClient.wrap(client);
        webClient.get(8082,"localhost", "/logout").send(ar -> {
            if (ar.succeeded()) {
                tc.assertEquals(200,ar.result().statusCode());
            }
            else {
                tc.fail();
            }
            async.complete();
        });
    }

    @Test
    public void play_with_api(TestContext context) {
        Async async = context.async();

//        Future<String> tokenRequest = Future.future();
//        webClient.get("/api/token")
//                .putHeader("login", "foo")
//                .putHeader("password", "bar")
//                .as(BodyCodec.string())
//                .send(ar -> {
//                    if (ar.succeeded()) {
//                        tokenRequest.complete(ar.result().body());
//                    } else {
//                        context.fail(ar.cause());
//                    }
//                });
    }

}