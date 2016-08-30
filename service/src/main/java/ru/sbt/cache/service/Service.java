package ru.sbt.cache.service;

import ru.sbt.cache.cacheproxy.Cache;

import java.util.Date;
import java.util.List;

import static ru.sbt.cache.cacheproxy.CacheStorage.FILE;
import static ru.sbt.cache.cacheproxy.CacheStorage.MEMORY;
import static ru.sbt.cache.cacheproxy.CacheStorage.ZIP;

/**
 * Created by Alexander Ushakov on 19.08.2016.
 */

public interface Service {
    @Cache(storage = ZIP, fileNamePrefix = "data", identityBy = {true, true, false})
    List<String> doHardWork(String item, double value, Date date);

    @Cache(storage = MEMORY, storageSize = 100_000)
    List<String> work(String item);
}
