package com.imstuding.www.handwyu.WidgetBig;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.widget.RemoteViews;

import com.imstuding.www.handwyu.R;
import com.imstuding.www.handwyu.ToolUtil.DatabaseHelper;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.imstuding.www.handwyu.MainUi.TableFragment.getDaySub;
import static com.imstuding.www.handwyu.ToolUtil.DatabaseHelper.db_version;
import static com.imstuding.www.handwyu.WidgetSmall.MyCourseWidgetSmall.WIDGET_UPDATE;

/**
 * Implementation of App Widget functionality.
 */
public class MyCourseWidgetBig extends AppWidgetProvider {

    final int[]  w_z={R.id.w_qi,R.id.w_yi,R.id.w_er,R.id.w_san,R.id.w_si,R.id.w_wu,R.id.w_liu};
    public static final String CHANGE_IMAGE = "com.imstuding.www.handwyu.Widget.action.CHANGE_IMAGE";
    private Intent startUpdateIntent=null;
    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {


        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_course_widget_big);

        //设置adapter
        Intent intent=new Intent(context,MyRemoteViewsServiceBig.class);
        views.setRemoteAdapter(R.id.w_courceDetail_big,intent);

        // 点击列表触发事件
        Intent clickIntent = new Intent(context, MyCourseWidgetBig.class);
        // 设置Action，方便在onReceive中区别点击事件
        clickIntent.setAction(CHANGE_IMAGE);

        clickIntent.setData(Uri.parse(clickIntent.toUri(Intent.URI_INTENT_SCHEME)));
        // Instruct the widget manager to update the widget
        PendingIntent pendingIntentTemplate = PendingIntent.getBroadcast(
                context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        views.setPendingIntentTemplate(R.id.w_courceDetail_big,
                pendingIntentTemplate);

        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        if (startUpdateIntent==null)
            startUpdateIntent = new Intent(context, MyServiceBig.class);
        context.startService(startUpdateIntent);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        //startUpdateIntent = new Intent(context, MyServiceBig.class);
        if (startUpdateIntent==null)
            startUpdateIntent = new Intent(context, MyServiceBig.class);
        context.startService(startUpdateIntent);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        if (startUpdateIntent==null)
            startUpdateIntent = new Intent(context, MyServiceBig.class);
        context.stopService(startUpdateIntent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (startUpdateIntent==null)
            startUpdateIntent = new Intent(context, MyServiceBig.class);
        context.startService(startUpdateIntent);
        String action = intent.getAction();
        if (action.equals(CHANGE_IMAGE)) {
            // 单击Wdiget中ListView的某一项会显示一个Toast提示。
            String content=intent.getStringExtra("content");
            if (!content.isEmpty()){
                Intent startAcIntent = new Intent();
                startAcIntent.setComponent(new ComponentName("com.imstuding.www.handwyu","com.imstuding.www.handwyu.MainUi.MainActivity"));//第一个是包名，第二个是类所在位置的全称
                startAcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(startAcIntent);
            }
        }else if (action.equals(Intent.ACTION_SCREEN_ON)){
            //Toast.makeText(context,"开屏" , Toast.LENGTH_SHORT).show();
        }else if (action.equals(Intent.ACTION_SCREEN_OFF)){
            // Toast.makeText(context,"锁屏" , Toast.LENGTH_SHORT).show();
        }else if (action.equals(Intent.ACTION_USER_PRESENT)){
            // Toast.makeText(context,"解锁" , Toast.LENGTH_SHORT).show();
        }else if (action.equals(Intent.ACTION_BOOT_COMPLETED)){
            //Toast.makeText(context,"开机" , Toast.LENGTH_SHORT).show();
        }else if (action.equals(Intent.ACTION_SHUTDOWN)){
            // Toast.makeText(context,"关机" , Toast.LENGTH_SHORT).show();
        }else if (action.equals(WIDGET_UPDATE)){
            //Toast.makeText(context,"刷新big" , Toast.LENGTH_SHORT).show();
            reFresh(context);
        }
        super.onReceive(context, intent);
    }

    private void reFresh(Context context){
        final AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        final ComponentName cn = new ComponentName(context,MyCourseWidgetBig.class);
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.my_course_widget_big);

        Date d = new Date();
        // 这句话会调用RemoteViewSerivce中RemoteViewsFactory的onDataSetChanged()方法。
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn),
                R.id.w_courceDetail_big);
        views.setTextViewText(R.id.w_text_zc_big,"第"+getWeek(d,context)+"周");
        for (int i=0;i<7;i++){
            int j=getWeekOfDate(d);
            if (i==j)
                views.setTextColor(w_z[i], Color.RED);
            else
                views.setTextColor(w_z[i], Color.WHITE);
        }

        AppWidgetManager awm = AppWidgetManager.getInstance(context);
        awm.updateAppWidget(cn, views);


    }

    public int getWeekOfDate(Date date) {
        int[] weekDays = { 0, 1, 2, 3, 4, 5, 6 };
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0)
            w = 0;
        return weekDays[w];
    }

    public String getWeek(Date d,Context context){
        DatabaseHelper dbhelp=new DatabaseHelper(context,"course.db",null,db_version);
        SQLiteDatabase db=dbhelp.getReadableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String n_rq=sdf.format(d);
        String xq=null;
        String o_rq=null;
        String zc=null;
        try{
            Cursor cursor= db.rawQuery("select * from week",null);
            while (cursor.moveToNext()){
                xq = cursor.getString(0);
                o_rq = cursor.getString(1);
                zc = cursor.getString(2);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        long countDay=getDaySub(o_rq,n_rq);
        int countweek= (int) (countDay/7);
        int extreeday=(int)(countDay%7);
        int i_zc=Integer.parseInt(zc);
        int i_xq=Integer.parseInt(xq)%7;
        if (i_xq+extreeday>6){
            i_zc = i_zc+countweek;
            i_zc++;
        }else {
            i_zc = i_zc+countweek;
        }

        return i_zc+"";
    }
}

