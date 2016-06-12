package com.mycloset.server.cache;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import io.vertx.core.logging.Logger;
import io.vertx.core.logging.LoggerFactory;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

import java.sql.Connection;
import java.sql.Statement;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by jtan on 3/10/16.
 */
public abstract class JDBCCacheHandler<U, V> extends AbstractCacheHandler<String, List<U>> {

    private final static Logger logger = LoggerFactory.getLogger(JDBCCacheHandler.class);

    protected CacheManager cacheManager;
    protected Connection connection;
    protected Statement statement;
    protected ComboPooledDataSource dataSource;

    protected JDBCCacheHandler(String name) throws Exception {
        this.cacheManager = CacheManager.create("src/main/resources/ehcache.xml");
        if (this.cacheManager.cacheExists(name)) {
            this.cache = this.cacheManager.getCache(name);
        } else {
            this.cacheManager.addCache(name);
            this.cache = this.cacheManager.getCache(name);
        }
        this.dataSource = new ComboPooledDataSource();
        this.dataSource.setDriverClass("org.mariadb.jdbc.Driver");
        this.dataSource.setJdbcUrl("jdbc:mariadb://104.196.15.12:3306/test?autoReconnect=true");
        this.dataSource.setUser("tajinx");
        this.dataSource.setPassword("jtan");
    }

    protected Statement prepareStatement() throws Exception {
        if (this.connection == null) {
            logger.info("Connected to MySQL database.");
            connection = dataSource.getConnection();
        }
        this.statement = this.connection.createStatement();
        return this.statement;
    }

    @Override
    protected List<U> request(String sql) throws Exception {
        Map<U, V> results = mapToObject(sql);
        results.forEach((key, value) -> this.cache.put(new Element(key, value)));
        return results.entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    protected abstract Map<U, V> mapToObject(String sql) throws Exception;

}
