package ru.sbt.cache.cacheproxy.strategy;

import java.io.IOException;

/**
 * Created by Alexander Ushakov on 29.08.2016.
 */
public interface CacheStrategy {

    boolean isCached(String name);
    Object getCache(String name) throws IOException, ClassNotFoundException;
    String getName(String name);
    void putCache(String name, Object cache) throws IOException;
}
