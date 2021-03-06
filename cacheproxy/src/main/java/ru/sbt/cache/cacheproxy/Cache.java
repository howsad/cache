package ru.sbt.cache.cacheproxy;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Alexander Ushakov on 19.08.2016.
 */
@Target(value = ElementType.METHOD)
@Retention(value = RetentionPolicy.RUNTIME)
public @interface Cache {
    CacheStorage storage() default CacheStorage.MEMORY;
    String fileNamePrefix() default "";
    //True if parameter with that index has importance in checking for the cache, else otherwise.
    //All omitted indexes are considered important.
    boolean[] identityBy() default {};

    //Negative values mean that storage is unlimited. Zero effectively denies the use of cache.
    int storageSize() default -1;
}