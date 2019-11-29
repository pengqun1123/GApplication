package com.sd.tgfinger.pojos;

/**
 * Created By pq
 * on 2019/6/26
 */
public class FingerImgBean {
    //抓取的图片数据
    private byte[] imgData;
    //图片数据的长度
    private int imgDataLength;
    //抓取图片的结果
    private int imgResultCode;

    public FingerImgBean(){}

    public FingerImgBean(byte[] imgData, int imgDataLength, int imgResultCode) {
        this.imgData = imgData;
        this.imgDataLength = imgDataLength;
        this.imgResultCode = imgResultCode;
    }

    public byte[] getImgData() {
        return imgData;
    }

    public void setImgData(byte[] imgData) {
        this.imgData = imgData;
    }

    public int getImgDataLength() {
        return imgDataLength;
    }

    public void setImgDataLength(int imgDataLength) {
        this.imgDataLength = imgDataLength;
    }

    public int getImgResultCode() {
        return imgResultCode;
    }

    public void setImgResultCode(int imgResultCode) {
        this.imgResultCode = imgResultCode;
    }
}
