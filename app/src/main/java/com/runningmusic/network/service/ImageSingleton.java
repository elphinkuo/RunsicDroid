package com.runningmusic.network.service;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;
import com.runningmusic.network.LruBitmapCache;

/**
 * Created by guofuming on 22/1/16.
 */
public class ImageSingleton {

    private static ImageSingleton mInstance;
    private RequestQueue mRequestQueue;
    private ImageLoader mImageLoader;
    private LruBitmapCache bitmapCache;
    private static Context mCtx;

    private ImageSingleton(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
//        bitmapCache = new LruBitmapCache(20 * 1024 * 1024);
//        mImageLoader = new ImageLoader(mRequestQueue, bitmapCache);
        mImageLoader = new ImageLoader(mRequestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(2 * 1024 * 1024);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized ImageSingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new ImageSingleton(context);
        }
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
        }
        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return mInstance.mImageLoader;
    }

//    public Bitmap getBitmap(String url) {
//        return bitmapCache.getBitmap(url);
//    }
}
