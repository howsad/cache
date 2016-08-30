package ru.sbt.cache.cacheproxy.strategy;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Alexander Ushakov on 29.08.2016.
 */
public class MemoryStrategy implements CacheStrategy {
    private final Map<String, Object> map = new LinkedHashMap<>();

    @Override
    public boolean isCached(String name) {
        return map.containsKey(name);
    }

    @Override
    public Object getCache(String name) {
        return map.get(name);
    }

    @Override
    public String getName(String name) {
        return name;
    }

    @Override
    public void putCache(String name, Object cache) {
        map.put(name, cache);
/*
        if (map.size() > 0 && annotation.storageSize() == map.size()) {
            map.remove(map.entrySet().iterator().next().getKey());

        }
*/
    }
}
