package com.mycloset.server.cache;

import com.mycloset.server.model.Item;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * Created by jtan on 3/10/16.
 */
public class ItemRequestHandler extends JDBCCacheHandler<Long, Item> {

    private final static Logger logger = LoggerFactory.getLogger(ItemRequestHandler.class);

    private static ItemRequestHandler itemRequestHandler;

    private ItemRequestHandler() throws Exception {
        super(ItemRequestHandler.class.getName());
    }

    public static ItemRequestHandler getInstance() throws Exception {
        if (itemRequestHandler == null) {
            itemRequestHandler = new ItemRequestHandler();
        }
        return itemRequestHandler;
    }

    public List<Item> handle() throws Exception {
        String sql =    "SELECT itm.item_id, item_name, item_price, item_in_stock, item_description, image_store_path, image_id, image_store_format\n" +
                        "FROM test.item itm\n" +
                        "JOIN test.image img\n" +
                        "ON itm.item_id = img.item_id\n" +
                        "AND itm.label_image_id = img.image_id\n" +
                        "WHERE itm.is_active = 1\n" +
                        "ORDER BY itm.modified_timestamp DESC limit 10;";
        return this.get(sql).stream()
                .map(id -> cache.get(id))
                .filter(element -> element != null)
                .map(element -> (Item) element.getObjectValue())
                .collect(Collectors.toList());
    }

    @Override
    protected Map<Long, Item> mapToObject(String sql) throws Exception {
        logger.info("[Worker] Retrieving message in Thread " + Thread.currentThread().getName());
        Map<Long, Item> resultMap = new HashMap<>();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            Long itemID = resultSet.getLong("item_id");
            Item item = new Item(itemID);
            item.setName(resultSet.getString("item_name"));
            item.setPrice(resultSet.getDouble("item_price"));
            item.setItemInStock(resultSet.getInt("item_in_stock"));
            item.setDescription(resultSet.getString("item_description"));
            item.addImagePath(resultSet.getString("image_store_path") + "/" + resultSet.getString("image_id") + "." + resultSet.getString("image_store_format"));
            resultMap.put(itemID, item);
        }
        return resultMap;
    }

}
