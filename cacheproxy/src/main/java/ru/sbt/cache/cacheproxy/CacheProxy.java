package ru.sbt.cache.cacheproxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Proxy;
import java.nio.file.NoSuchFileException;

/**
 * Created by Alexander Ushakov on 19.08.2016.
 */
public class CacheProxy {
    private final File dir;

    public CacheProxy(File dir) throws FileNotFoundException {
        if (!dir.exists()) {
            throw new FileNotFoundException("No directory with such name.");
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("That's not a directory.");
        }
        this.dir = dir;
    }

    public <T> T cache(Object o) {
        Class<?> aClass = o.getClass();
        Class[] interfaces = aClass.getInterfaces();
        return (T) Proxy.newProxyInstance(aClass.getClassLoader(), interfaces, new CacheProxyHandler(o, dir));
    }
}
