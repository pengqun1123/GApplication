package com.sd.tgfinger.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Arrays;

/**
 * Created By pq
 * on 2019/9/18
 * 指静脉登记的结果
 */
public class RegisterResult implements Parcelable {

    private Integer result;
    private byte[] registerFinger;

    public RegisterResult(Integer result, byte[] registerFinger) {
        this.result = result;
        this.registerFinger = registerFinger;
    }

    protected RegisterResult(Parcel in) {
        if (in.readByte() == 0) {
            result = null;
        } else {
            result = in.readInt();
        }
        registerFinger = in.createByteArray();
    }

    public static final Creator<RegisterResult> CREATOR = new Creator<RegisterResult>() {
        @Override
        public RegisterResult createFromParcel(Parcel in) {
            return new RegisterResult(in);
        }

        @Override
        public RegisterResult[] newArray(int size) {
            return new RegisterResult[size];
        }
    };

    public Integer getResult() {
        return result;
    }

    public void setResult(Integer result) {
        this.result = result;
    }

    public byte[] getRegisterFinger() {
        return registerFinger;
    }

    public void setRegisterFinger(byte[] registerFinger) {
        this.registerFinger = registerFinger;
    }

    @Override
    public String toString() {
        return "RegisterResult{" +
                "result=" + result +
                ", registerFinger=" + Arrays.toString(registerFinger) +
                '}';
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
        parcel.writeByteArray(registerFinger);
    }
}
