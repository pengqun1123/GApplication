package com.sd.tgfinger.utils;

import android.app.Activity;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created By pq
 * on 2019/1/29
 * Activity的管理工具类
 */
public class ActivitysManagerUtil {

    private static ActivitysManagerUtil activitysManagerUtil = null;

    public static ActivitysManagerUtil instance() {
        if (activitysManagerUtil == null) {
            synchronized (ActivitysManagerUtil.class) {
                if (activitysManagerUtil == null) {
                    activitysManagerUtil = new ActivitysManagerUtil();
                }
            }
        }
        return activitysManagerUtil;
    }

    //每个实例对象所属的内存地址都是不一样的
    private List<Activity> activityList = Collections.synchronizedList(new LinkedList<Activity>());

    /**
     * 添加activity
     *
     * @param activity 要添加的activity的对象实例
     */
    public void putAct(Activity activity) {
        if (activityList != null) {
            activityList.add(activity);
        }
    }

    /**
     * 删除一个activity
     *
     * @param activity 要删除的activity对象实例
     */
    public void delAct(Activity activity) {
        if (activityList != null) {
            if (activityList.size() > 0 && activity != null) {
                activityList.remove(activity);
            }
        }
    }

    /**
     * 获取当前的activity
     *
     * @return 返回最后压入栈中的那个activity
     */
    public Activity getCurrentAct() {
        if (activityList == null || activityList.isEmpty()) {
            return null;
        }
        return activityList.get(activityList.size() - 1);
    }

    /**
     * 移除当前的activity
     */
    public void delCurrentAct() {
        if (activityList == null || activityList.isEmpty())
            return;
        activityList.remove(activityList.size() - 1);
    }

    /**
     * 结束指定的activity
     *
     * @param activity 要结束的那个activity
     */
    public void finishAct(Activity activity) {
        if (activityList == null || activityList.isEmpty())
            return;
        activityList.remove(activity);
        activity.finish();
        activity = null;
    }

    /**
     * 结束指定类名的activity
     *
     * @param clazz 指定的类名
     */
    public void finishActivity(Class<?> clazz) {
        if (activityList == null || activityList.isEmpty())
            return;
        for (int i = 0; i < activityList.size(); i++) {
            Class<? extends Activity> aClass = activityList.get(i).getClass();
            if (clazz.equals(aClass)) {
                if (activityList.contains(activityList.get(i)))
                    finishAct(activityList.get(i));
            }
        }
    }

    /**
     * 根据类名获activity实例
     *
     * @param clazz 需要获取的类名
     * @return
     */
    public Activity findActivity(Class<?> clazz) {
        Activity activity = null;
        if (activityList == null || activityList.isEmpty()) {
            return null;
        } else {
            for (int i = 0; i < activityList.size(); i++) {
                Class<? extends Activity> aClass = activityList.get(i).getClass();
                if (aClass.equals(clazz)) {
                    activity = activityList.get(i);
                } else {
                    activity = null;
                }
            }
        }
        return activity;
    }

    /**
     * @return 作用说明 ：获取当前最顶部activity的实例
     */
    public Activity getTopActivity() {
        Activity mBaseActivity = null;
        synchronized (activityList) {
            final int size = activityList.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = activityList.get(size);
        }
        return mBaseActivity;
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        if (activityList == null) {
            return;
        }
        for (Activity activity : activityList) {
            if (activityList.contains(activity))
                activity.finish();
        }
        activityList.clear();
    }

    /**
     * @return 作用说明 ：获取当前最顶部的activity名字
     */
    public String getTopActivityName() {
        Activity mBaseActivity = null;
        synchronized (activityList) {
            final int size = activityList.size() - 1;
            if (size < 0) {
                return null;
            }
            mBaseActivity = activityList.get(size);
        }
        return mBaseActivity.getClass().getName();
    }

    /**
     * 退出应用程序
     */
    public void appExit() {
        try {
            LogUtils.d("app exit");
            finishAllActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
