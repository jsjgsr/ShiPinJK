package com.example.gsr_pc.myproject0324;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import tool.HttpClientUtils;
import util.ActivtyUtil;
import util.DatabaseHelper;
import util.EditDialog;

public class UserInfoActivity extends AppCompatActivity {
    private TextView uname_cha;
    private Button btnChaUname;

    private Button btnChaPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        uname_cha = (TextView)findViewById(R.id.uname_cha);
        btnChaUname = (Button)findViewById(R.id.btnChaUname);
        btnChaPwd = (Button)findViewById(R.id.btnChaPwd);

        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);//可以传输多个！！！！！！
        String uname = preferences.getString("uname", "");

        uname_cha.setText(uname);

        btnChaUname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用来跳出确认窗口的（Activity的名，标题信息，框里要显示的值，回调方法）
                EditDialog dialog = new EditDialog(UserInfoActivity.this, "请输入新名字", "", ahangeUnameListener);
                dialog.show();
            }
        });


        btnChaPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Bundle bundle = new Bundle()
                Intent intent = new Intent(UserInfoActivity.this,ChangePwdActivity.class);
                startActivity(intent);
            }
        });
    }
    private EditDialog.OnDataSetListener ahangeUnameListener = new EditDialog.OnDataSetListener(){//实现EditDialog的OnDataSetListener接口
        public void onDataSet(String text) {
            try {
                ////////////////////////////////////////启动修改线程///////////////////////////////////////
                Handler handler = new Handler() {

                    public void handleMessage(Message msg) {
                        //获取data，data是从服务器端获取的数据（数据以Jason串的形式传到工具类，在工具类里封装成 ArrayList<Map<String,String>>）
                        ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) msg.getData().getSerializable("res");
                        Toast.makeText(UserInfoActivity.this, "数据大小" + data, Toast.LENGTH_SHORT).show();
                        String state = data.get(0).get("state");
                        if ((state.equals("1"))) {//当id不是0且数据大小是一的时候
                            Toast.makeText(UserInfoActivity.this,"修改成功",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();//页面跳转
                            intent.setClass(UserInfoActivity.this, MainActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(UserInfoActivity.this, "修改失败", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent();
                            intent.setClass(UserInfoActivity.this, UserInfoActivity.class);
                            startActivity(intent);
                        }
                    }
                };
                ////////////////////////线程/////////////////////
                //将前台获取的姓名和密码，拼成访问服务器的地址字符串
                final SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);//可以传输多个！！！！！！
                final String uid = preferences.getString("uid", "");
                final String uname = preferences.getString("uname", "");
                String url = getResources().getString(R.string.url) + "/userservice/chaUname/" + uid + "," + text;

                HttpClientUtils htc = new HttpClientUtils();//调用工具类

                htc.setUrl(url);//设置访问后台（服务器）路径

                htc.setHandler(handler); //调用线程

                htc.start(); //开启线程

                htc.interrupt();//开启后要暂停
            } catch (Exception e) {

                ActivtyUtil.openToast(UserInfoActivity.this, "错误，错误原因:"+e.getMessage());
            }

        }

    };
}
