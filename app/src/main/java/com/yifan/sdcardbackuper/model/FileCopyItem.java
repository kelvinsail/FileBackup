package com.yifan.sdcardbackuper.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 文件复制model类
 *
 * Created by yifan on 2016/11/17.
 */
public class FileCopyItem implements Parcelable {

    /**
     * 文件路径
     */
    private String path;

    /**
     * 是否复制成功
     */
    private boolean isSuccess;

    public FileCopyItem(String path) {
        this.path = path;
    }

    protected FileCopyItem(Parcel in) {
        path = in.readString();
        isSuccess = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeByte((byte) (isSuccess ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileCopyItem> CREATOR = new Creator<FileCopyItem>() {
        @Override
        public FileCopyItem createFromParcel(Parcel in) {
            return new FileCopyItem(in);
        }

        @Override
        public FileCopyItem[] newArray(int size) {
            return new FileCopyItem[size];
        }
    };

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }
}
