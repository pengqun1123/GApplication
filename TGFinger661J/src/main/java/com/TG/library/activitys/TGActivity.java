package com.TG.library.activitys;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.TG.library.api.TG661JAPI;
import com.TG.library.utils.LogUtils;
import com.TG.library.utils.LogcatHelper;
import com.example.mylibrary.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class TGActivity extends AppCompatActivity {

    private final int requestMainPermissCode = 0xFF;
    //用于存储需要申请的权限
    private List<String> needRequestPermission = new ArrayList<>();
    //用于存储用户拒绝掉的必须权限
    private List<String> requestPermission;



    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == FIND_USB) {
                findUSBDev(1317, 42156);
            }
        }
    };
    private int type;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tg);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", -1);
        checkPermissions();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    //检查权限
    private void checkPermissions() {
        /*
         * PackageManager.PERMISSION_GRANTED -- 表示权限已经同意
         * PackageManager.PERMISSION_DENIED -- 表示权限没有同意，需要申请该权限
         */
        String[] perms = TG661JAPI.getTG661JAPI().getPerms();
        //检查权限
        for (int i = 0; i < perms.length; i++) {
            String perm = perms[i];
            int checkResult = ContextCompat.checkSelfPermission(TGActivity.this, perm);
            if (checkResult == PackageManager.PERMISSION_DENIED) {
                //权限没有同意，需要申请该权限
                needRequestPermission.add(perm);
            }
        }
        if (needRequestPermission.size() > 0) {
            //有权限需要申请
            int size = needRequestPermission.size();
            String[] permissions = needRequestPermission.toArray(new String[size]);
            //这里利用隐式启动大方式，启动权限依赖的activity,// com.tg.m661j.vein.api  隐式意图
            //申请权限
            ActivityCompat.requestPermissions(TGActivity.this, permissions, requestMainPermissCode);
        } else {
            if (type == 1) {
                TG661JAPI.getTG661JAPI().saveTemplHost();
                TGActivity.this.finish();
            } else if (type == 2) {
                //Hot-USB连接方式，为设备授权
                cmdUSBThread.start();
            }
        }
    }

    //接收权限申请的结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case requestMainPermissCode:
                if (requestPermission != null && requestPermission.size() > 0) {
                    requestPermission.clear();
                }
                if (requestPermission == null) {
                    requestPermission = new ArrayList<>();
                }
                for (int i = 0; i < grantResults.length; i++) {
                    int grantResult = grantResults[i];
                    if (grantResult == -1) {
                        //添加需要申请的权限
                        requestPermission.add(permissions[i]);
                    }else {
                        requestPermission.remove(permissions[i]);
                    }
                }
                Log.d("===ppp"," 未同意权限的数量："+requestPermission.size());
                if (requestPermission.size() > 0) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    alertDialog = builder.create();
                    builder.setCancelable(false);
                    builder.setMessage("App正常工作需要这些权限，请同意，否则App不能正常工作!");
                    builder.setNegativeButton("同意", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // 申请权限
                            int size = requestPermission.size();
                            String[] strings = requestPermission.toArray(new String[size]);
                            ActivityCompat.requestPermissions(TGActivity.this, strings,
                                    requestMainPermissCode);
                            alertDialog.dismiss();
                        }//强制用户同意文件权限
//                    })
//                            .setPositiveButton("取消", new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
                        //用户拒绝正常的权限，推出应用
//                            ActivitysManagerUtil.instance().finishAllActivity();
//                            alertDialog.dismiss();
//                            TGActivity.this.finish();
//                        }
                    });
                    builder.show();
                } else {
                    if (alertDialog!=null&&alertDialog.isShowing())alertDialog.dismiss();
                    if (type == 1) {
                        TG661JAPI.getTG661JAPI().saveTemplHost();
                    } else if (type == 2) {
                        //Hot-USB连接方式，为设备授权
                        cmdUSBThread.start();
                    }
                    //简单的写，所有的权限被同意后执行记录错误日志的初始化，
                    // 其实在文件的一些完成后就可以执行
                    //执行日志记录的初始化工作
                    recordLog();
                    TGActivity.this.finish();
                }
                break;
        }
    }

    private final int FIND_USB = 0xF2;
    /*--------------------------这里是工作线程创建区-------------------------*/
    private Thread cmdUSBThread = new Thread(new Runnable() {
        @Override
        public void run() {
            TG661JAPI.getTG661JAPI().writeLicennse();
//            requestUsbPermission2();
        }
    });

    /**
     * 找寻目标的USB设备
     *
     * @param VENDORID
     * @param PRODUCTID
     */
    private void findUSBDev(int VENDORID, int PRODUCTID) {
        //检索USB设备
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        //获取到所有的USB设备，过滤出合适的USb设备
        if (usbManager != null) {
            HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
            Log.d("===TAG==", "    deviceList  size  " + deviceList.size());
            Iterator<UsbDevice> iterator = deviceList.values().iterator();
            while (iterator.hasNext()) {
                UsbDevice usbDevice = iterator.next();
                int deviceId = usbDevice.getDeviceId();
                int vendorId = usbDevice.getVendorId();
                String deviceName = usbDevice.getDeviceName();
                int productId = usbDevice.getProductId();
                LogUtils.d("deviceName:" + deviceName + "  vendorId :" + vendorId +
                        "deviceId:" + deviceId + "productId:" + productId);
                if (vendorId == VENDORID && productId == PRODUCTID) {
                    UsbDevice mUsbDevice = usbDevice;
                    //获取目标USB设备成功

                }
            }
            TGActivity.this.finish();
        }
    }

    /**
     * 为661j和650设备授权，如果设备连接方式为usb-host模式
     */
    private void requestUsbPermission2() {
        String command = "chmod -R 777 /dev/bus/usb";
        try {
            Process process = Runtime.getRuntime().exec(new String[]{"su", "-c", command});
            int i = process.waitFor();
            if (i == 0) {
                handler.sendEmptyMessage(FIND_USB);
            }
            LogUtils.d(" VM665J  执行授权命令 " + i);
//            ToastUtil.toast(this," 执行授权命令 ");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //记录日志
    private void recordLog() {
        //使用完之后，记得stop
        String logDir = TG661JAPI.getTG661JAPI().getLogDir();
        //捕捉错误日志记录，存储在手机外部SD卡
        LogcatHelper.getInstance().init(logDir).start();
    }


}
