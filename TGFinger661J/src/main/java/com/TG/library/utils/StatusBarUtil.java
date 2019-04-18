package com.TG.library.utils;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

/**
 * Created By pq
 * on 2019/1/30
 * 状态栏适配
 */
public class StatusBarUtil {

    /**
     * 获取状态栏高度
     *
     * @param context
     * @return
     */
    public static int getStatusBarH(Context context) {
        int height = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        if (resourceId > 0) {
            height = context.getResources().getDimensionPixelSize(resourceId);
        }
        return height;
    }


    public static View createStatusBarView(Context context, @ColorInt int color){
        View stausView=new View(context);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.
                LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                getStatusBarH(context));
        stausView.setLayoutParams(layoutParams);
        stausView.setBackgroundColor(color);
        return stausView;
    }

    public static void setStatuBar(Activity activity, @ColorInt int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //5.0+
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            //取消透明状态栏，contentView不再与状态栏重叠
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            activity.getWindow().setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //4.4
            //透明状态栏，contentView与状态栏重叠，需使用fitSystemWindow属性
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
            decorView.addView(createStatusBarView(activity, color));
        }
    }

}
