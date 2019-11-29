package com.sd.tgfinger.pojos;

/**
 * Created By pq
 * on 2019/6/26
 */
public class FingerFeatureBean {

   //特征的提取结果
    private int featureResult;
    //手指特征数据
    private byte[] fingerFeatureData;

    public FingerFeatureBean(int featureResult, byte[] fingerFeatureData) {
        this.featureResult = featureResult;
        this.fingerFeatureData = fingerFeatureData;
    }

    public int getFeatureResult() {
        return featureResult;
    }

    public void setFeatureResult(int featureResult) {
        this.featureResult = featureResult;
    }

    public byte[] getFingerFeatureData() {
        return fingerFeatureData;
    }

    public void setFingerFeatureData(byte[] fingerFeatureData) {
        this.fingerFeatureData = fingerFeatureData;
    }
}
