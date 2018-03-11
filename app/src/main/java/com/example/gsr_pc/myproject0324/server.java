package com.example.gsr_pc.myproject0324;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import config.CamMonitorParameter;
import util.ActivtyUtil;
import util.DatabaseHelper;

public class server extends AppCompatActivity {
    public static String TAG = "ServerAct";

    private CamMonitorView cmView;
    private Button btnDownload;
    private TextView textMessage;
    private Button btnDisconnect;

    private CamMonitorParameter param;
    public static final int TRY_TIMES = 3;
    public int current_times = 1;

    private int userid;

    private ScrollView scrollView;

    public Handler handler = new Handler();

    public List<String> downloadList = new ArrayList<String>();

    public ProgressDialog downloadProgressDialog = null;
    private Thread mThread;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);
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

        try {
            findView();
            setListener();
            fillData();

        } catch (Exception e) {
            Log.e(server.TAG, e.getMessage(), e);
            ActivtyUtil.showAlert(server.this, "Error", e.getMessage(), "确定");
        }

    }
    private void fillData() throws Exception{//添加数据

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        if (bundle != null) {

            userid = bundle.getInt("id");
            param = DatabaseHelper.query(server.this, userid);//根据id获取自定义对象
            cmView.setCmPara(param);                             //进行图像联机

        } else {
            throw new Exception("没有发现id");
        }
        appendMessage("system ready to connect to " + param.getIp());

    }

    private void findView() {
        btnDownload = (Button) findViewById(R.id.btn_save);
        textMessage = (TextView) findViewById(R.id.Message);
        scrollView = (ScrollView) findViewById(R.id.ScrollViewMessage);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        cmView = (CamMonitorView)findViewById(R.id.cmView);//用于图像显示

    }

    private void setListener() {
        btnDownload.setOnClickListener(btnDownloadListener);
        btnDisconnect.setOnClickListener(btnDisConnect);
    }

    public View.OnClickListener btnDisConnect = new View.OnClickListener(){

        public void onClick(View v) {

            cmView.setRunning(false);

            finish();
        }

    };

    private View.OnClickListener btnDownloadListener =  new View.OnClickListener() {

        public void onClick(View v) {
            try {
                DownloadThread downloadThread = new DownloadThread(server.this);
                downloadThread.start();
            }catch (Exception e){

            }
        }

    };



    class DownloadThread extends Thread{
        server server = null;
        public DownloadThread(server mAct){
            server = mAct;
        }
        public void run() {
            CamMonitorView.CamMonitorThread cmt = cmView.getThread();
            try{
                if(cmt.saveImage()){
                    Toast.makeText(server.this,"保存成功",Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(server.this,"保存失败",Toast.LENGTH_SHORT).show();
                }
            }catch (Exception e){

            }

        }


    };



    public void appendMessage(final String message) {
        textMessage.setText(textMessage.getText() + message);
        scrollView.post(new Runnable() {
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }

        });
    }

    public void showStatus(final String mstatus){
        handler.post(new Runnable(){

            public void run() {
                if("LogIn".equalsIgnoreCase(mstatus)){
                    downloadProgressDialog.dismiss();
                }else{
                    downloadProgressDialog.setMessage(  mstatus + "..." );
                }

            }

        });
    }


    private boolean checkEndsWithInStringArray(String checkItsEnd, String[] fileEndings) {
        for (String aEnd : fileEndings) {
            if (checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }



}
