package com.mycloset.server.cache;

import io.vertx.core.AbstractVerticle;
import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;


/**
 * Created by jtan on 3/11/16.
 */
public abstract class AbstractCacheHandler<K, V> {

    protected Cache cache;

    protected void setCache(Cache cache) {
        this.cache = cache;
    }

    protected Cache getCache() {
        return this.cache;
    }

    protected boolean cacheEnable = true;

    public void setCacheEnable(boolean cacheEnable) {
        this.cacheEnable = cacheEnable;
    }

    protected abstract V request(K key) throws Exception;

    public V get(K key) throws Exception {
        if (!this.cacheEnable) {
            return this.request(key);
        }
        Element element = cache.get(key);
        if (element == null) {
            V value = this.request(key);
            cache.put(new Element(key, value));
            return value;
        } else {
            return (V) element.getObjectValue();
        }
    }

}
