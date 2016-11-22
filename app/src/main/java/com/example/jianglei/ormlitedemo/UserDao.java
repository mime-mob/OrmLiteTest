package com.example.jianglei.ormlitedemo;

import com.example.jianglei.ormlitedemo.bean.User;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jianglei on 2016/5/26.
 */
public class UserDao {

    public static UserDao mUserDaoInstance;

    private Dao<User, Integer> mUserDao;

    public UserDao() {
        try {
            mUserDao = DatabaseHelper.getInstance().getDao(User.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static UserDao getInstance() {
        if (mUserDaoInstance == null) {
            mUserDaoInstance = new UserDao();
        }
        return mUserDaoInstance;
    }

    /**
     * 单条插入数据
     */
    public void insertUser(User user) {
        try {
            mUserDao.create(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 多条插入数据
     */
    public void insertUsers(List<User> users) {
        try {
            mUserDao.create(users);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查询所有数据
     */
    public List<User> queryAllUser() {
        List<User> users = new ArrayList<>();
        try {
            users = mUserDao.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    /**
     * 通过id查询数据
     */
    public User queryUserById(int id) {
        User user = null;
        try {
            user = mUserDao.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    /**
     * 删除该id的数据
     */
    public void deleteUserById(int id) {
        try {
            mUserDao.deleteById(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除这些id的数据
     */
    public void deleteUserByIds(List<Integer> ids) {
        try {
            mUserDao.deleteIds(ids);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除所有
     */
    public void deleteAllUser() {
        try {
            mUserDao.deleteBuilder().delete();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新当前实体类数据
     */
    public void updateUser(User user) {
        try {
            mUserDao.update(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新当前数据的id
     */
    public void updateUserById(User user, int id) {
        try {
            mUserDao.updateId(user, id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 自定义查询
     */
    public List<User> queryBy() throws SQLException {
        QueryBuilder<User, Integer> queryBuilder = mUserDao
                .queryBuilder();
        Where<User, Integer> where = queryBuilder.where();
        where.eq("user_id", 1);
        where.and();
        where.eq("name", "xxx");

        //或者
        mUserDao.queryBuilder().
                where().
                eq("user_id", 1).and().
                eq("name", "xxx");
        return queryBuilder.query();
    }
}
