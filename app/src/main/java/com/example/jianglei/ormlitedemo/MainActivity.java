package com.example.jianglei.ormlitedemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.example.jianglei.ormlitedemo.bean.User;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button button1;
    private Button button2;
    private Button button3;
    private Button button4;
    private ListView listView;

    private MyAdapter adapter;

    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = (Button) findViewById(R.id.btn1);
        button2 = (Button) findViewById(R.id.btn2);
        button3 = (Button) findViewById(R.id.btn3);
        button4 = (Button) findViewById(R.id.btn4);
        listView = (ListView) findViewById(R.id.lv);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);

        users = new ArrayList<>();
        adapter = new MyAdapter(this, users);
        listView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn1:
                insert();
                query();
                break;
            case R.id.btn2:
                queryById();
                break;
            case R.id.btn3:
                update();
                query();
                break;
            case R.id.btn4:
                delete();
                query();
                break;
            default:break;
        }
    }

    private void queryById() {
        users.clear();
        users.add(UserDao.getInstance().queryUserById(1));
        adapter.setUsers(users);
        adapter.notifyDataSetChanged();
    }

    private void insert() {
        User user = new User("江磊", "金陵小霸王");
        UserDao.getInstance().insertUser(user);
    }

    private void query() {
        users = UserDao.getInstance().queryAllUser();
        adapter.setUsers(users);
        adapter.notifyDataSetChanged();
    }

    private void delete() {
        UserDao.getInstance().deleteAllUser();
    }

    private void update() {
        User user = new User("江磊", "金陵小霸王 猴赛雷");
        user.setId(1);
        UserDao.getInstance().updateUserById(user, 10);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        DatabaseHelper.getInstance().close();
    }
}
