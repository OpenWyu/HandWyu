package com.imstuding.www.handwyu.ToolAdapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.imstuding.www.handwyu.R;
import com.imstuding.www.handwyu.ToolUtil.SubJect;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yangkui on 2018/1/26.
 */

public class ScoreAdapter extends BaseAdapter {
    private final List<SubJect> object;//成绩对象
    private final Context mContext;
    private final int resourceId;//布局文件
    private final List<SubJect> selectsubject;
    private final List<CheckBox> checkBoxList;
    public ScoreAdapter(Context mContext, int resourceId, List<SubJect> object) {
        super();
        this.object = object;
        this.resourceId=resourceId;
        this.mContext = mContext;
        selectsubject= new LinkedList<>();
        CopySubject(object,selectsubject);
        checkBoxList= new LinkedList<>();
    }

    @Override
    public int getCount() {
       return object.size();
    }

    @Override
    public Object getItem(int position) {
        return object.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        SubJect subJect=object.get(position);
        View view= LayoutInflater.from(mContext).inflate(resourceId,null);
        CheckBox item_check= view.findViewById(R.id.item_select);
        checkBoxList.add(item_check);
        item_check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SubJect subJect=object.get(position);
                CheckBox checkBox= v.findViewById(R.id.item_select);
                if (checkBox.isChecked()){
                    subJect.setCheck(true);
                    selectsubject.add(subJect);
                }else {
                    subJect.setCheck(false);
                    selectsubject.remove(subJect);
                }
            }
        });
        TextView item_xf= view.findViewById(R.id.item_xf);
        TextView item_zcj= view.findViewById(R.id.item_zcj);
        TextView item_xdfsmc= view.findViewById(R.id.item_xdfsmc);
        TextView item_kcmc= view.findViewById(R.id.item_kcmc);
        item_check.setChecked(subJect.isCheck());
        item_xf.setText(subJect.getXf());
        item_zcj.setText(subJect.getZcj());
        item_xdfsmc.setText(subJect.getXdfsmc());
        item_kcmc.setText(subJect.getKcmc());
        return view;
    }

    public List<SubJect> getSelectSubject(){
        return selectsubject;
    }

    public void CopySubject(List<SubJect> src,List<SubJect> dst){
        if (src==null){
            return;
        }
        dst.addAll(src);
    }

    public void selectAll(){
        try{
            for (int i=0;i<checkBoxList.size();i++){
                CheckBox item_check=checkBoxList.get(i);
                item_check.setChecked(true);
                item_check.callOnClick();
            }
            selectsubject.clear();
            CopySubject(object,selectsubject);
        }catch (Exception e){

        }

    }

    public void notSelectAll(){
        try{
            for (int i=0;i<checkBoxList.size();i++){
                CheckBox item_check=checkBoxList.get(i);
                item_check.setChecked(false);
                item_check.callOnClick();
            }
            selectsubject.clear();
        }catch (Exception e){

        }

    }
}
