package com.yifan.sdcardbackuper.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.File;

/**
 * 文件列表类
 *
 * Created by yifan on 2016/11/15.
 */
public class FileItem implements Parcelable {

    /**
     * 文件名
     */
    private String name;

    /**
     * 路径
     */
    private String path;

    /**
     * 是否选中
     */
    private boolean isChecked;

    public FileItem(String path, String name) {
        this.path = new StringBuilder(path).append(File.separator).append(name).toString();
        this.name = name;
    }

    protected FileItem(Parcel in) {
        name = in.readString();
        path = in.readString();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(path);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FileItem> CREATOR = new Creator<FileItem>() {
        @Override
        public FileItem createFromParcel(Parcel in) {
            return new FileItem(in);
        }

        @Override
        public FileItem[] newArray(int size) {
            return new FileItem[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
