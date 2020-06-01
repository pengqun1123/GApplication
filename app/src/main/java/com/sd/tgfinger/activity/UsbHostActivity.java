package com.sd.tgfinger.activity;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.sd.tgfinger.gapplication.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class UsbHostActivity extends AppCompatActivity {

    private static final String TAG = "UsbHostActivity";

    private UsbDevice mUsbDevice; // 找到的USB设备

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_behind);

        usb();
    }

    private void usb() {
        UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        if (usbManager == null) {
            return;
        } else {
            Log.i(TAG, "usb设备：" + String.valueOf(usbManager.toString()));
        }
        HashMap<String, UsbDevice> deviceList = usbManager.getDeviceList();
        Log.i(TAG, "usb设备：" + String.valueOf(deviceList.size()));
        Iterator<UsbDevice> deviceIterator = deviceList.values().iterator();
        ArrayList<String> USBDeviceList = new ArrayList<String>(); // 存放USB设备的数量
        while (deviceIterator.hasNext()) {
            UsbDevice device = deviceIterator.next();

            USBDeviceList.add(String.valueOf(device.getVendorId()));
            USBDeviceList.add(String.valueOf(device.getProductId()));

            // 在这里添加处理设备的代码
            if (device.getVendorId() == 6790 && device.getProductId() == 57360) {
                mUsbDevice = device;
                Log.i(TAG, "找到设备");
            }
        }


    }
}
