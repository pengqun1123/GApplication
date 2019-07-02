package com.sd.tgfinger.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;


/**
 * Created by **
 * on 2018/4/23.
 */

public class MyActivityManager {
    private static Stack<Activity> activityStack;
    private static List<Activity> activities;
    private static MyActivityManager instance;

    private MyActivityManager() {
    }

    public static MyActivityManager getMyActivityManager() {
        if (instance == null) {
            synchronized (MyActivityManager.class) {
                if (instance == null) {
                    instance = new MyActivityManager();
                }
            }
        }
        return instance;
    }

    //存储activity
    public void pushAct(Activity activity) {
        if (activities == null) {
            activities = new ArrayList<>();
        }
        if (activities.size() > 0) {
            for (int i = 0; i < activities.size(); i++) {
                boolean equals = activities.get(i).getClass().equals(activity.getClass());
                if (!equals) {
                    activities.add(activity);
                }
            }
        } else {
            activities.add(activity);
        }
    }

    //删除所有的activity
    public void removeAllAct() {
        if (activities != null && activities.size() > 0) {
            for (int i = 0; i < activities.size(); i++) {
                if (!activities.get(i).isFinishing()) {
                    activities.get(i).finish();
                }
            }
            activities.clear();
        }
    }

    private List<Activity> activities1 = null;
    //关闭摸一个activity
    public void removeAct(Class cla) {
        if (activities1 == null) {
            activities1 = new ArrayList<>();
        }else {
            if (activities1.size()>0){
                activities1.clear();
            }
        }
        activities1.addAll(activities);
        if (cla != null && activities != null && activities.size() > 0) {
            for (int i = 0; i < activities1.size(); i++) {
                boolean equals = activities.get(i).getClass().equals(cla);
                if (equals) {
                    activities.get(i).finish();
                    activities.remove(i);
                    break;
                }
            }
        }
    }

    /**
     * 获取目标activity
     *
     * @param cla
     * @param <T>
     * @return
     */
    public <T extends Activity> T getAct(Class<T> cla) {
        Activity activity = null;
        if (cla != null && activities != null && activities.size() > 0) {
            for (int i = 0; i < activities.size(); i++) {
                boolean equals = activities.get(i).getClass().equals(cla);
                if (equals) {
                    activity = activities.get(i);
                }
            }
        }
        return (T) (activity);
    }

    /**
     * 判断一个Activity 是否存在
     *
     * @param clz
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public <T extends Activity> boolean isActivityExist(Class<T> clz) {
        boolean res;
        Activity activity = null;
        for (int i = 0; i < activities.size(); i++) {
            boolean equals = activities.get(i).getClass().equals(clz);
            if (equals) {
                activity = activities.get(i);
            }
        }
        if (activity == null) {
            res = false;
        } else {
            if (activity.isFinishing() || activity.isDestroyed()) {
                res = false;
            } else {
                res = true;
            }
        }
        return res;
    }

    public Integer getActSize() {
        if (activities != null) {
            return activities.size();
        }
        return 0;
    }

//    //退出栈顶Activity
//    public void popActivity(Activity activity){
//        if(activity!=null){
//            activity.finish();
//            activityStack.remove(activity);
//            activity=null;
//        }
//    }
//
//    //获得当前栈顶Activity
//    public Activity currentActivity(){
//        Activity activity=activityStack.lastElement();
//        return activity;
//    }
//
//    //将当前Activity推入栈中
//    public void pushActivity(Activity activity){
//        if(activityStack==null){
//            activityStack=new Stack<Activity>();
//        }
//        activityStack.add(activity);
//    }
//    //退出栈中所有Activity
//    public void popAllActivityExceptOne(Class cls){
//        while(true){
//            Activity activity=currentActivity();
//            if(activity==null){
//                break;
//            }
//            if(activity.getClass().equals(cls) ){
//                break;
//            }
//            popActivity(activity);
//        }
//    }
}
