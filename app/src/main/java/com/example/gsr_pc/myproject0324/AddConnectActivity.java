package com.example.gsr_pc.myproject0324;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.test.ActivityUnitTestCase;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import util.ActivtyUtil;
import util.DatabaseHelper;
import util.EditDialog;

public class AddConnectActivity extends AppCompatActivity {
    private Spinner spinner;
    private static final String TAG="AddConnectActivity";

    private Button btnCancle;
    private Button btnSave;

    private EditText editIp;
    private EditText editPort;
    private EditText editPort1;
    //	private EditText editUsername;
//	private EditText editPassword;
    private EditText editClientDir;
    private String name;
    private boolean isModify = false;
    private int id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add);
        spinner = (Spinner) findViewById(R.id.SpinnerFtpType);
        btnSave = (Button) findViewById(R.id.BtnSave);
        btnCancle = (Button) findViewById(R.id.BtnCancle);
        editClientDir = (EditText) findViewById(R.id.FtpLocalDir);
        editIp = (EditText) findViewById(R.id.FtpIp);
        editPort=(EditText) findViewById(R.id.FtpPort);//手机摄像头端口
        editPort1=(EditText) findViewById(R.id.FtpPort1);//连接信息端口

        fillView();
        setListener();


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


    protected void setListener(){

        btnSave.setOnClickListener(btnSaveListener);
        btnCancle.setOnClickListener(btnCancleListener);
    }
    private void fillView(){//预加载信息
        //spinner的填充
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new String[]{"Socket"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);


        if(this.getIntent().getExtras()!= null&&this.getIntent().getExtras().containsKey("id")){//如果有值则说明是修改
            try{
                isModify = true;
                int id = this.getIntent().getExtras().getInt("id");
                this.id = id;

                DatabaseHelper helper = new DatabaseHelper(this);
                Cursor cursor = helper.query(id);
                cursor.moveToFirst();
                this.name = cursor.getString(1);//连接名
                editIp.setText(cursor.getString(2));//ip地址
                editPort.setText(String.valueOf(cursor.getInt(3)));//摄像头连接信息
                editPort1.setText(String.valueOf(cursor.getInt(4)));//摄像头连接信息
                editClientDir.setText(cursor.getString(6));//保存地址

                helper.close();//数据连接关闭
            }catch (Exception e) {
                Toast.makeText(AddConnectActivity.this, "错误", Toast.LENGTH_SHORT).show();
                ActivtyUtil.openToast(AddConnectActivity.this, "错误，错误原因:" + e.getMessage());
            }
        }
    }
    private View.OnClickListener btnSaveListener = new View.OnClickListener(){//点击保存按钮

        public void onClick(View v) {
            if(editIp.getText().toString().trim().length()<=0){
                ActivtyUtil.showAlert(AddConnectActivity.this, getText(R.string.error), getText(R.string.error_ip), getText(R.string.btn_ok));
                return;
            }
            if (editPort.getText().toString().trim().length() <= 0) {
                ActivtyUtil.showAlert(AddConnectActivity.this, getText(R.string.error), getText(R.string.error_port), getText(R.string.btn_ok));
                return;
            }

            String cfgname = editIp.getText().toString();
            if (isModify) {
                cfgname = AddConnectActivity.this.name;
            }
            //用来跳出确认窗口的（Activity的名，标题信息，框里要显示的值，回调方法）
            EditDialog dialog = new EditDialog(AddConnectActivity.this, getText(R.string.CamMonitorConfigActivty_Cfg_Name).toString(), cfgname, saveListener);
            dialog.show();

        }
    };
    private EditDialog.OnDataSetListener saveListener = new EditDialog.OnDataSetListener(){//实现EditDialog的OnDataSetListener接口
        public void onDataSet(String text) {
            try {
                ContentValues contentValue = new ContentValues();//存储值,并根据值修改数据库
                SharedPreferences preferences = getSharedPreferences("user",MODE_PRIVATE);
                String uid = preferences.getString("uid","");

                contentValue.put("uid", uid);
                contentValue.put("name", text);
                contentValue.put("ip", editIp.getText().toString());
                contentValue.put("port",Integer.parseInt(editPort.getText().toString()));
                contentValue.put("port1",Integer.parseInt(editPort1.getText().toString()));

                //获取user信息

                contentValue.put("client_dir", editClientDir.getText().toString());

                if(isModify){//是否是修改的，是则修改
                    DatabaseHelper.update(AddConnectActivity.this, "tb_cammonitor_configs", contentValue,id);
                }else{//不是则添加
                    DatabaseHelper.insert(AddConnectActivity.this, "tb_cammonitor_configs", contentValue);
                }




                setResult(Activity.RESULT_OK);//
                Intent intent = new Intent(AddConnectActivity.this,MyMenuActivity.class);
                startActivity(intent);
//                finish();						//用于跳转





                ActivtyUtil.openToast(AddConnectActivity.this, "保存成成功");
            } catch (Exception e) {
                Log.e(AddConnectActivity.TAG, e.getMessage(), e);
                ActivtyUtil.openToast(AddConnectActivity.this, "错误，错误原因:"+e.getMessage());
            }

        }

    };
    private View.OnClickListener btnCancleListener = new View.OnClickListener(){//取消按钮

        public void onClick(View v) {
            setResult(Activity.RESULT_CANCELED);
            finish();
        }

    };



}
