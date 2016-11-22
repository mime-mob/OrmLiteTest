package com.example.jianglei.ormlitedemo;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.example.jianglei.ormlitedemo.bean.Article;
import com.example.jianglei.ormlitedemo.bean.User;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jianglei on 2016/5/26.
 */
public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String TABLE_NAME = "demo.db";

    private static Context mApplicationContext;

    private static DatabaseHelper instance;

    private Map<String, Dao> daoMaps = new HashMap<>();

    private DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 2);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, User.class);
            TableUtils.createTable(connectionSource, Article.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, User.class, true);
            TableUtils.dropTable(connectionSource, Article.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void initOrmLite(Context context) {
        mApplicationContext = context;
        getInstance();
    }

    /**
     * 单例获取该Helper
     */
    public static DatabaseHelper getInstance() {
        if (instance == null) {
            synInit(mApplicationContext);
        }
        return instance;
    }

    private synchronized static void synInit(Context context) {
        if (instance == null) {
            instance = new DatabaseHelper(context);
        }
    }

    /**
     * 获得Dao
     *
     * @return
     * @throws SQLException
     */
    public synchronized Dao getDao(Class clazz) throws SQLException {
        Dao dao;
        String className = clazz.getSimpleName();

        if (daoMaps.containsKey(className)) {
            dao = daoMaps.get(className);
        } else {
            dao = super.getDao(clazz);
            daoMaps.put(className, dao);
        }
        return dao;
    }

    /**
     * 释放资源
     */
    @Override
    public void close() {
        super.close();
        for (String key : daoMaps.keySet()) {
            Dao dao = daoMaps.get(key);
            dao = null;
        }
    }

}
