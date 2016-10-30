package com.baidumap;

import android.content.Context;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by CaiBing.Zhang on 2015/11/6.
 */
public class WxhlImageLoader {
    public static void loaderHeadImage(Context context, ImageView imageView,
                                       String portraitURL,BitmapCache.ImageLoaderCallBack callBack) {
        RequestQueue mQueue = Volley.newRequestQueue(context);
        com.android.volley.toolbox.ImageLoader mImageLoader = new com.android.volley.toolbox.ImageLoader(mQueue, new BitmapCache(callBack));
        com.android.volley.toolbox.ImageLoader.ImageListener listener =
                com.android.volley.toolbox.ImageLoader.getImageListener(imageView, R.drawable.img_head, R.drawable.img_head);
        mImageLoader.get(portraitURL, listener);
    }
}
