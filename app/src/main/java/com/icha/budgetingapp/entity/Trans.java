package com.icha.budgetingapp.entity;

import android.os.Parcel;
import android.os.Parcelable;

public class Trans implements Parcelable {
    private int id;
    private String type;
    private double amount;
    private String description;
    private String date;

    public Trans(){}
    public Trans(int id, String type, double amount, String description, String date) {
        this.id = id;
        this.type = type;
        this.amount = amount;
        this.description = description;
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.type);
        dest.writeDouble(this.amount);
        dest.writeString(this.description);
        dest.writeString(this.date);
    }

    protected Trans(Parcel in) {
        this.id = in.readInt();
        this.type = in.readString();
        this.amount = in.readDouble();
        this.description = in.readString();
        this.date = in.readString();
    }

    public static final Parcelable.Creator<Trans> CREATOR = new Parcelable.Creator<Trans>() {
        @Override
        public Trans createFromParcel(Parcel source) {
            return new Trans(source);
        }

        @Override
        public Trans[] newArray(int size) {
            return new Trans[size];
        }
    };
}
