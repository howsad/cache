package ru.sbt.cache.cacheproxy;

import ru.sbt.cache.cacheproxy.strategy.CacheStrategy;
import ru.sbt.cache.cacheproxy.strategy.FileStrategy;
import ru.sbt.cache.cacheproxy.strategy.MemoryStrategy;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;

import static ru.sbt.cache.cacheproxy.CacheStorage.FILE;
import static ru.sbt.cache.cacheproxy.CacheStorage.MEMORY;
import static ru.sbt.cache.cacheproxy.CacheStorage.ZIP;

/**
 * Created by Alexander Ushakov on 19.08.2016.
 */
public class CacheProxyHandler implements InvocationHandler {
    private final Object obj;
    private final Map<CacheStorage, CacheStrategy> strategyMap = new HashMap<>();

    public CacheProxyHandler(Object obj, File dir) {
        this.obj = obj;
        String dirString = dir.toString();
        strategyMap.put(MEMORY, new MemoryStrategy());
        strategyMap.put(FILE, new FileStrategy(dirString, false));
        strategyMap.put(ZIP, new FileStrategy(dirString, true));
    }

    private String getBasicName(String prefix, Object[] args, boolean[] identityBy) {
        StringBuilder builder = new StringBuilder();
        builder.append(prefix);
        for (int i = 0; i < args.length; i++) {
            builder.append("_");
            if (identityBy.length <= i || identityBy[i]) {
                builder.append(args[i].toString());
            }
        }
        return builder.toString();
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!method.isAnnotationPresent(Cache.class)) {
            return method.invoke(obj, args);
        }
        Cache annotation = method.getAnnotation(Cache.class);
        CacheStrategy strategy = strategyMap.get(annotation.storage());

        String name = getBasicName(annotation.fileNamePrefix().isEmpty() ? method.getName()
                : annotation.fileNamePrefix(), args, annotation.identityBy());
        name = strategy.getName(name);

        if (strategy.isCached(name)) {
            return strategy.getCache(name);
        }
        Object result = method.invoke(obj, args);
        strategy.putCache(name, result);
        return result;
    }
}
