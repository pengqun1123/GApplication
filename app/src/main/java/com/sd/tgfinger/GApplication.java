package com.sd.tgfinger;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

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

//        CrashHandler.getInstance().init(this);

        //前比
//        TG661JFrontAPI.getTg661jFrontApi().startDevService(GApplication.this);
        //后比
        TGAPI.getTGAPI().startDevService(GApplication.this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

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
            TGAPI.getTGAPI().unbindDevService(getApplicationContext());
            MyActivityManager.getMyActivityManager().removeAllAct();
        }
    };


}
