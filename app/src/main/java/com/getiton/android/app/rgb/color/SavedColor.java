package com.getiton.android.app.rgb.color;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by christophesmet on 02/11/15.
 */


public class SavedColor implements Parcelable {

    private int id;
    private int mColor = 0;

    public SavedColor(int id, int color) {
        this.id = id;
        this.mColor = color;
    }

    public SavedColor(int id, boolean addNewColor) {
        this.id = id;
        this.mColor = 0;
    }

    public void setColor(int color) {
        mColor = color;
    }

    public int getId() {
        return id;
    }

    protected SavedColor(Parcel in) {
        id = in.readInt();
        mColor = in.readInt();
    }

    public static final Creator<SavedColor> CREATOR = new Creator<SavedColor>() {
        @Override
        public SavedColor createFromParcel(Parcel in) {
            return new SavedColor(in);
        }

        @Override
        public SavedColor[] newArray(int size) {
            return new SavedColor[size];
        }
    };

    public boolean isAddNewColor() {
        return this.id==-1;
    }

    public int getColor() {
        return mColor;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(mColor);
    }
}