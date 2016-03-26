package com.mycloset.server.verticle;

/**
 * Created by jtan on 2/17/16.
 */
import com.mycloset.server.model.Item;
import com.mycloset.server.util.Runner;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;
import io.vertx.ext.web.Router;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * @author <a href="http://tfox.org">Tim Fox</a>
 */
public class DefaultItemListVerticle extends AbstractVerticle {

    // Convenience method so you can run it in your IDE
    public static void main(String[] args) {
        Runner.runExample(DefaultItemListVerticle.class);
    }

    @Override
    public void start() throws Exception {

        Item item1 = new Item(1L);
        Item item2 = new Item(2L);
        Item item3 = new Item(3L);
        Item item4 = new Item(4L);

        item1.setCoverImage(new File("/Users/jtan/IdeaProjects/closet-frontend/asset/img/closet-test-1.jpg"));
        item2.setCoverImage(new File("/Users/jtan/IdeaProjects/closet-frontend/asset/img/closet-test-2.jpg"));
        item3.setCoverImage(new File("/Users/jtan/IdeaProjects/closet-frontend/asset/img/closet-test-3.jpg"));
        item4.setCoverImage(new File("/Users/jtan/IdeaProjects/closet-frontend/asset/img/closet-test-4.jpg"));

        item1.setDescription("Add description to this image.");
        item2.setDescription("Add description to this image.");
        item3.setDescription("Add description to this image.");
        item4.setDescription("Add description to this image.");

        List<Map<String, String>> itemList = new ArrayList<>();
        itemList.add(item1.getShortSerializable());
        itemList.add(item2.getShortSerializable());
        itemList.add(item3.getShortSerializable());
        itemList.add(item4.getShortSerializable());

        Map<String, List<Map<String, String>>> itemMap = new HashMap<>();
        itemMap.put("items", itemList);

        Router router = Router.router(vertx);

        router.route().handler(routingContext -> {
            routingContext.response()
                    .putHeader("content-type", "application/json")
                    .putHeader("Access-Control-Allow-Origin", "*")
                    .end(Json.encodePrettily(itemMap));
        });

        vertx.createHttpServer().requestHandler(router::accept).listen(8080);
    }
}
