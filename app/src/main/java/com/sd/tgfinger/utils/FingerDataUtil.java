package com.sd.tgfinger.utils;

import android.view.View;

import com.orhanobut.logger.Logger;
import com.sd.tgfinger.GApplication;
import com.sd.tgfinger.dao.db.DBUtil;
import com.sd.tgfinger.dao.db.DbCallBack;
import com.sd.tgfinger.pojo.Finger6;
import com.sd.tgfinger.tgApi.bigFeature.TGB2Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 保存指静脉数据到数据库
 */
public class FingerDataUtil {

    public static FingerDataUtil getInstance() {
        return Holder.INSTANCE;
    }

    private static class Holder {
        private static final FingerDataUtil INSTANCE = new FingerDataUtil();
    }

    /**
     * 目前只考虑注册6特征模式
     *
     * @param fingerData 指静脉数据
     */
    public void insertOrReplaceFinger(byte[] fingerData) {
        Finger6 finger6 = new Finger6();
        finger6.setFinger6Feature(fingerData);
        DBUtil dbUtil = GApplication.getDbUtil();
        dbUtil.setDbCallBack(new DbCallBack<Finger6>() {
            @Override
            public void onSuccess(Finger6 result) {
                Logger.d("Fg6成功插入：" + result);
                //可插入User表的数据
                FingerListManager.getInstance().addFingerData(result);
                FingerServiceUtil.getInstance().addFinger(result.getFinger6Feature());
//                fingerListToFingerByte();
            }

            @Override
            public void onSuccess(List<Finger6> result) {

            }

            @Override
            public void onFailed() {
                Logger.d("Fg6插入失败：");
            }

            @Override
            public void onNotification(boolean result) {

            }
        }).insertAsyncSingle(finger6);
    }


}
