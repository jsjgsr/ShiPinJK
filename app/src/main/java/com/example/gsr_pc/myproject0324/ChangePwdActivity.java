package com.example.gsr_pc.myproject0324;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import tool.HttpClientUtils;

public class ChangePwdActivity extends AppCompatActivity {
    private TextView etChaPwd;
    private TextView etChaNewPwd;
    private TextView etChaReNewPwd;
    private Button btnReWrite;
    private Button btnToChaPwd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pwd);
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


        etChaPwd = (TextView)findViewById(R.id.etChaPwd);
        etChaNewPwd = (TextView)findViewById(R.id.etChaNewPwd);
        etChaReNewPwd = (TextView)findViewById(R.id.etChaReNewPwd);
        btnReWrite = (Button)findViewById(R.id.btnReWrite);
        btnToChaPwd = (Button)findViewById(R.id.btnToChaPwd);


        btnReWrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etChaPwd.setText("");
                etChaNewPwd.setText("");
                etChaReNewPwd.setText("");
            }
        });




        btnToChaPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences = getSharedPreferences("user", MODE_PRIVATE);//可以传输多个！！！！！！
                String uid =  preferences.getString("uid", "");
                String pwd = etChaPwd.getText().toString();
                String npwd = etChaNewPwd.getText().toString();
                String rnpwd = etChaReNewPwd.getText().toString();

                if(!npwd.equals(rnpwd)){
                    Toast.makeText(ChangePwdActivity.this,"新密码与重复密码不一致",Toast.LENGTH_SHORT).show();
                    etChaNewPwd.setText("");
                    etChaReNewPwd.setText("");
                }
                else if(pwd.equals(npwd)){
                    Toast.makeText(ChangePwdActivity.this,"新密码与原密码不能一致",Toast.LENGTH_SHORT).show();
                    etChaPwd.setText("");
                    etChaNewPwd.setText("");
                    etChaReNewPwd.setText("");
                }
                else if((npwd.equals(rnpwd)) || (!pwd.equals(npwd))){


                    //1、验证用户名是否重复
                    //2、如果没有重复的用户名就注册用户
                    //1、
                    Handler handler1 = new Handler(){

                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) msg.getData().getSerializable("res");
                            if(data.get(0).get("state").equals("3")){
                                Toast.makeText(ChangePwdActivity.this, "用户原密码不正确", Toast.LENGTH_SHORT).show();
                                etChaPwd.setText("");
                                etChaNewPwd.setText("");
                                etChaReNewPwd.setText("");
                            }
                            if(data.get(0).get("state").equals("1")){
                                Toast.makeText(ChangePwdActivity.this, "用户修改密码注册成功！！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ChangePwdActivity.this,MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(ChangePwdActivity.this,"对不起，修改失败！",Toast.LENGTH_SHORT).show();
                                etChaPwd.setText("");
                                etChaNewPwd.setText("");
                                etChaReNewPwd.setText("");
                            }
                        }
                    };
                    String url1 = getResources().getString(R.string.url) + "/userservice/chaUpwd/"+ uid + ","+ pwd + "," + npwd;//验证是否重命名的地址
                    HttpClientUtils htc1 = new HttpClientUtils();
                    htc1.setHandler(handler1);
                    htc1.setUrl(url1);
                    htc1.start();
                    htc1.interrupt();
                }
            }
        });
    }

}
