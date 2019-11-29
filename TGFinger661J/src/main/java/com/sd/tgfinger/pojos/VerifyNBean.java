package com.sd.tgfinger.pojos;

/**
 * Created By pq
 * on 2019/6/27
 */
public class VerifyNBean {
    //验证的结果码
    private int verifyNResult;
    //验证的分数
    private int verifyScore;
    //验证的模板位置
    private int fingerIndex;
    //验证成功后可更新的模板数据
    private byte[] updateFingerData;

    public VerifyNBean(int verifyNResult, int verifyScore,
                       int fingerIndex, byte[] updateFingerData) {
        this.verifyNResult = verifyNResult;
        this.verifyScore = verifyScore;
        this.fingerIndex = fingerIndex;
        this.updateFingerData = updateFingerData;
    }

    public int getVerifyNResult() {
        return verifyNResult;
    }

    public void setVerifyNResult(int verifyNResult) {
        this.verifyNResult = verifyNResult;
    }

    public int getVerifyScore() {
        return verifyScore;
    }

    public void setVerifyScore(int verifyScore) {
        this.verifyScore = verifyScore;
    }

    public int getFingerIndex() {
        return fingerIndex;
    }

    public void setFingerIndex(int fingerIndex) {
        this.fingerIndex = fingerIndex;
    }

    public byte[] getUpdateFingerData() {
        return updateFingerData;
    }

    public void setUpdateFingerData(byte[] updateFingerData) {
        this.updateFingerData = updateFingerData;
    }
}
