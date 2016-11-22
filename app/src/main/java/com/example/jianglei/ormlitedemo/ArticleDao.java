package com.example.jianglei.ormlitedemo;

import com.example.jianglei.ormlitedemo.bean.Article;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by jianglei on 2016/6/1.
 */
public class ArticleDao {
    private Dao<Article, Integer> articleDaoOpe;
    private DatabaseHelper helper;

    @SuppressWarnings("unchecked")
    public ArticleDao() {
        try {
            helper = DatabaseHelper.getInstance();
            articleDaoOpe = helper.getDao(Article.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加一个Article
     *
     * @param article
     */
    public void add(Article article) {
        try {
            articleDaoOpe.create(article);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过Id得到一个Article
     *
     * @param id
     * @return
     */
    @SuppressWarnings("unchecked")
    public Article getArticleWithUser(int id) {
        Article article = null;
        try {
            article = articleDaoOpe.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return article;
    }

    /**
     * 通过Id得到一篇文章
     *
     * @param id
     * @return
     */
    public Article get(int id) {
        Article article = null;
        try {
            article = articleDaoOpe.queryForId(id);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return article;
    }

    /**
     * 通过UserId获取所有的文章
     *
     * @param userId
     * @return
     */
    public List<Article> listByUserId(int userId) {
        try {
            return articleDaoOpe.queryBuilder().where().eq("user_id", userId)
                    .query();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
