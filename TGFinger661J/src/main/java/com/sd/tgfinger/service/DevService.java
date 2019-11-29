package com.sd.tgfinger.service;

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

import com.sd.tgfinger.pojos.Msg;
import com.sd.tgfinger.tgApi.Constant;
import com.sd.tgfinger.tgApi.TGBApi;
import com.sd.tgfinger.tgApi.bigFeature.TGB2API;
import com.sd.tgfinger.utils.LogUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class DevService extends Service {

    private Timer timer;
    private MyTask myTask;
    private Boolean isLoop = false;
    private TGB2API tgapi;

    public DevService() {
    }

    private Messenger tg661JMessennger;

    private Messenger messenger = new Messenger(new DevServiceHandler());

    @SuppressLint("HandlerLeak")
    private class DevServiceHandler extends Handler {


        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constant.SEND_MESSAGE_CODE) {
                tg661JMessennger = msg.replyTo;
                tgapi = TGB2API.getTGAPI();
                if (tgapi != null && !isLoop) {
                    LogUtils.d("======启用 DevService");
                    timer.schedule(myTask, 1000, 1200);
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
        stopDevService();
    }

    private void releaseTimerTask() {
        DevService.this.isLoop = false;
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
            Msg msg = tgapi.tgDevStatus();
            Integer result = msg.getResult();
            DevService.this.isLoop = true;
//            Log.d("===TAG", "  DevService 获取设备状态  :" + result);
            Message devServiceMessage = new Message();
            devServiceMessage.what = Constant.RECEIVE_MESSAGE_CODE;
            Bundle bundle = new Bundle();
            if (result == 1) {
                //设备已经连接
                bundle.putInt(Constant.STATUS, 0);
                bundle.putInt(Constant.EXE_CMD, 1);
            } else {
                int execResult = writeCMD();
                //设备未连接
                bundle.putInt(Constant.EXE_CMD, execResult);
                bundle.putInt(Constant.STATUS, -2);
            }
            if (tg661JMessennger != null) {
                try {
                    devServiceMessage.setData(bundle);
                    tg661JMessennger.send(devServiceMessage);
                } catch (RemoteException e) {
                    e.printStackTrace();
                    Log.d("===TAG===", "  DevService 向客户端发送信息失败！ E:" + e.toString());
                }
            }
        }
    }

    private int writeCMD() {
        String command1 = "chmod -R 777 /dev/bus/usb/*";
        String command4 = "adb root";
        String command3 = "chmod -R 777 /dev/hidraw*";
        String command2 = "chmod -R 777 /dev/hidraw0 \nchmod -R 777 /dev/hidraw1 " +
                "\nchmod -R 777 /dev/hidraw2 \nchmod -R 777 /dev/hidraw3 \nchmod -R 777 /dev/hidraw4";
        try {
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(new String[]{"su", "-c", command3});
            int i = process.waitFor();
//            process = runtime.exec(new String[]{"su", "-c", command2});
//            int i1 = process.waitFor();
            LogUtils.i("DevService    CDM写入su1111命令:" + i);
            return i;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void stopDevService() {
        stopSelf();
    }

}
