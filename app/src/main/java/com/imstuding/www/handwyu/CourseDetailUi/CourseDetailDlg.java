package com.imstuding.www.handwyu.CourseDetailUi;

/**
 * Created by yangkui on 2018/3/25.
 */
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.imstuding.www.handwyu.R;
import com.imstuding.www.handwyu.ToolUtil.Course;

public class CourseDetailDlg {
    private final Context mcontext;
    private AlertDialog alertDialog;
    private AlertDialog.Builder builder;
    private final Course course;
    private TextView T_kcmc;
    private TextView T_jxcdmc;
    private TextView T_jcdm;
    private TextView T_zc;
    private TextView T_teaxms;
    private TextView T_jxbmc;
    private TextView T_sknrjj;
    public CourseDetailDlg(Context mcontext, Course course){
        this.mcontext=mcontext;
        this.course=course;
    }

    public void show(){
        builder=new AlertDialog.Builder(mcontext);
        Activity activity=(Activity)mcontext;
        LayoutInflater inflater =activity.getLayoutInflater();
        View view = inflater.inflate(R.layout.course_detail, null);
        initDlg(view);//初始化数据
        builder.setView(view);
        builder.setCancelable(true);
        alertDialog=builder.create();
        alertDialog.show();
    }
    public void initDlg(View view){
        try{
            T_kcmc= view.findViewById(R.id.course_kcmc);
            T_jxcdmc= view.findViewById(R.id.course_jxcdmc);
            T_jcdm= view.findViewById(R.id.course_jcdm);
            T_zc= view.findViewById(R.id.course_zc);
            T_teaxms= view.findViewById(R.id.course_teaxms);
            T_jxbmc= view.findViewById(R.id.course_jxbmc);
            T_sknrjj= view.findViewById(R.id.course_sknrjj);

            T_kcmc.setText(course.getKcmc());
            T_jxcdmc.setText(course.getJxcdmc());
            T_jcdm.setText(course.getJs());
            T_zc.setText(course.getZc());
            T_teaxms.setText(course.getTeaxms());
            T_jxbmc.setText(course.getJxbmc());
            T_sknrjj.setText(course.getSknrjj());
        }catch (Exception e){
            T_kcmc.setText("NULL");
            T_jxcdmc.setText("NULL");
            T_jcdm.setText("NULL");
            T_zc.setText("NULL");
            T_teaxms.setText("NULL");
            T_jxbmc.setText("NULL");
            T_sknrjj.setText("NULL");
        }

    }
}

