package com.TG.library.utils;

import android.content.Context;

/**
 * Created By pq
 * on 2019/1/21
 * 单位转换
 */
public class SpDpUtil {

    public static int px2dp(Context context, float px){
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(px/density+0.5);
    }

    public static int dp2px(Context context, float dp){
        float density = context.getResources().getDisplayMetrics().density;
        return (int)(dp*density+0.5);
    }
}
