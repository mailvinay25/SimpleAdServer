/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vinay.simpleadserver.cache;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.collections.MapIterator;
import org.apache.commons.collections.map.LRUMap;

/**
 * AdServerInMemoryCache is In Memory Cache implementation for the Simple Ad
 * server web application
 *
 * @author vchaitankar
 * @param <K> Key for storing in the cache
 * @param <T> Value to be stored along with key in the cache
 */
public class AdServerInMemoryCache<K, T> {

    private final LRUMap adServerCacheMap;

    /**
     * AdServerCacheObject is a inner class within InMemory cache. Its used to
     * store value.
     */
    protected class AdServerCacheObject {

        public long creationTime = System.currentTimeMillis();
        private final long timeToLive;
        public T value;

        /**
         * @param value Used to represent the value
         * @param adServerTimeToLive Time in seconds upon which the value should
         * expire
         */
        protected AdServerCacheObject(T value, long adServerTimeToLive) {
            this.timeToLive = adServerTimeToLive * 1000;
            this.value = value;
        }
    }

    /**
     *
     * @param adServerTimerInterval Time in seconds in which the InMemory Cache
     * cleans up expired objects
     * @param maxItems The maximum number of key/value pairs the InMemory cache
     * can support.
     */
    public AdServerInMemoryCache(final long adServerTimerInterval, int maxItems) {
        adServerCacheMap = new LRUMap(maxItems);

        if (adServerTimerInterval > 0) {

            Thread t;
            t = new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(adServerTimerInterval * 1000);
                        } catch (InterruptedException ex) {
                        }
                        cleanup();
                    }
                }
            });

            t.setDaemon(true);
            t.start();
        }
    }

    /**
     *
     * @param key Key for storing object in Cache
     * @param value Value of object stored in Cache
     * @param timeToLive Time in seconds in which the key/value pair should
     * expire
     */
    public void put(K key, T value, Long timeToLive) {
        synchronized (adServerCacheMap) {
            adServerCacheMap.put(key, new AdServerCacheObject(value, timeToLive));
        }
    }

    /**
     *
     * @param key Key to retrieve object from Cache
     * @return Returns the value associated with the key from Cache
     */
    @SuppressWarnings("unchecked")
    public T get(K key) {
        synchronized (adServerCacheMap) {
            AdServerCacheObject c = (AdServerCacheObject) adServerCacheMap.get(key);

            if (c == null) {
                return null;
            } else if ((System.currentTimeMillis() > (c.timeToLive + c.creationTime))) {
                adServerCacheMap.remove(key);
                return null;
            } else {
                return c.value;
            }
        }
    }

    /**
     *
     * @param key Key to retrieve object from Cache
     * @return Returns the value associated with the key from Cache
     */
    public AdServerCacheObject getCacheObject(K key) {
        synchronized (adServerCacheMap) {
            AdServerCacheObject c = (AdServerCacheObject) adServerCacheMap.get(key);

            if (c == null) {
                return null;
            } else if ((System.currentTimeMillis() > (c.timeToLive + c.creationTime))) {
                adServerCacheMap.remove(key);
                return null;
            } else {
                return c;
            }
        }
    }

    /**
     *
     * @param key key to be removed from cache
     */
    public void remove(K key) {
        synchronized (adServerCacheMap) {
            adServerCacheMap.remove(key);
        }
    }

    /**
     *
     * @return Returns the size of the cache
     */
    public int size() {
        synchronized (adServerCacheMap) {
            return adServerCacheMap.size();
        }
    }

    /**
     * cleanup() method is periodically called to remove key/value pairs that
     * have expired from the cache.
     */
    @SuppressWarnings("unchecked")
    public void cleanup() {

        long now = System.currentTimeMillis();
        ArrayList<K> deleteKey = null;

        synchronized (adServerCacheMap) {
            MapIterator itr = adServerCacheMap.mapIterator();

            deleteKey = new ArrayList<>((adServerCacheMap.size() / 2) + 1);
            K key = null;
            AdServerCacheObject c = null;

            while (itr.hasNext()) {
                key = (K) itr.next();
                c = (AdServerCacheObject) itr.getValue();

                if (c != null && (now > (c.timeToLive + c.creationTime))) {
                    deleteKey.add(key);
                }
            }
        }

        for (K key : deleteKey) {
            synchronized (adServerCacheMap) {
                adServerCacheMap.remove(key);
            }

            Thread.yield();
        }
    }

    /**
     *
     * @return getAll() returns all the key/value pairs that have not expired
     * and are available in the cache.
     */
    public Map<K, T> getAll() {
        MapIterator itr = adServerCacheMap.mapIterator();
        Map<K, T> resultMap = new LinkedHashMap<>();
        K key = null;
        AdServerCacheObject c = null;
        while (itr.hasNext()) {
            key = (K) itr.next();
            c = (AdServerCacheObject) itr.getValue();

            if (c != null && (System.currentTimeMillis() < (c.timeToLive + c.creationTime))) {
                resultMap.put(key, c.value);
            }
        }
        return resultMap;
    }
}
