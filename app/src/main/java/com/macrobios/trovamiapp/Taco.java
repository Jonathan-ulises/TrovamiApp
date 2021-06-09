package com.macrobios.trovamiapp;

import android.os.Parcel;
import android.os.Parcelable;


public class Taco implements Parcelable {
    private Double latitude;
    private Double longitud;
    private String flavor;


    public Taco(Double latitude, Double longitud, String flavor) {
        this.latitude = latitude;
        this.longitud = longitud;
        this.flavor = flavor;
    }

    protected Taco(Parcel in) {
        if (in.readByte() == 0) {
            latitude = null;
        } else {
            latitude = in.readDouble();
        }
        if (in.readByte() == 0) {
            longitud = null;
        } else {
            longitud = in.readDouble();
        }
        flavor = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (latitude == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(latitude);
        }
        if (longitud == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeDouble(longitud);
        }
        dest.writeString(flavor);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Taco> CREATOR = new Creator<Taco>() {
        @Override
        public Taco createFromParcel(Parcel in) {
            return new Taco(in);
        }

        @Override
        public Taco[] newArray(int size) {
            return new Taco[size];
        }
    };

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitud() {
        return longitud;
    }

    public String getFlavor() {
        return flavor;
    }
}
