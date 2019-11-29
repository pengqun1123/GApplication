package com.sd.tgfinger.pojos;

/**
 * Created By pq
 * on 2019/6/26
 */
public class FusionFeatureBean {
    //特征融合的结果
    private int fusionResult;
    //特征融合后的模板
    private byte[] fusionTempl;

    public FusionFeatureBean(int fusionResult, byte[] fusionTempl) {
        this.fusionResult = fusionResult;
        this.fusionTempl = fusionTempl;
    }

    public int getFusionResult() {
        return fusionResult;
    }

    public void setFusionResult(int fusionResult) {
        this.fusionResult = fusionResult;
    }

    public byte[] getFusionTempl() {
        return fusionTempl;
    }

    public void setFusionTempl(byte[] fusionTempl) {
        this.fusionTempl = fusionTempl;
    }
}
