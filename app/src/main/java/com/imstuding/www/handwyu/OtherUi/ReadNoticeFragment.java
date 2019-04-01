package com.imstuding.www.handwyu.OtherUi;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.imstuding.www.handwyu.R;

/**
 * Created by yangkui on 2018/9/7.
 */

public class ReadNoticeFragment extends Fragment {

    private Context mcontext;
    private View view;
    private TextView title;
    private TextView time;
    private TextView content;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        mcontext=getActivity();
        view=inflater.inflate(R.layout.fragment_readnotice,container,false);
        initFragment(view);
        return view;
    }

    public void initFragment(View view){
        Bundle bundle=null;
        try {
            bundle = getArguments();
        }catch (Exception e){
        }
        title= view.findViewById(R.id.readnotice_title);
        time= view.findViewById(R.id.readnotice_time);
        content= view.findViewById(R.id.readnotice_content);

        title.setText(bundle.getString("title"));
        time.setText(bundle.getString("time"));
        content.setText(bundle.getString("content"));
    }
}