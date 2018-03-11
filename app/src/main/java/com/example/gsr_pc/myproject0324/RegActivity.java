package com.example.gsr_pc.myproject0324;

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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;

import tool.HttpClientUtils;

public class RegActivity extends AppCompatActivity {
    private EditText regETUname;
    private EditText regETUpwd;
    private  EditText regETUrpwd;
    private Button reg_rewrite;
    private  Button login_btn_reg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
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

        regETUname = (EditText)findViewById(R.id.regETUname);
        regETUpwd = (EditText)findViewById(R.id.regETUpwd);
        regETUrpwd = (EditText)findViewById(R.id.regETUrpwd);
        reg_rewrite = (Button)findViewById(R.id.reg_rewrite);
        login_btn_reg = (Button)findViewById(R.id.login_btn_reg);

        reg_rewrite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                regETUname.setText("");
                regETUpwd.setText("");
                regETUrpwd.setText("");
            }
        });

        login_btn_reg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String uname = regETUname.getText().toString();
                final String upwd = regETUpwd.getText().toString();
                final String urpwd = regETUrpwd.getText().toString();
                final String address = getResources().getString(R.string.url);//主要地址

                if( !(upwd.equals(urpwd)) ){
                    Toast.makeText(RegActivity.this, "您输入的密码和确认密码不正确", Toast.LENGTH_SHORT).show();
                    regETUpwd.setText("");
                    regETUrpwd.setText("");
                }else{

                    //1、验证用户名是否重复
                    //2、如果没有重复的用户名就注册用户
                    //1、
                    Handler handler1 = new Handler(){

                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);
                            ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) msg.getData().getSerializable("res");
                            if(data.get(0).get("state").equals("1")){
                                Toast.makeText(RegActivity.this, "用户名重复", Toast.LENGTH_SHORT).show();
                                regETUname.setText("");
                                regETUpwd.setText("");
                                regETUrpwd.setText("");
                            }
                            if(data.get(0).get("state").equals("2")){
                                Toast.makeText(RegActivity.this, "用户注册成功！！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(RegActivity.this,MainActivity.class);
                                startActivity(intent);
                            }else{
                                Toast.makeText(RegActivity.this,"对不起，注册失败！",Toast.LENGTH_SHORT).show();
                            }
                        }
                    };
                    String url1 = address + "/userservice/isReNameAndReg/"+ uname + ","+ upwd;//验证是否重命名的地址
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
