package com.mycloset.server.cache;

import net.sf.ehcache.CacheManager;
import java.io.File;

/**
 * Created by jtan on 3/11/16.
 */
public abstract class FileRequestHandler extends AbstractCacheHandler<String, byte[]> {

    protected CacheManager cacheManager;

    protected FileRequestHandler(String name) {
        this.cacheManager = CacheManager.create();
        if (this.cacheManager.cacheExists(name)) {
            this.cache = this.cacheManager.getCache(name);
        } else {
            this.cacheManager.addCache(name);
            this.cache = this.cacheManager.getCache(name);
        }
    }

    @Override
    protected byte[] request(String filePath) throws Exception {
        File file = new File(filePath);
        return getFileByteStream(file);
    }

    protected abstract byte[] getFileByteStream(File file) throws Exception;

}
