package com.baidumap;

import android.app.Application;

import com.baidu.mapapi.SDKInitializer;

/**
 * Created by yifengZhang on 2016/9/18 17:19.
 * 描述:(请用一句话描述这个内容)
 */
public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // 在使用 SDK 各组间之前初始化 context 信息，传入 ApplicationContext
        SDKInitializer.initialize(this);
    }
}
