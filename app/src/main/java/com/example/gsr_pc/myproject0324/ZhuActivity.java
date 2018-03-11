package com.example.gsr_pc.myproject0324;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
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
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import tool.HttpClientUtils;
import util.ActivtyUtil;
import util.DatabaseHelper;

public class ZhuActivity extends AppCompatActivity {
    public final static String TAGaa = "CamMonitorClient";
    protected TextView view;
//    protected DatabaseHelper helper;
    protected Spinner spinner;

    protected Button btnAdd;
    protected Button btnModify;
    protected Button btnDelete;
    protected Button btnConnect;
    private SimpleCursorAdapter adapter;//数据库的调用
    private Cursor cursor;//游标
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_zhu);
            spinner = (Spinner) findViewById(R.id.SpinnerLdapConfig);
            btnAdd = (Button) findViewById(R.id.BtnNew);
            btnConnect = (Button) findViewById(R.id.BtnConnect);
            btnModify = (Button) findViewById(R.id.BtnModify);
            btnDelete = (Button) findViewById(R.id.BtnDelete);

            fillDataWithCursor();//读取手机数据库的数据并显示
            setListenner();//点击事件
        } catch (Exception e) {
            ActivtyUtil.showAlert(ZhuActivity.this, "Error", e.getMessage(), "确定");
        }
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










    }
    private void fillDataWithCursor() throws Exception {//调用数据库，插曲数据
        DatabaseHelper helper = new DatabaseHelper(this);
        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);//可以传输多个！！！！！！
        String uid = preferences.getString("uid", "");
        Integer id = Integer.parseInt(uid);
        cursor = helper.loadAllName(id);
        int count = cursor.getCount();
//        Toast.makeText(ZhuActivity.this,"chang===" + count,Toast.LENGTH_SHORT).show();
        cursor.moveToFirst();//定位定位第一行
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item, cursor,
                new String[] { "name" },
                new int[] { android.R.id.text1 });
        adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);//显示数据库导的数据
        spinner.refreshDrawableState();
        helper.close();

        if (count <= 0) {
            btnConnect.setEnabled(false);
            btnDelete.setEnabled(false);
            btnModify.setEnabled(false);
            spinner.setEnabled(false);
        } else {
            btnConnect.setEnabled(true);
            btnDelete.setEnabled(true);
            btnModify.setEnabled(true);
            spinner.setEnabled(true);
        }
    }
    private void setListenner() {
        btnAdd.setOnClickListener(btnAddListener);
        btnModify.setOnClickListener(btnModifyListener);
        btnDelete.setOnClickListener(btnDeleteListener);
        btnConnect.setOnClickListener(btnConnectListener);
    }
    private View.OnClickListener btnAddListener = new View.OnClickListener() {

        public void onClick(View v) {
            Intent intent = new Intent(ZhuActivity.this, AddConnectActivity.class);
            startActivityForResult(intent, 0);
        }
    };
    private View.OnClickListener btnModifyListener = new View.OnClickListener() {//修改

        public void onClick(View v) {
            try {
                Cursor cc = (Cursor) spinner.getSelectedItem();
                int id = cc.getInt(0);
                Intent intent = new Intent(ZhuActivity.this, AddConnectActivity.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 1);//修改完了以后将修改后的数据传回这个页面
                // DatabaseHelper.testInsert(CamMonitorClient.this);
                // fillData();
            } catch (Exception e) {
                Log.e(TAGaa, e.getMessage(), e);
                ActivtyUtil.showAlert(ZhuActivity.this, "Error", e.getMessage(), "确定");
            }
        }

    };
    private View.OnClickListener btnDeleteListener = new View.OnClickListener() {

        public void onClick(View v) {
            try {
                Cursor cc = (Cursor) spinner.getSelectedItem();
                final int id = cc.getInt(0);
                String name = cc.getString(1);
                new AlertDialog.Builder(ZhuActivity.this).setTitle("确定删除吗？")
                        .setMessage("确定删除吗？" + name + "吗？").setPositiveButton(
                        "确定", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    DatabaseHelper//对保存数据的操作
                                            .delete(ZhuActivity.this, id);
                                    fillDataWithCursor();
                                    ActivtyUtil.openToast(ZhuActivity.this,
                                            "删除成功!");
                                } catch (Exception e) {
                                    Log.e(TAGaa, e.getMessage(), e);
                                    ActivtyUtil.openToast(ZhuActivity.this, e
                                            .getMessage());
                                }
                            }
                        }).setNegativeButton("取消",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,
                                                int which) {

                            }

                        }).show();
                // DatabaseHelper.Delete(CamMonitorClient.this, name);
            } catch (Exception e) {
                ActivtyUtil.showAlert(ZhuActivity.this, "Error", e.getMessage(), "确定");
            }
        }

    };
    private View.OnClickListener btnConnectListener = new View.OnClickListener() {//连接按钮

        public void onClick(View v) {
            try {
                Cursor cc = (Cursor) spinner.getSelectedItem();
                int id = cc.getInt(0);

///////////////////////线程////////////////////
                Handler handler = new Handler() {

                    public void handleMessage(Message msg) {
                        //获取data，data是从服务器端获取的数据（数据以Jason串的形式传到工具类，在工具类里封装成 ArrayList<Map<String,String>>）
                        ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) msg.getData().getSerializable("res");
                        }
                    };

                String url = getResources().getString(R.string.url) + "/userservice/connect";

                HttpClientUtils htc = new HttpClientUtils();//调用工具类

                htc.setUrl(url);//设置访问后台（服务器）路径

                htc.setHandler(handler); //调用线程

                htc.start(); //开启线程

                htc.interrupt();//开启后要暂停

                Intent intent = new Intent(ZhuActivity.this, server.class);
                intent.putExtra("id", id);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAGaa, e.getMessage(), e);
                ActivtyUtil.showAlert(ZhuActivity.this, "Error", e.getMessage(), "确定");
            }
        }

    };

}
