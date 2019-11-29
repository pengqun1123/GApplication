package com.sd.tgfinger.pojos;

/**
 * Created By pq
 * on 2019/6/29
 */
public class TaskBean {

    //work方法所需要的参数

    //所有的模板数据
    private byte[] templData;
    //模板的数量
    private int fingerSize;
    //提取的特征
    private byte[] imgFeature;
    //单元模板组所处的指针
    private int index;
    //单元模板组的数量
    private int cellDataCount;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    //类型
    private int type;

    public TaskBean(byte[] templData, int fingerSize, byte[] imgFeature, int index, int cellDataCount
            , int type) {
        this.templData = templData;
        this.fingerSize = fingerSize;
        this.imgFeature = imgFeature;
        this.index = index;
        this.cellDataCount = cellDataCount;
        this.type = type;
    }

    public byte[] getTemplData() {
        return templData;
    }

    public void setTemplData(byte[] templData) {
        this.templData = templData;
    }

    public int getFingerSize() {
        return fingerSize;
    }

    public void setFingerSize(int fingerSize) {
        this.fingerSize = fingerSize;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getCellDataCount() {
        return cellDataCount;
    }

    public void setCellDataCount(int cellDataCount) {
        this.cellDataCount = cellDataCount;
    }

    public byte[] getImgFeature() {
        return imgFeature;
    }

    public void setImgFeature(byte[] imgFeature) {
        this.imgFeature = imgFeature;
    }
}
