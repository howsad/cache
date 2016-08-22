package ru.sbt.cache.service;

import ru.sbt.cache.cacheproxy.CacheProxy;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Alexander Ushakov on 19.08.2016.
 */
public class ServiceImpl implements Service {
    @Override
    public List<String> doHardWork(String item, double value, Date date) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < Math.abs(value); i++) {
            list.add(item);
        }
        list.add(Double.toString(value));
        return list;
    }

    @Override
    public List<String> work(String item) {
        List<String> list = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            list.add(item);
        }
        return list;
    }

    public static void main(String[] args) throws FileNotFoundException {
        CacheProxy proxy = new CacheProxy(new File("src/main/resources"));
        Service service = proxy.cache(new ServiceImpl());
//        service.work("Burp!");
//        service.work("Burp!");
        service.doHardWork("Burp", 178.14, new Date());
        List<String> list = service.doHardWork("Burp", 3.14, new Date());
/*        for (String s : list) {
            System.out.println(s);
        }*/
    }
}
