package com.example.jianglei.ormlitedemo.bean;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by jianglei on 2016/5/26.
 */
@DatabaseTable(tableName = "user")
public class User {

    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField(columnName = "name")
    private String name;
    @DatabaseField(canBeNull = false, columnName = "desc")
    private String desc;
    @DatabaseField(columnName = "name", foreign = true)
    private Article article;

    /*关键啊，一定要加, 为每个class添加一个无参的构造器，并且构造器在包内是可见的*/
    public User() {}

    public User(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

}
