package com.mycloset.server.verticle;

import com.mycloset.server.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.eventbus.EventBus;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.StaticHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;

/**
 * Created by jtan on 2/23/16.
 */
public class MainVerticle extends AbstractVerticle {

    private final String WEB_ROOT = "../../../client/";

    public static void main(String[] args) {
        Runner.runExample(MainVerticle.class);
    }

    @Override
    public void start() throws Exception {

        System.out.println("[Manager] Main verticle has started in thread " + Thread.currentThread().getName());

        System.setProperty("vertx.disableFileCaching", "true");

        // Avoid concurrent access to the objects otherwise cause ImageRequestHandler exception
        vertx.deployVerticle("com.mycloset.server.verticle.ItemRequestVerticle",
                new DeploymentOptions().setWorker(true),
                res -> { vertx.deployVerticle("com.mycloset.server.verticle.ItemDetailRequestVerticle",
                            new DeploymentOptions().setWorker(true));
                });
        Router router = Router.router(vertx);

        // Handle eventbus messages
        BridgeOptions bridgeOptions = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddress("index.item.request"))
                .addInboundPermitted(new PermittedOptions().setAddress("detail.item.request"));
        router.route("/eventbus/*").handler(SockJSHandler.create(vertx).bridge(bridgeOptions));

        // Handle static resources
        router.route("/*").handler(StaticHandler.create(WEB_ROOT).setCachingEnabled(false));

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);

    }

}
