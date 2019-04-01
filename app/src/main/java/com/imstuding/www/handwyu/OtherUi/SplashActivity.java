package com.imstuding.www.handwyu.OtherUi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.view.WindowManager;

import com.imstuding.www.handwyu.MainUi.MainActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;

import com.imstuding.www.handwyu.ToolUtil.MyHttpHelp;

public class SplashActivity extends Activity {
    private Handler handler = new Handler();
    private SharedPreferences sharedPreferences;
    private String JSESSIONID = null;
    public static boolean isAutoUpdate;
    public static boolean isShowUpdate;
    public static boolean isUpdateView;
    static String static_version = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏

        //初始化活动
        initActivity();

    }

    public void initActivity() {
        sharedPreferences = getSharedPreferences("userInfo", Context.MODE_PRIVATE);

        isShowUpdate = false;
        isUpdateView = true;
        isAutoUpdate = getSharedPreferences("userInfo", Context.MODE_PRIVATE).getBoolean("autoUpdate", false);

        JSESSIONID = sharedPreferences.getString("JSESSIONID", "");
        static_version = getVersion();
        //测试是否登录
        myTestLoginThread testLoginThread = new myTestLoginThread();
        testLoginThread.start();

        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public String getVersion() {
        String localVersion = null;
        try {
            PackageInfo packageInfo = getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(getPackageName(), 0);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }


    class myTestLoginThread extends Thread {
        @Override
        public void run() {
            try {
                MyHttpHelp httpHelp = new MyHttpHelp("http://202.192.240.29/xxzyxx!xxzyList.action", "post");
                httpHelp.setHeader("Cookie", "JSESSIONID=" + JSESSIONID);
                httpHelp.setHeader("Referer", "http://202.192.240.29/login!welcome.action");

                HttpResponse httpResponse = httpHelp.postRequire(null);
                if (httpResponse.getStatusLine().getStatusCode() == 200) {
                    // 请求和响应都成功了
                    HttpEntity entity = httpResponse.getEntity();
                    String response = EntityUtils.toString(entity, "utf-8");
                    parseJSONWithJSONObject(response);

                }
            } catch (Exception e) {
                Message message = new Message();
                Bundle bundle = new Bundle();
                message.what = 1007;
                bundle.putInt("retcode", 0);
                message.setData(bundle);
                handle.sendMessage(message);
                e.printStackTrace();
            }
        }

        private void parseJSONWithJSONObject(String jsonData) {
            Message message = new Message();
            Bundle bundle = new Bundle();
            try {
                JSONArray jsonArray = new JSONArray(jsonData);

                //把数据发送出去
                message.what = 1007;
                bundle.putInt("retcode", 1);
                message.setData(bundle);
                handle.sendMessage(message);
            } catch (Exception e) {
                message.what = 1007;
                bundle.putInt("retcode", 0);
                message.setData(bundle);
                handle.sendMessage(message);
                e.printStackTrace();
            }
        }
    }


    private final Handler handle = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1005: {//注册
                    Bundle bundle = msg.getData();
                    String ret = bundle.getString("ret");
                    String mgs = bundle.getString("msg");

                    if (ret.equals("true")) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("register", mgs);
                        editor.commit();
                    }
                    break;
                }
                case 1007: {
                    Bundle bundle = msg.getData();
                    int ret = bundle.getInt("retcode");
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    if (ret == 1) {
                        editor.putBoolean("isLogin", true);
                    } else if (ret == 0) {
                        editor.putString("JSESSIONID", "123456789");
                        editor.putBoolean("isLogin", false);
                    }
                    editor.commit();
                    break;
                }
            }

        }

    };
}