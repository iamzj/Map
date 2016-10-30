package com.baidumap;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 */
public class BitmapCache implements ImageLoader.ImageCache {

    private LruCache<String, Bitmap> mCache;

    private ImageLoaderCallBack callBack;

    public BitmapCache() {
        setCache();
    }

    public BitmapCache(ImageLoaderCallBack callBack) {
        setCache();
        this.callBack=callBack;
    }

    private void setCache(){
        int maxSize = 10 * 1024 * 1024;
        mCache = new LruCache<String, Bitmap>(maxSize) {
            @Override
            protected int sizeOf(String key, Bitmap bitmap) {
                return bitmap.getRowBytes() * bitmap.getHeight();
            }
        };
    }

    @Override
    public Bitmap getBitmap(String url) {
        Bitmap bitmap = mCache.get(url);
        if(bitmap!=null && callBack!=null){
            callBack.imageLoaderSuccess(url,bitmap);
        }
        return mCache.get(url);
    }

    @Override
    public void putBitmap(String url, Bitmap bitmap) {
        if(bitmap!=null && callBack!=null){
            callBack.imageLoaderSuccess(url,bitmap);
        }
        mCache.put(url, bitmap);
    }

    public interface ImageLoaderCallBack{
        public void imageLoaderSuccess(String url, Bitmap bitmap);
    }
}
