package tool;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by gsr on 2015/4/9.
 */
public class HttpClientUtils2 extends Thread{
    private String url = "";
    private Handler handler = null;
    private ArrayList<Map<String,String>> result = null;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Handler getHandler() {
        return handler;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    //    public List<Map<String, String>> getResult() {
//        return result;
//    }
//
//    public void setResult(ArrayList<Map<String, String>> result) {
//        this.result = result;
//    }

//    private Handler handler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//            setResult((ArrayList<Map<String,String>) msg.getData().getSerializable("res"));
//        }
//    };//android.os
    @Override
    public void run() {
        //获取地址
        HttpGet request = new HttpGet(url);
        HttpResponse response = null;
        Message message = new Message();
        Bundle bundle = new Bundle();
        message.setData(bundle);
        ArrayList<Map<String,String>> data = new ArrayList<Map<String, String>>();
        try{
            HttpClient client = new DefaultHttpClient();
            response = client.execute(request);
            if(response.getStatusLine().getStatusCode() == 200){
                String resultStr = EntityUtils.toString(response.getEntity(),"UTF-8");//汉子要转字符集
                JSONObject jsonObject = new JSONObject(resultStr);//获得对象第一个key
                Iterator<String> keys = jsonObject.keys();//获得key
                if (keys.hasNext()){
                    String str = jsonObject.getString(keys.next());//获得login
                    if(str.indexOf("[") == 0){
                        JSONArray jsonArray = new JSONArray(str);//login的下的数组
                        for(int i = 0;i < jsonArray.length() ; i++){
                            Map<String,String> jsonMap = new HashMap<String, String>();
                            JSONObject temp = new JSONObject(jsonArray.get(i).toString());//获得values里的key值
                            Iterator<String> tempKeys = temp.keys();
                            while(tempKeys.hasNext()){
                                String key = tempKeys.next();
                                jsonMap.put(key,temp.getString(key));//获得数组里面的values值
                            }
                            data.add(jsonMap);
                        }
                    }else{
                        Map<String,String> jsonMap = new HashMap<String,String>();
                        JSONObject temp = new JSONObject(str);
                        Iterator<String> tempKeys = temp.keys();
                        while(tempKeys.hasNext()){
                            String key = tempKeys.next();
                            jsonMap.put(key,temp.getString(key));
                        }
                        data.add(jsonMap);
                    }



                }
              // System.out.println("sssss ----" + data.get(0).get("state").toString());
              //  System.out.println(" state------- " + data.get("state").toString());
                bundle.putSerializable("res",data);
                handler.sendMessage(message);
                //result有值
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
