package com.example.gsr_pc.myproject0324;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import java.util.ArrayList;
import java.util.Map;
import android.os.Handler;

import config.User;
import tool.HttpClientUtils;
import tool.HttpClientUtils2;


public class MainActivity extends AppCompatActivity {
    private Button btnReg;
    private Button btnLogin;
    private EditText etUName;
    private EditText etUPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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


        btnReg = (Button) findViewById(R.id.btnReg);
        btnLogin = (Button)findViewById(R.id.btnLogin);
        etUName = (EditText)findViewById(R.id.etUName);
        etUPwd = (EditText)findViewById(R.id.etUPwd);

        SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);//json串的名字
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();//清空数据

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String uname = etUName.getText().toString().trim();//获取用户名，密码字符数据
                String upwd = etUPwd.getText().toString().trim();

                ///////////////////////线程////////////////////
                Handler handler = new Handler() {

                    public void handleMessage(Message msg) {
                        //获取data，data是从服务器端获取的数据（数据以Jason串的形式传到工具类，在工具类里封装成 ArrayList<Map<String,String>>）
                        ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) msg.getData().getSerializable("res");
                        Toast.makeText(MainActivity.this,data.toString(),Toast.LENGTH_SHORT).show();
                        //data===[{"uid":"6","uname":"a","upwd":"a"},{awdqeqwe}]
                        //{"reUser":{"uid":"6","uname":"a","upwd":"a"}}
                        String uid = data.get(0).get("uid");
                        if ((!uid.equals("0")) && (data.size() == 1) ) {//当id不是0且数据大小是一的时候
                        //验证返回信息，来判断用户是否注册过
                            //跳转主页面
                            //用于判断是否登陆
                            //用于页面之间的数据传输

                            SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);//json串的名字
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            //将数据 data.get(0).get("uid") 放到“uid” 的容器里。用于以后调用

                            Toast.makeText(MainActivity.this,data.get(0).toString(),Toast.LENGTH_SHORT).show();
//                            String name = data.get(0).get("uname");
//                            String id = data.get(0).get("uid");
//                            User user = new User(id,name);
                            editor.putString("uid", data.get(0).get("uid"));
                            editor.putString("uname", data.get(0).get("uname"));
                            editor.commit();
                            Intent intent = new Intent();//页面跳转
                            intent.setClass(MainActivity.this, MyMenuActivity.class);
                            startActivity(intent);

                        } else {
                            Toast.makeText(MainActivity.this, "对不起登陆错误", Toast.LENGTH_SHORT).show();
                            etUPwd.setText("");
                            etUName.setText("");
//                            Intent intent = new Intent();
//                            intent.setClass(MainActivity.this, ErrorActivity.class);
//                            startActivity(intent);

                            //
                        }
                    }
                };
                ////////////////////////线程/////////////////////
                //将前台获取的姓名和密码，拼成访问服务器的地址字符串

                String url = getResources().getString(R.string.url) + "/userservice/getuser/" + uname + "," + upwd;

                HttpClientUtils htc = new HttpClientUtils();//调用工具类

                htc.setUrl(url);//设置访问后台（服务器）路径

                htc.setHandler(handler); //调用线程

                htc.start(); //开启线程

                htc.interrupt();//开启后要暂停

            }
        });
        btnReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
//                intent.setClass(MainActivity.this,RegActivity.class);
                intent.setClass(MainActivity.this,RegActivity.class);
                startActivity(intent);
            }
        });



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
}
