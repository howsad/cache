package ru.sbt.cache.cacheproxy.strategy;

import java.io.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by Alexander Ushakov on 29.08.2016.
 */
public class FileStrategy implements CacheStrategy {
    private final String dir;
    private final boolean zip;

    public FileStrategy(String dir, boolean zip) {
        this.dir = dir;
        this.zip = zip;
    }

    @Override
    public boolean isCached(String name) {
        return new File(name).exists();
    }

    @Override
    public Object getCache(String name) throws IOException, ClassNotFoundException {
        try (ObjectInputStream stream = zip ? new ObjectInputStream(new GZIPInputStream(new FileInputStream(name)))
                : new ObjectInputStream(new FileInputStream(name))) {
            return stream.readObject();
        } catch (ClassNotFoundException e) {
            throw new ClassNotFoundException("Bad file " + name + ". Try to delete it first.");
        }
    }

    @Override
    public String getName(String name) {
        String s = dir + "\\" + name;
        if (zip) {
            return s + ".gz";
        } else {
            return s + ".ser";
        }
    }

    @Override
    public void putCache(String name, Object cache) throws IOException {
        try (ObjectOutputStream stream = zip ? new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream(name)))
                : new ObjectOutputStream(new FileOutputStream(name))) {
            stream.writeObject(cache);
        } catch (FileNotFoundException e) {
            throw new FileNotFoundException("Probably bad arguments or file prefix names.");
        }
    }
}
