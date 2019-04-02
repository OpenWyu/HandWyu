package com.imstuding.www.handwyu.MainUi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.imstuding.www.handwyu.ToolUtil.MainFragmentTitle;
import com.imstuding.www.handwyu.WebViewDlg.LoginActivity;
import com.imstuding.www.handwyu.ToolUtil.MyHttpHelp;
import com.imstuding.www.handwyu.OtherUi.OtherActivity;
import com.imstuding.www.handwyu.R;
import com.imstuding.www.handwyu.ToolUtil.UrlUtil;
import com.imstuding.www.handwyu.testUpdate;
import com.jauker.widget.BadgeView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.protocol.HTTP;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

import static com.imstuding.www.handwyu.OtherUi.SplashActivity.isShowUpdate;

/**
 * Created by yangkui on 2018/3/22.
 */

public class HomeFragment extends Fragment {

    private Context mcontext = null;
    private View view = null;
    private LinearLayout layout_update = null;
    private LinearLayout layout_intranet = null;
    private LinearLayout layout_logout = null;
    private LinearLayout layout_notice_widget = null;
    private LinearLayout layout_more = null;
    private LinearLayout layout_shool_date = null;
    private ImageView home_imageview_logout = null;
    private TextView home_textview_logout = null;
    private MyClickListener myClickListener;
    private TextView home_textview_update = null;
    private MainFragmentTitle titleView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);
        initFragment(view);
        return view;
    }

    private void reFresh() {
        home_imageview_logout = view.findViewById(R.id.home_imageview_logout);
        home_textview_logout = view.findViewById(R.id.home_textview_logout);
        if (isShowUpdate) {
            //设置提醒更新
            home_textview_update = view.findViewById(R.id.home_textview_update);
            BadgeView badge = new BadgeView(getActivity());
            badge.setTargetView(home_textview_update);
            badge.setBadgeCount(1);
        }

        if (MainActivity.isLogin()) {
            home_imageview_logout.setBackgroundResource(R.drawable.exit);
            home_textview_logout.setText("退出当前账号");
        } else {
            home_imageview_logout.setBackgroundResource(R.drawable.login);
            home_textview_logout.setText("登录");
        }
    }

    private void initFragment(View view) {
        myClickListener = new MyClickListener();

        titleView = view.findViewById(R.id.title_home);
        titleView.setTitleText("发现");

        layout_shool_date = view.findViewById(R.id.layout_shool_date);
        layout_shool_date.setOnClickListener(myClickListener);

        layout_more = view.findViewById(R.id.layout_more);
        layout_more.setOnClickListener(myClickListener);

        layout_update = view.findViewById(R.id.layout_update);
        layout_update.setOnClickListener(myClickListener);


        layout_logout = view.findViewById(R.id.layout_logout);
        layout_logout.setOnClickListener(myClickListener);

        layout_intranet = view.findViewById(R.id.layout_intranet);
        layout_intranet.setOnClickListener(myClickListener);

        layout_notice_widget = view.findViewById(R.id.layout_notice_widget);
        layout_notice_widget.setOnClickListener(myClickListener);

        reFresh();
    }

    @Override
    public void onResume() {
        reFresh();
        super.onResume();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden)
            reFresh();
    }


    class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.layout_update: {
                    testUpdate update = new testUpdate(mcontext, true, true);
                    update.update();
                    break;
                }
                case R.id.layout_intranet: {
                    Intent intent = new Intent();
                    intent.setClass(mcontext, OtherActivity.class);
                    intent.putExtra("msg", "intranet");
                    startActivity(intent);
                    break;
                }
                case R.id.layout_more: {
                    Intent intent = new Intent();
                    intent.setClass(mcontext, OtherActivity.class);
                    intent.putExtra("msg", "more");
                    startActivity(intent);
                    break;
                }
                case R.id.layout_notice_widget: {
                    Intent intent = new Intent();
                    intent.setClass(mcontext, OtherActivity.class);
                    intent.putExtra("msg", "AutoNotice");
                    startActivity(intent);
                    break;
                }
                case R.id.layout_logout: {
                    if (MainActivity.isLogin()) {
                        MainActivity.setJessionId("123456789");
                        MainActivity.setLogin(false);

                        home_imageview_logout.setBackgroundResource(R.drawable.login);
                        home_textview_logout.setText("登录");

                        Toast.makeText(mcontext, "已退出", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent intent = new Intent();
                        intent.setClass(mcontext, LoginActivity.class);
                        startActivity(intent);
                    }
                    break;
                }
                case R.id.layout_shool_date: {
                    Intent intent = new Intent();
                    intent.setClass(mcontext, OtherActivity.class);
                    intent.putExtra("msg", "webView");
                    intent.putExtra("url", UrlUtil.schoolDateUrl);
                    startActivity(intent);
                    break;
                }
            }
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mcontext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mcontext = null;
    }
}
