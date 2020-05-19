package com.example.hansol.spot_200510_hs;

import android.os.Parcel;
import android.os.Parcelable;

public class send_data implements Parcelable {
    String code;
    String name;

    public send_data(String code, String name){
        this.code = code;
        this.name = name;
    }

    protected send_data(Parcel in) {
        code = in.readString();
        name = in.readString();
    }

    public static final Creator<send_data> CREATOR = new Creator<send_data>() {
        @Override
        public send_data createFromParcel(Parcel in) {
            return new send_data(in);
        }

        @Override
        public send_data[] newArray(int size) {
            return new send_data[size];
        }
    };

    public String getCode() { return this.code; }

    String getName() {
        return this.name;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    //객체를 전달할 때 호출
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(code);
        dest.writeString(name);
    }
}