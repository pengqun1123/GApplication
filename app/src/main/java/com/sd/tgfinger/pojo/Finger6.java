package com.sd.tgfinger.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Property;

import java.util.Arrays;

/**
 * Created By pq
 * on 2019/9/29
 */
@Entity
public class Finger6 implements Parcelable {
    @Id(autoincrement = true)
    Long uId;
    @Property(nameInDb = "feature")
    byte[] finger6Feature;
    @Generated(hash = 2087254474)
    public Finger6(Long uId, byte[] finger6Feature) {
        this.uId = uId;
        this.finger6Feature = finger6Feature;
    }
    @Generated(hash = 1398466695)
    public Finger6() {
    }

    protected Finger6(Parcel in) {
        if (in.readByte() == 0) {
            uId = null;
        } else {
            uId = in.readLong();
        }
        finger6Feature = in.createByteArray();
    }

    public static final Creator<Finger6> CREATOR = new Creator<Finger6>() {
        @Override
        public Finger6 createFromParcel(Parcel in) {
            return new Finger6(in);
        }

        @Override
        public Finger6[] newArray(int size) {
            return new Finger6[size];
        }
    };

    public Long getUId() {
        return this.uId;
    }
    public void setUId(Long uId) {
        this.uId = uId;
    }
    public byte[] getFinger6Feature() {
        return this.finger6Feature;
    }
    public void setFinger6Feature(byte[] finger6Feature) {
        this.finger6Feature = finger6Feature;
    }


    @Override
    public String toString() {
        return "Finger6{" +
                "uId=" + uId +
                ", finger6Feature=" + Arrays.toString(finger6Feature) +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (uId == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeLong(uId);
        }
        parcel.writeByteArray(finger6Feature);
    }
}
