package com.mycloset.server.verticle;

import com.mycloset.server.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import io.vertx.ext.jdbc.JDBCClient;
import io.vertx.ext.sql.SQLConnection;
import io.vertx.ext.web.Router;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import java.util.Random;

/**
 * Created by jtan on 7/28/16.
 */
public class DemoVerticle extends AbstractVerticle {

    private final static Logger logger = LoggerFactory.getLogger("Server.Security.Activity.Monitor");

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

        List<String> ips = new ArrayList<String>();
        ips.add("102.152.10.10");
        ips.add("102.152.10.10");
        ips.add("102.152.10.10");
        ips.add("102.152.10.10");
        ips.add("72.64.62.73");
        ips.add("102.152.10.12");
        ips.add("102.152.10.12");
        ips.add("102.152.10.16");
        ips.add("102.152.10.16");
        ips.add("110.91.23.65");

        router.get("/api/client").handler(routingContext -> {
            String clientId = routingContext.request().getParam("id");
            Random generator = new Random();
            int randomIndex = generator.nextInt(10);
            logger.info("Request from " + ips.get(randomIndex) + " for client ID: " + clientId);
            jdbcClient.getConnection(res -> {
                if (res.succeeded()) {
                    SQLConnection connection = res.result();
                    // connection.queryWithParams("SELECT * FROM test.client WHERE id = ?", new JsonArray().add(clientId), res2 -> {
                    connection.query("SELECT * FROM test.client WHERE id=" + clientId, res2 -> {
                        if (res2.failed()) {
                            // logger.error("Cannot retrieve the data from the database");
                            routingContext.response()
                                    .putHeader("content-type", "application/json; charset=utf-8")
                                    .end(Json.encodePrettily(new ArrayList<>()));
                            connection.close();
                            //res2.cause().printStackTrace();
                            return;
                        }
                        List<String> results = res2.result().getResults().stream()
                                .map(JsonArray::encode)
                                .collect(Collectors.toList());
                        routingContext.response()
                                .putHeader("content-type", "application/json; charset=utf-8")
                                .end(Json.encodePrettily(results));
                        connection.close();
                    });
                } else {
                    logger.error("Failed to connect to database.");
                }
            });
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
