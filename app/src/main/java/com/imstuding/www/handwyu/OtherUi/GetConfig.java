package com.imstuding.www.handwyu.OtherUi;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.imstuding.www.handwyu.ToolUtil.UrlUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by yangkui on 2018/9/9.
 */

public class GetConfig {
    private final Context mContext;
    public final UrlUtil util;
    private final String testAdUrl =null;
    private Bitmap bmVerifation=null;
    private String m_adFlag;
    private final SharedPreferences sharedPreferences;
    public GetConfig(Context context){
        mContext=context;
        //初始化配置文件
        util=new UrlUtil(mContext);
        sharedPreferences=mContext.getSharedPreferences("userInfo", Context.MODE_PRIVATE);
    }

    public void update(){
        myTestAdThread testAdThread=new myTestAdThread();
        testAdThread.start();
    }

    class myTestAdThread extends Thread {
        @Override
        public void run() {
            try {
                HttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet(testAdUrl);
                httpGet.setHeader("Accept", "*/*");
                httpGet.setHeader("Connection", "keep-alive");
                httpGet.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/63.0.3239.132 Safari/537.36");
                HttpResponse httpResponse = httpClient.execute(httpGet);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    // 请求和响应都成功了
                    HttpEntity entity = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity, "utf-8");
                    parseJSONWithJSONObject(response);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void parseJSONWithJSONObject(String jsonData) {
            try {
                JSONArray jsonArray = new JSONArray(jsonData);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    String adFlag=jsonObject.getString("adFlag");
                    String adUrl=jsonObject.getString("adUrl");
                    int showAdTime=jsonObject.getInt("showAdTime");
                    String testAdUrl=jsonObject.getString("testAdUrl");
                    boolean redPaper=jsonObject.getBoolean("redPaper");
                    String libraryUrl=jsonObject.getString("libraryUrl");
                    String busUrl=jsonObject.getString("busUrl");
                    String schoolDateUrl=jsonObject.getString("schoolDateUrl");
                    String youDaoUrl=jsonObject.getString("youDaoUrl");

                    //把数据发送出去
                    Message message=new Message();
                    Bundle bundle=new Bundle();
                    bundle.putString("adFlag",adFlag);
                    bundle.putString("adUrl",adUrl);
                    bundle.putInt("showAdTime",showAdTime);
                    bundle.putString("testAdUrl",testAdUrl);
                    bundle.putBoolean("redPaper",redPaper);
                    bundle.putString("libraryUrl",libraryUrl);
                    bundle.putString("busUrl",busUrl);
                    bundle.putString("schoolDateUrl",schoolDateUrl);
                    bundle.putString("youDaoUrl",youDaoUrl);

                    message.setData(bundle);
                    message.what=1004;
                    handle.sendMessage(message);//获取验证码
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1003: {//获取广告
                    break;
                }
                case 1004: {//测试是否更新广告
                    Bundle bundle = msg.getData();
                    String adFlag = bundle.getString("adFlag");
                    String adUrl = bundle.getString("adUrl");
                    int showAdTime = bundle.getInt("showAdTime");
                    boolean redPaper = bundle.getBoolean("redPaper");
                    String libraryUrl = bundle.getString("libraryUrl");
                    String busUrl = bundle.getString("busUrl");
                    String schoolDateUrl = bundle.getString("schoolDateUrl");
                    String youDaoUrl = bundle.getString("youDaoUrl");


                    SharedPreferences.Editor editor = sharedPreferences.edit();

                    editor.putInt("showAdTime", showAdTime);
                    editor.putBoolean("redPaper", redPaper);

                    util.setUpdateUrl(libraryUrl, busUrl, schoolDateUrl, youDaoUrl);
                    editor.commit();
                    break;
                }
            }
        }
    };
}
