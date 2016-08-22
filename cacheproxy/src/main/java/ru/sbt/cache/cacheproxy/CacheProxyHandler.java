package ru.sbt.cache.cacheproxy;

import java.io.*;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Alexander Ushakov on 19.08.2016.
 */
public class CacheProxyHandler implements InvocationHandler {
    private Object obj;
    private File dir;
    private Map<String, Object> map = new LinkedHashMap<>();

    public CacheProxyHandler(Object obj, File dir) {
        this.obj = obj;
        this.dir = dir;
    }

    private Object findCached(boolean toFile, boolean zip, String name) throws IOException, ClassNotFoundException {
        if (!toFile) {
            return map.get(name);
        } else if (new File(name).exists()) {
            if (zip) {
                try (ObjectInputStream stream = new ObjectInputStream(new GZIPInputStream
                        (new FileInputStream(name)))) {
                    return stream.readObject();
                } catch (ClassNotFoundException e) {
                    throw new ClassNotFoundException("Bad file " + name + ". Try to delete it first.");
                }
            } else {
                try (ObjectInputStream stream = new ObjectInputStream(new FileInputStream(name))) {
                    return stream.readObject();
                } catch (ClassNotFoundException e) {
                    throw new ClassNotFoundException("Bad file " + name + ". Try to delete it first.");
                }
            }
        } else {
            return null;
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!method.isAnnotationPresent(Cache.class)) {
            return method.invoke(obj, args);
        } else {
            Cache annotation = method.getAnnotation(Cache.class);
            boolean toFile = annotation.storage().equals(CacheStorage.FILE);
            boolean zip = annotation.zip();

            boolean[] identityBy = annotation.identityBy();

            StringBuilder builder = new StringBuilder();
            if (toFile) {
                builder.append(dir.toString()).append("\\");
            }
            builder.append(annotation.fileNamePrefix());

            for (int i = 0; i < args.length; i++) {
                if (identityBy.length <= i || identityBy[i]) {
                    builder.append("_").append(args[i].toString());
                }
            }
            if (toFile) {
                if (annotation.zip()) {
                    builder.append(".gz");
                } else {
                    builder.append(".ser");
                }
            }
            String name = builder.toString();

            Object cache = findCached(toFile, zip, name);
            if (cache != null) {
                return cache;
            } else {
                Object result = method.invoke(obj, args);
                if (!toFile) {
                    if (map.size() > 0 && annotation.storageSize() == map.size()) {
                        map.remove(map.entrySet().iterator().next().getKey());
                    }
                    map.put(name, result);
                } else {
                    if (zip) {
                        try (OutputStream out = new GZIPOutputStream(new FileOutputStream(name))) {
                        }
                        try (ObjectOutputStream stream = new ObjectOutputStream(new GZIPOutputStream
                                (new FileOutputStream(name)))) {
                            stream.writeObject(result);
                        } catch (FileNotFoundException e) {
                            throw new FileNotFoundException("Probably bad arguments or file prefix names.");
                        }
                    } else {
                        try (ObjectOutputStream stream = new ObjectOutputStream(new FileOutputStream(name))) {
                            stream.writeObject(result);
                        } catch (FileNotFoundException e) {
                            throw new FileNotFoundException("Probably bad arguments or file prefix names.");
                        }
                    }
                }
                return result;
            }
        }
    }
}

