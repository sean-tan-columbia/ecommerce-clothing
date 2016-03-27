package com.mycloset.server.verticle;

import com.mycloset.server.cache.ItemRequestHandler;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.Json;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.mycloset.server.cache.ItemRequestHandler;

/**
 * Created by jtan on 2/23/16.
 */
public class ItemRequestVerticle extends AbstractVerticle {

    private ItemRequestHandler itemRequestHandler;

    @Override
    public void start() throws Exception {

        System.out.println("[Worker] Starting in " + Thread.currentThread().getName());

        this.itemRequestHandler = new ItemRequestHandler();

        vertx.eventBus().consumer("index.item.request", message -> {
            System.out.println("[Worker] Received message " + message.body() + " from " + message.address() + " in thread " + Thread.currentThread().getName());
            try {
                message.reply(mapToJson());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    private String mapToJson() throws Exception {
        List<Map<String, String>> items = this.itemRequestHandler.handle().stream()
            .map(item -> {
                Map<String, String> itemProp = new HashMap<>();
                itemProp.put("id", item.getId().toString());
                itemProp.put("source", item.getCoverImagePath());
                itemProp.put("description", item.getDescription());
                return itemProp;
            }).collect(Collectors.toList());
        Map<String, List<Map<String, String>>> itemMap = new HashMap<>();
        itemMap.put("items", items);
        return Json.encodePrettily(itemMap);
    }

}
