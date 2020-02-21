package com.sd.tgfinger.CallBack;

/**
 * 返回验证的特征
 */
public interface VerifyFeatureCallBack {
    void verifyFeatureCallBack(int code, String tip, byte[] feature);
}
