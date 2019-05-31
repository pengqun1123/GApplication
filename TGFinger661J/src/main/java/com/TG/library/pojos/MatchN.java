package com.TG.library.pojos;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created By pq
 * on 2019/5/21
 * 特征1:N的结果对象
 */
public class MatchN implements Parcelable {

    //结果码
    private int resultCode;
    //模板的指针位置
    private int templIndex;
    //比对分数
    private int templScore;
    //对应主机中模板的名称
    private String fileName;
    //返回的可更新的模板的数据
    private byte[] updateTempl;


    protected MatchN(Parcel in) {
        resultCode = in.readInt();
        templIndex = in.readInt();
        templScore = in.readInt();
        fileName = in.readString();
        updateTempl = in.createByteArray();
    }

    public static final Creator<MatchN> CREATOR = new Creator<MatchN>() {
        @Override
        public MatchN createFromParcel(Parcel in) {
            return new MatchN(in);
        }

        @Override
        public MatchN[] newArray(int size) {
            return new MatchN[size];
        }
    };

    public MatchN(int resultCode, int templIndex, int templScore, String fileName, byte[] updateTempl) {
        this.resultCode = resultCode;
        this.templIndex = templIndex;
        this.templScore = templScore;
        this.fileName = fileName;
        this.updateTempl = updateTempl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(resultCode);
        parcel.writeInt(templIndex);
        parcel.writeInt(templScore);
        parcel.writeString(fileName);
        parcel.writeByteArray(updateTempl);
    }

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public int getTemplIndex() {
        return templIndex;
    }

    public void setTemplIndex(int templIndex) {
        this.templIndex = templIndex;
    }

    public int getTemplScore() {
        return templScore;
    }

    public void setTemplScore(int templScore) {
        this.templScore = templScore;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public byte[] getUpdateTempl() {
        return updateTempl;
    }

    public void setUpdateTempl(byte[] updateTempl) {
        this.updateTempl = updateTempl;
    }

    public static Creator<MatchN> getCREATOR() {
        return CREATOR;
    }
}
