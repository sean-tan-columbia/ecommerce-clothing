package com.gtech.webgo.server.verticle;

import com.gtech.webgo.server.utils.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.Session;
import io.vertx.ext.web.handler.CookieHandler;
import io.vertx.ext.web.handler.SessionHandler;
import io.vertx.ext.web.handler.sockjs.BridgeOptions;
import io.vertx.ext.web.handler.sockjs.PermittedOptions;
import io.vertx.ext.web.handler.sockjs.SockJSHandler;
import io.vertx.ext.web.sstore.LocalSessionStore;

/**
 * Created by jtan on 8/8/16.
 */
public class DemoSession extends AbstractVerticle {

    public static void main(String[] args) {
        Runner.runExample(DemoSession.class);
    }

    @Override
    public void start() throws Exception {

        Router router = Router.router(vertx);

        // Handle eventbus messages
        BridgeOptions bridgeOptions = new BridgeOptions()
                .addInboundPermitted(new PermittedOptions().setAddressRegex("room\\..+"))
                .addOutboundPermitted(new PermittedOptions().setAddressRegex("room\\..+"));
        router.route("/match/*").handler(SockJSHandler.create(vertx).bridge(bridgeOptions));

        router.route().handler(CookieHandler.create());
        router.route().handler(SessionHandler.create(LocalSessionStore.create(vertx)));
        router.route().handler(routingContext -> {
            Session session = routingContext.session();
            System.out.println(session.id());
            vertx.eventBus().consumer("room." + session.id(), res -> {
                System.out.println(res.body().toString());
                vertx.eventBus().publish("room." + session.id(), res.body().toString());
            });
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }

}
