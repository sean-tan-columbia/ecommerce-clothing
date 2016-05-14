package com.mycloset.server.cache;

import com.mycloset.server.model.Image;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jtan on 3/20/16.
 */
public class ItemDetailRequestHandler extends JDBCCacheHandler<Long, Image> {

    private final static Logger logger = LoggerFactory.getLogger(ItemRequestHandler.class);

    public ItemDetailRequestHandler() throws Exception {
        super(ItemDetailRequestHandler.class.getName());
    }

    public List<Image> handle(String itemId) throws Exception {
        String sql =    "SELECT item_id, image_id, image_store_path, image_store_format\n" +
                        "FROM test.image img\n" +
                        "WHERE item_id = " + itemId + "\n" +
                        "AND is_active = 1\n" +
                        "ORDER BY image_position;\n";
        return this.get(sql).stream()
                .map(id -> cache.get(id))
                .filter(element -> element != null)
                .map(element -> (Image) element.getObjectValue())
                .collect(Collectors.toList());
    }

    @Override
    protected Map<Long, Image> mapToObject(String sql) throws Exception {
        logger.info("[Worker] Retrieving message in Thread " + Thread.currentThread().getName());
        Map<Long, Image> resultMap = new HashMap<>();
        ResultSet resultSet = this.getConnection().createStatement().executeQuery(sql);
        while (resultSet.next()) {
            Long imageId = resultSet.getLong("image_id");
            Image image = new Image(imageId);
            image.setItemId(resultSet.getLong("item_id"));
            image.setImagePath(resultSet.getString("image_store_path") + "/" + resultSet.getString("image_id") + "." + resultSet.getString("image_store_format"));
            resultMap.put(imageId, image);
        }
        return resultMap;
    }

}
