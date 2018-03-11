package com.example.gsr_pc.myproject0324;

import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.LocalServerSocket;
import android.net.LocalSocket;
import android.net.LocalSocketAddress;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
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
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import config.CamMonitorParameter;
import tool.HttpClientUtils;
import util.ActivtyUtil;
import util.DatabaseHelper;

public class MyMenu2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    public static String TAG = "ServerAct";
    protected Spinner spinner;
    private CamMonitorView cmView;
    private Button btnDownload;
    private Button btnLuZhi;
    private Button btnShutDown;
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

    private static final int mVideoEncoder = MediaRecorder.VideoEncoder.H264;
    private LocalSocket receiver, sender;
    private LocalServerSocket lss;
    private MediaRecorder mMediaRecorder = null;
    private boolean mMediaRecorderRecording = false;
    private SurfaceView mSurfaceView = null;
    private SurfaceHolder mSurfaceHolder = null;
    private Thread t;
//    final LuzhiThread luzhiThread = new LuzhiThread(MyMenu2Activity.this);





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_menu2);
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
/////////////////////////////////////////////////////////////////////////////////////////////////////
        try {
            findView();
            setListener();
            fillData();

        } catch (Exception e) {
            Log.e(server.TAG, e.getMessage(), e);
            ActivtyUtil.showAlert(MyMenu2Activity.this, "Error", e.getMessage(), "确定");
        }

        /////////////////////////录制视频/////////////////////////////
    }

    private void fillData() throws Exception{//添加数据
        Intent intent = getIntent();//获取上一页出书的数据
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            userid = bundle.getInt("id");
            param = DatabaseHelper.query(MyMenu2Activity.this, userid);//根据id获取自定义对象
            cmView.setCmPara(param);                             //将信息传入CamMonitorView
        } else {
            throw new Exception("没有发现id");
        }
//        appendMessage("system ready to connect to " + param.getIp());

    }

    private void findView() {
        btnDownload = (Button) findViewById(R.id.btn_save);
        textMessage = (TextView) findViewById(R.id.Message);
        scrollView = (ScrollView) findViewById(R.id.ScrollViewMessage);
        btnDisconnect = (Button) findViewById(R.id.btnDisconnect);
        cmView = (CamMonitorView)findViewById(R.id.cmView);//用于图像显示
//        btnLuZhi = (Button) findViewById(R.id.btnLuZhi);
//        btnShutDown = (Button)findViewById(R.id.btnShutDown);
    }

    private void setListener() {
        btnDownload.setOnClickListener(btnDownloadListener);
        btnDisconnect.setOnClickListener(btnDisConnect);
//        btnLuZhi.setOnClickListener(btnLuzhiListener);
//        btnShutDown.setOnClickListener(btnShutDownListener);
    }

    public View.OnClickListener btnDisConnect = new View.OnClickListener(){

        public void onClick(View v) {
            ///////////////////////线程////////////////////
            Handler handler = new Handler() {

                public void handleMessage(Message msg) {
                    //获取data，data是从服务器端获取的数据（数据以Jason串的形式传到工具类，在工具类里封装成 ArrayList<Map<String,String>>）
                    ArrayList<Map<String, String>> data = (ArrayList<Map<String, String>>) msg.getData().getSerializable("res");
                }
            };

            String url = getResources().getString(R.string.url) + "/userservice/disconnect";

            HttpClientUtils htc = new HttpClientUtils();//调用工具类

            htc.setUrl(url);//设置访问后台（服务器）路径

            htc.setHandler(handler); //调用线程

            htc.start(); //开启线程

            htc.interrupt();//开启后要暂停
            //////////////////////////////////
            cmView.setRunning(false);
            finish();//用于跳到上一个页面
        }

    };
//    public  View.OnClickListener btnLuzhiListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            try {
////                LuzhiThread luzhiThread = new LuzhiThread(MyMenu2Activity.this);
////                Toast.makeText(MyMenu2Activity.this,"开始录制",Toast.LENGTH_SHORT).show();
//                luzhiThread.start();
//            }catch (Exception e){
//
//            }
//        }
//    };
//    public  View.OnClickListener btnShutDownListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            try {
////                LuzhiThread luzhiThread = new LuzhiThread(MyMenu2Activity.this);
////                Toast.makeText(MyMenu2Activity.this,"停止录制",Toast.LENGTH_SHORT).show();
//                luzhiThread.closeThread();
//            }catch (Exception e){
//
//            }
//        }
//    };
    private View.OnClickListener btnDownloadListener =  new View.OnClickListener() {

        public void onClick(View v) {
            try {
                DownloadThread downloadThread = new DownloadThread(MyMenu2Activity.this);
                downloadThread.start();
            }catch (Exception e){

            }
        }

    };

//    class LuzhiThread extends Thread{
//        MyMenu2Activity server = null;
//        public LuzhiThread(MyMenu2Activity mAct){
//            server = mAct;
//        }
//        public synchronized void closeThread() {
//            try {
//                notify();
////                setClose(true);
//                interrupt();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void run() {
//            CamMonitorView.CamMonitorThread cmt = cmView.getThread();
//            try{
//                if(cmt.LuZhi()){
//                    Toast.makeText(MyMenu2Activity.this, "保存成功", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(MyMenu2Activity.this,"保存失败",Toast.LENGTH_SHORT).show();
//                }
//            }catch (Exception e){
//
//            }
//
//        }
//
//
//    };

    class DownloadThread extends Thread{
        MyMenu2Activity server = null;
        public DownloadThread(MyMenu2Activity mAct){
            server = mAct;
        }
        public void run() {
            CamMonitorView.CamMonitorThread cmt = cmView.getThread();
            try{
                cmt.saveImage();
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
        getMenuInflater().inflate(R.menu.my_menu2, menu);
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

            cmView.setRunning(false);//先断开连接

            Intent intent = new Intent(MyMenu2Activity.this, AddConnectActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_gallery) {
            try {
                Intent i = getIntent();
                //接收到上一个页面的传来的产品id
                Bundle b = i.getExtras();
                int id1 = b.getInt("id");

                cmView.setRunning(false);

                Intent intent = new Intent(MyMenu2Activity.this, AddConnectActivity.class);
                intent.putExtra("id", id1);
                startActivity(intent);
            } catch (Exception e) {
                ActivtyUtil.showAlert(MyMenu2Activity.this, "Error", e.getMessage(), "确定");
            }
        } else if (id == R.id.nav_slideshow) {
            Intent intent = new Intent(MyMenu2Activity.this, MyMenuActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {
            Intent intent = new Intent(MyMenu2Activity.this,UserInfoActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_send) {
            Intent intent = new Intent(MyMenu2Activity.this,MainActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
