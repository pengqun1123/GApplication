package com.TG.library.utils;


import android.content.Context;
import android.widget.Toast;

/**
 * @desc 封装Toast
 */
public class ToastUtil {

    private static Toast mToast;

    /**
     * @param context
     * @param msg
     * @desc 显示长时间的Toast
     */
    public static void toast(Context context, String msg) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, Toast.LENGTH_LONG);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }

    /**
     * @param context
     * @param msg
     * @param dur 显示的时常类型
     * @desc 显示Toast，自定义时常
     */
    public static void toast(Context context, String msg, int dur) {
        if (mToast == null) {
            mToast = Toast.makeText(context, msg, dur);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
}
