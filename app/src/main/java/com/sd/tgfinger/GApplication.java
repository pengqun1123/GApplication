package com.sd.tgfinger;

import android.app.Application;
import android.util.Log;

import com.sd.tgfinger.api.TGAPI;


/**
 * Created By pq
 * on 2019/4/9
 */
public class GApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //后比算法初始化
        TGAPI.getTGAPI().init(this);

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
        TGAPI.getTGAPI().unbindDevService(this);
    }


}
