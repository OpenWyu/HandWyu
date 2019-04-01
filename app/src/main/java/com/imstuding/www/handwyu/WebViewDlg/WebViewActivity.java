package com.imstuding.www.handwyu.WebViewDlg;

import android.annotation.SuppressLint;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.view.KeyEvent;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.imstuding.www.handwyu.R;
import com.imstuding.www.handwyu.ToolUtil.TitleView;

import java.lang.reflect.Field;

public class WebViewActivity extends AppCompatActivity {

    private WebView webView;
   // private MyLoadDlg myLoadDlg;
    private String url;
    private Button webback;
    private Button webgo;
    private Button webclose;
    private ProgressBar pg1;
    public TitleView titleView;
    public ImageView imageView_menu;
    public ImageView imageView_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_web_view);
        try{
            url=getIntent().getStringExtra("url");
        }catch (Exception e){
            url="http://www.wyu.edu.cn/";
            Toast.makeText(WebViewActivity.this,"当你看到这个网页的时候，说明app出错了，请去提bug，谢谢",Toast.LENGTH_SHORT).show();
        }
        initActivity();
    }

    private void myPopMenu(){
        imageView_back= findViewById(R.id.title_back);
        imageView_menu= findViewById(R.id.title_menu);
        titleView= findViewById(R.id.title_webview);

        titleView.setTitleText("掌上邑大");

        imageView_menu.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(WebViewActivity.this, v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.web_menu, popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.web_menu_update:{
                                webView.reload();
                                break;
                            }
                            default:
                                break;
                        }
                        return false;
                    }
                });

                //使用反射，强制显示菜单图标
                try {
                    Field field = popup.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    MenuPopupHelper helper = (MenuPopupHelper) field.get(popup);
                    helper.setForceShowIcon(true);
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }catch (Exception e){
                    e.printStackTrace();
                }
                popup.show();
            }
        });
    }

    public void initActivity(){
        myPopMenu();
        pg1 = findViewById(R.id.progressBar1);
        webback= findViewById(R.id.webback);
        webgo= findViewById(R.id.webgo);
        webclose= findViewById(R.id.webclose);

        webclose.setOnClickListener(new MyClickListener());
        webback.setOnClickListener(new MyClickListener());
        webgo.setOnClickListener(new MyClickListener());

       // myLoadDlg=new MyLoadDlg(this);
       //myLoadDlg.show(false);
        webView= findViewById(R.id.schooldate_webview);

        //支持javascript
        webView.getSettings().setJavaScriptEnabled(true);
        // 设置可以支持缩放
        webView.getSettings().setSupportZoom(true);
        // 设置出现缩放工具
        webView.getSettings().setBuiltInZoomControls(true);
        //扩大比例的缩放
        webView.getSettings().setUseWideViewPort(true);
        //自适应屏幕
        webView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                // TODO 自动生成的方法存根
                if(newProgress==100){
                    pg1.setVisibility(View.GONE);//加载完网页进度条消失
                   // myLoadDlg.dismiss();
                }
                else{
                    pg1.setVisibility(View.VISIBLE);//开始加载网页时显示进度条
                    pg1.setProgress(newProgress);//设置进度值
                }

            }
        });

        webView.loadUrl(url);

    }

    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.webback:{
                    if (webView.canGoBack()){
                        webView.goBack();
                    }else {
                        Toast.makeText(WebViewActivity.this,"不能再后退了",Toast.LENGTH_SHORT).show();
                    }

                    break;
                }
                case R.id.webgo:{
                    if (webView.canGoForward()){
                        webView.goForward();
                    }else {
                        Toast.makeText(WebViewActivity.this,"不能再前进了",Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case R.id.webclose:{
                    webView.clearCache(true);
                    webView.clearFormData();
                    webView.clearHistory();
                    finish();
                    break;
                }
            }
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (webView.canGoBack()) {
                webView.goBack();
            }else {
                return super.onKeyDown(keyCode, event);
            }
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    @Override
    public void onDestroy() {
        if (webView != null) {
            webView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
            webView.clearHistory();
            webView.clearCache(true);
            webView.clearFormData();
            ((ViewGroup) webView.getParent()).removeView(webView);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }

}
