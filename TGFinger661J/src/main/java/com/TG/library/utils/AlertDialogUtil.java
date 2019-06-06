package com.TG.library.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.TG.library.CallBack.ActionClickListener;
import com.TG.library.CallBack.CommitCallBack;
import com.example.mylibrary.R;

/**
 * Created By pq
 * on 2019/1/30
 * AlertDialog工具类
 */
public class AlertDialogUtil {

    private static AlertDialogUtil instance = null;
    private TextView tipTv;
    private AlertDialog dialog;

    public static AlertDialogUtil Instance() {
        if (instance == null) {
            synchronized (AlertDialogUtil.class) {
                if (instance == null) {
                    instance = new AlertDialogUtil();
                }
            }
        }
        return instance;
    }

    /**
     * 呈现结果的Dialog
     *
     * @param context   上下文对象
     * @param resultTip 结果的提示文字
     */
    public AlertDialog showResultDialog(Context context, String resultTip, boolean cancelShow) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertDialog dialog = builder.create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_result_view, null);
        TextView resultTv = view.findViewById(R.id.resultTv);
        dialog.setCanceledOnTouchOutside(cancelShow);
        dialog.setCancelable(cancelShow);
//        Window window = dialog.getWindow();
//        WindowManager.LayoutParams layoutParams = null;
//        if (window != null) {
//            layoutParams = window.getAttributes();
//            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
//            layoutParams.width = /*WindowManager.LayoutParams.WRAP_CONTENT*/SpDpUtil.dp2px(context, 50);
//        }
        dialog.setView(view);
//        dialog.addContentView(view, layoutParams);
        resultTv.setText(resultTip);
        dialog.show();
        return dialog;
    }

    /**
     * 操作的dialog
     *
     * @param context
     * @param tip
     * @param actionClickListener
     * @param flag
     * @return
     */
    public AlertDialog showActDialog(Context context, String tip,
                                     final ActionClickListener actionClickListener
            , final int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_act_view, null);
        TextView actTv = view.findViewById(R.id.actTv);
        final Button actBtn = view.findViewById(R.id.actBtn);
        if (!TextUtils.isEmpty(tip)) {
            actTv.setText(tip);
        }
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);
        actBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (actionClickListener != null) {
                    actionClickListener.actListener(flag);
                }
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
        return dialog;
    }

    /**
     * 获取信息的dialog
     */
    public AlertDialog showGetTipDialog(Context context, final CommitCallBack commitCallBack
            , final int flag) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        final AlertDialog dialog = builder.create();
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_str_view, null);
        final EditText strEt = view.findViewById(R.id.strEt);
        final Button positionBtn = view.findViewById(R.id.positionBtn);
        dialog.setCanceledOnTouchOutside(true);
        dialog.setCancelable(true);

        positionBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String info = strEt.getText().toString().trim();
                if (commitCallBack != null) {
                    if (TextUtils.isEmpty(info)) {
                        commitCallBack.showTip("提交的信息不能为空");
                    } else {
                        commitCallBack.commiteInfo(info, flag);
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.setView(view);
        dialog.show();
        return dialog;
    }

    /**
     * 等待得dialog
     *
     * @param context
     * @param resultTip
     * @return
     */
    public  AlertDialog showWaitDialog(Context context, String resultTip) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        View view = LayoutInflater.from(context)
                .inflate(com.example.mylibrary.R.layout.dialog_wait_view, null);
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
        Log.d("===DDD","  d:"+dialog);
        return dialog;
    }


}
