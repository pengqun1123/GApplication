package com.example.administrator;

import android.app.Application;
import android.util.Log;

import com.TG.library.api.TG661JBAPI;
import com.TG.library.api.TG661JBehindAPI;

/**
 * Created By pq
 * on 2019/4/9
 */
public class GApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("===LLL", "   应用销毁 ：onCreate");
//        TG661JBehindAPI.getTG661JBehindAPI().startDevService(this);
        TG661JBAPI.getTg661JBAPI().startDevService(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        Log.d("===LLL", "   应用销毁 ：onTerminate");
//        TG661JBehindAPI.getTG661JBehindAPI().unbindDevService(this);
        TG661JBAPI.getTg661JBAPI().unbindDevService(this);
    }


}
