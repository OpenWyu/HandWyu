package com.imstuding.www.handwyu.WidgetNotice;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import android.widget.RemoteViews;

import com.imstuding.www.handwyu.R;
import com.imstuding.www.handwyu.BuildConfig;
import com.imstuding.www.handwyu.ToolUtil.DatabaseHelper;
import com.imstuding.www.handwyu.ToolUtil.NoticeUtil;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.imstuding.www.handwyu.MainUi.TableFragment.getDaySub;
import static com.imstuding.www.handwyu.ToolUtil.DatabaseHelper.db_version;

/**
 * Implementation of App Widget functionality.
 */
public class MyCourseWidgetNotice extends AppWidgetProvider {
    public static final String MY_PACKAGE_NAME = "com.imstuding.simple.handwyu";
    public static final String WIDGET_UPDATE = "com.imstuding.www.handwyu.Widget.action.WIDGET_UPDATE";
    public static final String LIST_ACTION = "Widget.Button.list.Click";
    public static final String WIDGET_PREV = "Widget.Button.prev.Click";
    public static final String WIDGET_NEXT = "Widget.Button.next.Click";
    public static Date nowDate = new Date();
    private static int count = 0;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_course_notice);

        views.setTextViewText(R.id.widget_notice_date, new SimpleDateFormat("MM月dd日").format(nowDate));
        views.setTextViewText(R.id.widget_notice_xq, widgetGetWeekOfDate(nowDate));
        if (count != 0) {
            views.setTextViewText(R.id.widget_notice_kcsl, "今天有" + count + "节课");
        } else {
            views.setTextViewText(R.id.widget_notice_kcsl, "今天没有课");
        }

        Intent prevIntent = new Intent(context, MyCourseWidgetNotice.class);

        prevIntent.setAction(WIDGET_PREV);
        views.setOnClickPendingIntent(R.id.widget_notice_prev, PendingIntent.getBroadcast(context, 0, prevIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent nextIntent = new Intent(context, MyCourseWidgetNotice.class);

        nextIntent.setAction(WIDGET_NEXT);
        views.setOnClickPendingIntent(R.id.widget_notice_next, PendingIntent.getBroadcast(context, 0, nextIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        Intent refreshIntent = new Intent(context, MyCourseWidgetNotice.class);

        refreshIntent.setAction(WIDGET_UPDATE);
        views.setOnClickPendingIntent(R.id.widget_notice_refresh, PendingIntent.getBroadcast(context, 0, refreshIntent, PendingIntent.FLAG_UPDATE_CURRENT));

        views.setRemoteAdapter(R.id.widget_notice_list, new Intent(context, MyRemoteViewsServiceNotice.class));

        Intent clickIntent = new Intent(context, MyCourseWidgetNotice.class);

        clickIntent.setAction(LIST_ACTION);
        views.setPendingIntentTemplate(R.id.widget_notice_list, PendingIntent.getBroadcast(context, 0, clickIntent, PendingIntent.FLAG_UPDATE_CURRENT));
        appWidgetManager.updateAppWidget(appWidgetId, views);

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        new NoticeUtil(context).Notice(getListNotice(context, nowDate));
        count = getListNotice(context, nowDate);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }

    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(nowDate);

            switch (action) {
                case LIST_ACTION:
                    Intent startAcIntent = new Intent();
                    startAcIntent.setComponent(new ComponentName(MY_PACKAGE_NAME, "com.imstuding.www.handwyu.MainUi.MainActivity"));
                    startAcIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(startAcIntent);
                    break;
                case WIDGET_PREV:
                    cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) - 1);
                    nowDate = cal.getTime();
                    reFresh(context, nowDate);
                    break;
                case WIDGET_NEXT:
                    cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
                    nowDate = cal.getTime();
                    reFresh(context, nowDate);
                    break;
                case WIDGET_UPDATE:
                    cal.setTime(new Date());
                    nowDate = cal.getTime();
                    reFresh(context, nowDate);
                    break;
            }
        }
        super.onReceive(context, intent);

    }

    private static String widgetGetWeekOfDate(Date date) {
        String[] weekDays = new String[]{"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w];
    }


    private void reFresh(Context context, Date date) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        ComponentName cn = new ComponentName(context, MyCourseWidgetNotice.class);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_course_notice);
        int count = getListNotice(context, date);
        if (count != 0) {
            views.setTextViewText(R.id.widget_notice_kcsl, "今天有" + count + "节课");
        } else {
            views.setTextViewText(R.id.widget_notice_kcsl, "今天没有课");
        }

        views.setTextViewText(R.id.widget_notice_date, new SimpleDateFormat("MM月dd日").format(date));
        views.setTextViewText(R.id.widget_notice_xq, widgetGetWeekOfDate(date));
        mgr.notifyAppWidgetViewDataChanged(mgr.getAppWidgetIds(cn), R.id.widget_notice_list);
        AppWidgetManager.getInstance(context).updateAppWidget(cn, views);
    }

    public int getListNotice(Context context, Date date) {
        SQLiteDatabase db = new DatabaseHelper(context, "course.db", null, db_version).getReadableDatabase();
        int count = 0;
        String zc = getWeek(context, date);
        if (zc == null) {
            return 0;
        }
        String xq = getWeekOfDate(date);
        try {
            Cursor cursor = db.rawQuery("select * from course where zc=? and xq=? and year=?", new String[]{zc, xq, getTerm(date)});
            while (cursor.moveToNext()) {
                count++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return count;
    }

    public String getTerm(Date date) {
        int month = date.getMonth() + 1;
        String year = new SimpleDateFormat("yyyy").format(date);
        if (month >= 2 && month <= 7) {
            return (Integer.parseInt(year) - 1) + "02";
        } else if (month >= 8) {
            return Integer.parseInt(year) + "01";
        } else {
            return (Integer.parseInt(year) - 1) + "01";
        }
    }


    public String getWeek(Context context, Date d) {
        DatabaseHelper dbhelp = new DatabaseHelper(context, "course.db", null, db_version);
        SQLiteDatabase db = dbhelp.getReadableDatabase();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String n_rq = sdf.format(d);
        String xq = null;
        String o_rq = null;
        String zc = null;
        try {
            Cursor cursor = db.rawQuery("select * from week", null);
            while (cursor.moveToNext()) {
                xq = cursor.getString(0);
                o_rq = cursor.getString(1);
                zc = cursor.getString(2);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        long countDay = getDaySub(o_rq, n_rq);
        int countweek = (int) (countDay / 7);
        int extreeday = (int) (countDay % 7);
        int i_zc = Integer.parseInt(zc);
        int i_xq = Integer.parseInt(xq) % 7;
        if (i_xq + extreeday > 6) {
            i_zc = i_zc + countweek;
            i_zc++;
        } else {
            i_zc = i_zc + countweek;
        }

        return i_zc + "";
    }

    public String getWeekOfDate(Date date) {
        int[] weekDays = new int[]{7, 1, 2, 3, 4, 5, 6};
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        if (w < 0) {
            w = 0;
        }
        return weekDays[w] + BuildConfig.FLAVOR;
    }

}

