package com.mycloset.server.verticle;

import com.mycloset.server.cache.ImageRequestHandler;
import com.mycloset.server.cache.ItemDetailRequestHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jtan on 3/22/16.
 */
public class ItemDetailRequestVerticle extends AbstractVerticle {

    private ItemDetailRequestHandler itemDetailRequestHandler;

    @Override
    public void start() throws Exception {
        System.out.println("[Worker] Starting in " + Thread.currentThread().getName());

        this.itemDetailRequestHandler = ItemDetailRequestHandler.getInstance();

        vertx.eventBus().consumer("detail.item.request", message -> {
            System.out.println("[Worker] Received message " + message.body() + " from " + message.address() + " in thread " + Thread.currentThread().getName());
            try {
                String itemId = message.body().toString();
                message.reply(mapToJson(itemId));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public String mapToJson(String itemId) throws Exception {
        List<Map<String, String>> imageList = this.itemDetailRequestHandler.handle(itemId).stream()
                .map(image -> {
                    Map<String, String> imageProp = new HashMap<>();
                    imageProp.put("source", image.getImagePath());
                    return imageProp;
                })
                .collect(Collectors.toList());
        Map<String, List<Map<String, String>>> imageMap = new HashMap<>();
        imageMap.put("images", imageList);
        return Json.encodePrettily(imageMap);
    }

}
