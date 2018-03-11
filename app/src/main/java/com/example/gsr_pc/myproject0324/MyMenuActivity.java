package com.example.gsr_pc.myproject0324;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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

public class MyMenuActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public final static String TAGaa = "CamMonitorClient";
    protected TextView view;
    //    protected DatabaseHelper helper;
    protected Spinner spinner;

    protected Button btnAdd;
    protected Button btnModify;
    protected Button btnDelete;
    protected Button btnConnect;
    private SimpleCursorAdapter adapter;//数据库的调用后的前台数据显示
    private Cursor cursor;//游标
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu);
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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);



        try {
            spinner = (Spinner) findViewById(R.id.SpinnerLdapConfig);
            btnAdd = (Button) findViewById(R.id.BtnNew);
            btnConnect = (Button) findViewById(R.id.BtnConnect);
            btnModify = (Button) findViewById(R.id.BtnModify);
            btnDelete = (Button) findViewById(R.id.BtnDelete);

            fillDataWithCursor();//读取手机数据库的数据并显示
            setListenner();//点击事件



        } catch (Exception e) {
            ActivtyUtil.showAlert(MyMenuActivity.this, "Error", e.getMessage(), "确定");
        }


    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.add_connect) {
            // Handle the camera action
            Intent intent = new Intent(MyMenuActivity.this, AddConnectActivity.class);
            startActivityForResult(intent, 0);
        } else if (id == R.id.nav_gallery) {
            try {
                Cursor cc = (Cursor) spinner.getSelectedItem();
                int id1 = cc.getInt(0);
                Intent intent = new Intent(MyMenuActivity.this, AddConnectActivity.class);
                intent.putExtra("id", id1);
                startActivityForResult(intent, 1);//修改完了以后将修改后的数据传回这个页面
                // DatabaseHelper.testInsert(CamMonitorClient.this);
                // fillData();
            } catch (Exception e) {
                Log.e(TAGaa, e.getMessage(), e);
                ActivtyUtil.showAlert(MyMenuActivity.this, "Error", e.getMessage(), "确定");
            }
        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MyMenuActivity.this,UserInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MyMenuActivity.this,MainActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void fillDataWithCursor() throws Exception {//调用数据库，插曲数据

        DatabaseHelper helper = new DatabaseHelper(this);

        SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);//可以传输多个！！！！！！
        String uid = preferences.getString("uid", "");
        Integer id = Integer.parseInt(uid);

        cursor = helper.loadAllName(id);//有id,连接名，端口号属性

        int count = cursor.getCount();//数据的个数
//        Toast.makeText(ZhuActivity.this,"chang===" + count,Toast.LENGTH_SHORT).show();
        cursor.moveToFirst();//定位定位第一行
        //第一个当前类名.第二个要显示的布局文件,第三个信息,第四个哪一个属性用来显示,第五个各键值的值要显示的位置
        adapter = new SimpleCursorAdapter(this,
                android.R.layout.simple_spinner_item,
                cursor,
                new String[] { "name" },
                new int[] { android.R.id.text1 });
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);//定义样式
        spinner.setAdapter(adapter);//显示数据库导的数据
        spinner.refreshDrawableState();//刷新Android的列表视图
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
            Intent intent = new Intent(MyMenuActivity.this, AddConnectActivity.class);
            startActivityForResult(intent, 0);
        }
    };
    private View.OnClickListener btnModifyListener = new View.OnClickListener() {//修改

        public void onClick(View v) {
            try {
                Cursor cc = (Cursor) spinner.getSelectedItem();//获取下拉列表的当前值
                int id = cc.getInt(0);
                Intent intent = new Intent(MyMenuActivity.this, AddConnectActivity.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, 1);//修改完了以后将修改后的数据传回这个页面
                // DatabaseHelper.testInsert(CamMonitorClient.this);
                // fillData();
            } catch (Exception e) {
                Log.e(TAGaa, e.getMessage(), e);
                ActivtyUtil.showAlert(MyMenuActivity.this, "Error", e.getMessage(), "确定");
            }
        }

    };
    private View.OnClickListener btnDeleteListener = new View.OnClickListener() {

        public void onClick(View v) {
            try {
                Cursor cc = (Cursor) spinner.getSelectedItem();
                final int id = cc.getInt(0);
                String name = cc.getString(1);
                new AlertDialog.Builder(MyMenuActivity.this).setTitle("确定删除吗？").setMessage("确定删除吗？" + name + "吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog,
                                                int which) {
                                try {
                                    DatabaseHelper//对保存数据的操作
                                            .delete(MyMenuActivity.this, id);
                                    fillDataWithCursor();
                                    ActivtyUtil.openToast(MyMenuActivity.this,
                                            "删除成功!");
                                } catch (Exception e) {
                                    Log.e(TAGaa, e.getMessage(), e);
                                    ActivtyUtil.openToast(MyMenuActivity.this, e
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
                ActivtyUtil.showAlert(MyMenuActivity.this, "Error", e.getMessage(), "确定");
            }
        }

    };
    private View.OnClickListener btnConnectListener = new View.OnClickListener() {//连接按钮

        public void onClick(View v) {
             final String connectState="2";
            Toast.makeText(MyMenuActivity.this,"连接成功！",Toast.LENGTH_SHORT).show();
            try {
                Cursor cc = (Cursor) spinner.getSelectedItem();
                int id = cc.getInt(0);

                String port = cc.getString(2);///摄像头端口
                String port1 = cc.getString(3);//连接端口
                ///////////////////////线程////////////////////
                Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        //获取data，data是从服务器端获取的数据（数据以Jason串的形式传到工具类，在工具类里封装成 ArrayList<Map<String,String>>）
                        ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) msg.getData().getSerializable("res");

                    }
                };

                String url = getResources().getString(R.string.url) + "/userservice/connect/"+port + ","+port1;

                HttpClientUtils htc = new HttpClientUtils();//调用工具类

                htc.setUrl(url);//设置访问后台（服务器）路径

                htc.setHandler(handler); //调用线程

                htc.start(); //开启线程

                htc.interrupt();//开启后要暂停
//                Intent intent = new Intent(MyMenuActivity.this, server.class);
                Intent intent = new Intent(MyMenuActivity.this, MyMenu2Activity.class);
                intent.putExtra("id", id);
                startActivity(intent);
            } catch (Exception e) {
                Log.e(TAGaa, e.getMessage(), e);
                ActivtyUtil.showAlert(MyMenuActivity.this, "Error", e.getMessage(), "确定");
            }
        }

    };
}
