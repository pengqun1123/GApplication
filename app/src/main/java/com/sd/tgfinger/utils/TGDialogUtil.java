package com.sd.tgfinger.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sd.tgfinger.gapplication.R;


/**
 * Created By pq
 * on 2019/6/5
 */
public class TGDialogUtil {
    private static TGDialogUtil instance = null;
    private TextView tipTv;
    private AlertDialog dialog;

    public static TGDialogUtil Instance() {
        if (instance == null) {
            synchronized (TGDialogUtil.class) {
                if (instance == null) {
                    instance = new TGDialogUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 等待得dialog
     *
     * @param context
     * @param resultTip
     * @return
     */
    public AlertDialog showWaitDialog(Context context, String resultTip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        View view = LayoutInflater.from(context)
                .inflate(R.layout.dialog_wait_view, null);
        tipTv = view.findViewById(R.id.tipTv);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams layoutParams = null;
//        if (window != null) {
//            layoutParams = window.getAttributes();
//            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            layoutParams.width = /*WindowManager.LayoutParams.WRAP_CONTENT*/SpDpUtil.dp2px(context, 50);
//        }
        dialog.setView(view);
//        dialog.addContentView(view, layoutParams);
        tipTv.setText(resultTip);
        dialog.show();
        return dialog;
    }

    public AlertDialog setTip(String tip) {
        if (dialog != null && dialog.isShowing() && tipTv != null) {
            tipTv.setText(tip);
        }
        return dialog;
    }

    public AlertDialog disDialog() {
        if (dialog != null /*&& dialog.isShowing()*/)
            dialog.dismiss();
        Log.d("===DDD", "  d:" + dialog);
        return dialog;
    }

}
