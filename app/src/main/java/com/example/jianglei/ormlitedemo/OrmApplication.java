package com.example.jianglei.ormlitedemo;

import android.app.Application;

/**
 * Created by jianglei on 2016/7/19.
 */
public class OrmApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        DatabaseHelper.initOrmLite(this);
    }
}
