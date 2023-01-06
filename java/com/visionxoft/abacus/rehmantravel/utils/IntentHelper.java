package com.visionxoft.abacus.rehmantravel.utils;

import java.util.Hashtable;

/**
 * Helper class used for temporary storage of data/objects. It is used as an alternative of Intents.
 */
public class IntentHelper {

    private static IntentHelper instance;
    private Hashtable<String, Object> hash;

    private IntentHelper() {
        hash = new Hashtable<>();
    }

    private static IntentHelper getInstance() {
        if (instance == null) instance = new IntentHelper();
        return instance;
    }

    /**
     * Add object by specific key
     *
     * @param object Object to add
     * @param key    Value of key
     */
    public static void addObjectForKey(Object object, String key) {
        getInstance().hash.put(key, object);
    }

    /**
     * Return object from specific key and remove its value
     *
     * @param key Value of key
     * @return Added object or null if not found
     */
    public static Object getObjectForKey(String key) {
        return getObjectForKey(key, false);
    }

    /**
     * Return object from specific key and check whether remove its value or not
     *
     * @param key        Value of key
     * @param remove_key true to remove key
     * @return Added object or null if not found
     */
    public static Object getObjectForKey(String key, boolean remove_key) {
        IntentHelper helper = getInstance();
        if (remove_key) return helper.hash.remove(key);
        else return helper.hash.get(key);
    }
}