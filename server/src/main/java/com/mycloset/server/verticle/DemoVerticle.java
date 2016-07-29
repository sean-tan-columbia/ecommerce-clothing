package com.mycloset.server.verticle;

import com.mycloset.server.model.Client;
import com.mycloset.server.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

/**
 * Created by jtan on 7/28/16.
 */
public class DemoVerticle extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(DemoVerticle.class);
    }

    @Override
    public void start(Future<Void> fut) {

        // Create a router object.
        Router router = Router.router(vertx);

        // Bind "/" to our hello message - so we are still compatible.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html")
                    .end("<h1>Hello from my first Vert.x 3 application</h1>");
        });

        router.get("/api/client").handler(routingContext -> {
            System.out.println("ClientId: " + routingContext.request().getParam("id"));
            Client client = getClient(Long.parseLong(routingContext.request().getParam("id")));
            routingContext.response()
                    .putHeader("content-type", "application/json; charset=utf-8")
                    .end(Json.encodePrettily(client));
        });

        // Create the HTTP server and pass the "accept" method to the request handler.
        vertx.createHttpServer()
                .requestHandler(router::accept)
                .listen(config().getInteger("http.port", 8080),
                        result -> {
                            if (result.succeeded()) {
                                fut.complete();
                            } else {
                                fut.fail(result.cause());
                            }
                        }
                );
    }

    private Client getClient(long id) {
        Client client = new Client(id);
        client.setSsn("123-34-5678");
        client.setName("Adam Smith");
        client.setSex("M");
        client.setBirthday("1970-02-28");
        client.setNationality("USA");
        client.setAddress("40 Lexington St.");
        client.setEnrollDate("2015-09-30");
        return client;
    }

}
