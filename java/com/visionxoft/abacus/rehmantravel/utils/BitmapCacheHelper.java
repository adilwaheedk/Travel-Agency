package com.visionxoft.abacus.rehmantravel.utils;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

public class BitmapCacheHelper {

    private LruCache<String, Bitmap> lruCache;

    public BitmapCacheHelper() {
        final int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 1024) / 8;

        lruCache = new LruCache<String, Bitmap>(cacheSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                // The cache size will be measured in kilobytes
                return bitmap.getByteCount() / 1024;
            }
        };
    }

    public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
        if (getBitmapFromMemCache(key) == null) lruCache.put(key, bitmap);
    }

    public Bitmap getBitmapFromMemCache(String key) {
        return lruCache.get(key);
    }
}
