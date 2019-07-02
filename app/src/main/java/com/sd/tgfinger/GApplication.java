package com.sd.tgfinger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import com.sd.tgfinger.api.TGAPI;
import com.sd.tgfinger.utils.MyActivityManager;


/**
 * Created By pq
 * on 2019/4/9
 */
public class GApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        //监听所有activity的生命周期
        registerActivityLifecycleCallbacks(activityLifecycleCallbacks);
        //后比算法初始化
//        TGAPI.getTGAPI().init(this);

//        CrashHandler.getInstance().init(this);
//        TG661JBehindAPI.getTG661JBehindAPI().startDevService(this);
//        TG661JBAPI.getTg661JBAPI().startDevService(this);
        TGAPI.getTGAPI().startDevService(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
//        TG661JBehindAPI.getTG661JBehindAPI().unbindDevService(this);
//        TG661JBAPI.getTg661JBAPI().unbindDevService(this);
        Log.d("===KKK", "   执行GApplication的onTerminate方法  ");
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
            TGAPI.getTGAPI().unbindDevService(getApplicationContext());
            MyActivityManager.getMyActivityManager().removeAllAct();
        }
    };


}
