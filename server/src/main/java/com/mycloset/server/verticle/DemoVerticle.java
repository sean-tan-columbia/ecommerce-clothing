package com.mycloset.server.verticle;

import com.mycloset.server.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;

import java.util.List;
import java.util.stream.Collectors;

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

        JsonObject config = new JsonObject()
                .put("url", "jdbc:mariadb://104.196.15.12:3306/test?autoReconnect=true")
                .put("driver_class", "org.mariadb.jdbc.Driver")
                .put("max_pool_size", 30)
                .put("user", "tajinx")
                .put("password", "jtan");
        JDBCClient jdbcClient = JDBCClient.createShared(vertx, config);

        // Bind "/" to our hello message - so we are still compatible.
        router.route("/").handler(routingContext -> {
            HttpServerResponse response = routingContext.response();
            response.putHeader("content-type", "text/html")
                    .end("<h1>Hello from my first Vert.x 3 application</h1>");
        });

        jdbcClient.getConnection(res -> {
            if (res.succeeded()) {
                System.out.println("[Info] Connected to database.");
                SQLConnection connection = res.result();
                router.get("/api/client").handler(routingContext -> {
                    String clientId = routingContext.request().getParam("id");
                    connection.queryWithParams("SELECT * FROM test.client WHERE id = ?", new JsonArray().add(clientId), res2 -> {
                        if (res2.failed()) {
                            System.err.println("Cannot retrieve the data from the database");
                            res2.cause().printStackTrace();
                            return;
                        }
                        List<String> results = res2.result().getResults().stream()
                                .map(JsonArray::encode)
                                .collect(Collectors.toList());
                        routingContext.response()
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(Json.encodePrettily(results));
                    });
                });
            } else {
                System.out.println("[Error] Failed to connect to database.");
            }
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

}
