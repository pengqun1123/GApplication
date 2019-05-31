package com.TG.library.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

import com.TG.library.api.TGXG661API;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class DevService extends Service {

    private static final int RECEIVE_MESSAGE_CODE = 0x0001;
    private static final int SEND_MESSAGE_CODE = 0x0002;

    private Timer timer;
    private MyTask myTask;

    public DevService() {
    }

    private Messenger tg661JMessennger;
    private TGXG661API tgxg661API;
    private Messenger messenger = new Messenger(new DevServiceHandler());

    @SuppressLint("HandlerLeak")
    private class DevServiceHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == RECEIVE_MESSAGE_CODE) {
                tg661JMessennger = msg.replyTo;
                tgxg661API = (TGXG661API) msg.obj;
                if (tgxg661API != null) {
                    timer.schedule(myTask, 1000, 1000);
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("===TAG===", " DevService 的 onCreate  ");

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("===TAG===", " DevService 的 onBind  ");
        timer = new Timer();
        myTask = new MyTask();
        //客户端可以通过调用这个方法获取到DevService的Binder
        return messenger.getBinder();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("===TAG===", " DevService 的 onStartCommand  ");


        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseTimerTask();
    }

    private void releaseTimerTask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        if (myTask != null) {
            myTask.cancel();
            myTask = null;
        }
    }

    private class MyTask extends TimerTask {

        @Override
        public void run() {
            //如果设备开启，获取设备当前的状态
            int devStatus = tgxg661API.TGGetDevStatus();
            Log.d("===TAG===", "  DevService 获取设备状态  :"+devStatus);
            Message devServiceMessage = Message.obtain();
            devServiceMessage.what = SEND_MESSAGE_CODE;
            if (devStatus >= 0) {
                //设备已经连接
                devServiceMessage.arg1 = 0;
            } else {
                writeCMD();
                //设备未连接
                devServiceMessage.arg1 = -2;
            }
            if (tg661JMessennger != null) {
                try {
                    tg661JMessennger.send(devServiceMessage);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.d("===TAG===", "  DevService 向客户端发送信息失败！");
                }
            }
        }
    }

    private void writeCMD(){
        String command = "chmod -R 777 /dev/bus/usb";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            int i = process.waitFor();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
