package com.sd.tgfinger.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created By pq
 * on 2019/9/19
 * 指静脉验证的结果
 */
public class VerifyResult implements Parcelable {

    private Integer result;
    private Integer compareScore;
    private Integer index;
    private byte[] updateFingerData;

    protected VerifyResult(Parcel in) {
        if (in.readByte() == 0) {
            result = null;
        } else {
            result = in.readInt();
        }
        if (in.readByte() == 0) {
            compareScore = null;
        } else {
            compareScore = in.readInt();
        }
        if (in.readByte() == 0) {
            index = null;
        } else {
            index = in.readInt();
        }
        updateFingerData = in.createByteArray();
    }

    public static final Creator<VerifyResult> CREATOR = new Creator<VerifyResult>() {
        @Override
        public VerifyResult createFromParcel(Parcel in) {
            return new VerifyResult(in);
        }

        @Override
        public VerifyResult[] newArray(int size) {
            return new VerifyResult[size];
        }
    };

    public VerifyResult(Integer result, Integer compareScore, Integer index, byte[] updateFingerData) {
        this.result = result;
        this.compareScore = compareScore;
        this.index = index;
        this.updateFingerData = updateFingerData;
    }

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public Integer getCompareScore() {
        return compareScore;
    }

    public void setCompareScore(Integer compareScore) {
        this.compareScore = compareScore;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public byte[] getUpdateFingerData() {
        return updateFingerData;
    }

    public void setUpdateFingerData(byte[] updateFingerData) {
        this.updateFingerData = updateFingerData;
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
        if (compareScore == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(compareScore);
        }
        if (index == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(index);
        }
        parcel.writeByteArray(updateFingerData);
    }

    @Override
    public String toString() {
        return "VerifyResult{" +
                "result=" + result +
                ", compareScore=" + compareScore +
                ", index=" + index +
                ", updateFingerData=" + Arrays.toString(updateFingerData) +
                '}';
    }
}
