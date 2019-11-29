package com.sd.tgfinger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import com.sd.tgfinger.CallBack.OnStartDevStatusServiceListener;
import com.sd.tgfinger.dao.db.DBUtil;
import com.sd.tgfinger.tgApi.TGBApi;
import com.sd.tgfinger.tgApi.bigFeature.TGB2API;
import com.sd.tgfinger.tgApi.tgb1.TGB1API;
import com.sd.tgfinger.utils.MyActivityManager;


/**
 * Created By pq
 * on 2019/4/9
 */
public class GApplication extends Application {

    private static DBUtil dbUtil;

    public static DBUtil getDbUtil() {
        return dbUtil;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //监听所有activity的生命周期
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);

        //初始化数据库
        if (dbUtil == null)
            dbUtil = DBUtil.getInstance(this);

//        CrashHandler.getInstance().init(this);

        //前比
//        TG661JFrontAPI.getTg661jFrontApi().startDevService(GApplication.this);
//        //后比
//        TGB2API.getTGAPI().startDevService(GApplication.this, new OnStartDevStatusServiceListener() {
//            @Override
//            public void startDevServiceStatus(Boolean aBoolean) {
//
//            }
//        });
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        DBUtil.getInstance(this).closeData();
    }

    private ActivityLifecycleCallbacks activityLifecycleCallbacks = new ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(Activity activity, Bundle bundle) {
            MyActivityManager.getMyActivityManager().pushAct(activity);
        }

        @Override
        public void onActivityStarted(Activity activity) {

        }

        @Override
        public void onActivityResumed(Activity activity) {

        }

        @Override
        public void onActivityPaused(Activity activity) {

        }

        @Override
        public void onActivityStopped(Activity activity) {

        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            //前比
//            TG661JFrontAPI.getTg661jFrontApi().unbindDevService(GApplication.this);

            //后比
//            TGB2API.getTGAPI().unbindDevService(getApplicationContext());
            MyActivityManager.getMyActivityManager().removeAllAct();
        }
    };


}
