package com.sd.tgfinger.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created By pq
 * on 2019/9/17
 * 调用接口后的结果
 */
public class Msg implements Parcelable {

    private Integer result;
    private String tip;
    private byte[] fingerData;
    private Integer index;
    private Integer score;
    private Integer fingerSize;

    public Msg() {

    }

    public Msg(Integer result, String tip) {
        this.result = result;
        this.tip = tip;
    }

    public Msg(Integer result, String tip, byte[] fingerData) {
        this.result = result;
        this.tip = tip;
        this.fingerData = fingerData;
    }


    protected Msg(Parcel in) {
        if (in.readByte() == 0) {
            result = null;
        } else {
            result = in.readInt();
        }
        tip = in.readString();
        fingerData = in.createByteArray();
        if (in.readByte() == 0) {
            index = null;
        } else {
            index = in.readInt();
        }
        if (in.readByte() == 0) {
            score = null;
        } else {
            score = in.readInt();
        }
        if (in.readByte() == 0) {
            fingerSize = null;
        } else {
            fingerSize = in.readInt();
        }
    }

    public static final Creator<Msg> CREATOR = new Creator<Msg>() {
        @Override
        public Msg createFromParcel(Parcel in) {
            return new Msg(in);
        }

        @Override
        public Msg[] newArray(int size) {
            return new Msg[size];
        }
    };

    public Msg(Integer result, String tip, byte[] fingerData,Integer fingerSize) {
        this.result = result;
        this.tip = tip;
        this.fingerData = fingerData;
        this.fingerSize = fingerSize;
    }

    public Msg(Integer result, String tip, Integer score) {
        this.result = result;
        this.tip = tip;
        this.score = score;
    }

    public Msg(Integer result, String tip, byte[] fingerData, Integer index, Integer score) {
        this.result = result;
        this.tip = tip;
        this.fingerData = fingerData;
        this.index = index;
        this.score = score;
    }

    public Msg(Integer result, String tip, byte[] fingerData, Integer index, Integer score,
               Integer fingerSize) {
        this.result = result;
        this.tip = tip;
        this.fingerData = fingerData;
        this.index = index;
        this.score = score;
        this.fingerSize = fingerSize;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (result == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(result);
        }
        parcel.writeString(tip);
        parcel.writeByteArray(fingerData);
        if (index == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(index);
        }
        if (score == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(score);
        }
        if (fingerSize == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(fingerSize);
        }
    }

    @Override
    public String toString() {
        return "Msg{" +
                "result=" + result +
                ", tip='" + tip + '\'' +
                ", fingerData=" + Arrays.toString(fingerData) +
                ", index=" + index +
                ", score=" + score +
                ", fingerSize=" + fingerSize +
                '}';
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }

    public byte[] getFingerData() {
        return fingerData;
    }

    public void setFingerData(byte[] fingerData) {
        this.fingerData = fingerData;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getFingerSize() {
        return fingerSize;
    }

    public void setFingerSize(Integer fingerSize) {
        this.fingerSize = fingerSize;
    }

    public static Creator<Msg> getCREATOR() {
        return CREATOR;
    }
}
