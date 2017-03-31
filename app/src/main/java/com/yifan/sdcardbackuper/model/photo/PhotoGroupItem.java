package com.yifan.sdcardbackuper.model.photo;

import android.os.Parcel;
import android.os.Parcelable;

import com.yifan.sdcardbackuper.utils.copy.PhotoCopyManager;

/**
 * 照片集合子项目
 *
 * Created by yifan on 2016/12/12.
 */
public class PhotoGroupItem implements Parcelable {

    /**
     * 路径，包含名称
     */
    public String path;

    /**
     * 文件名称
     */
    public String name;

    /**
     * 图片id
     */
    public int id;

    /**
     * 是否选中
     */
    private boolean isChecked;

    /**
     * 所属图片集合
     */
    public PhotoGroup group;

    public PhotoGroupItem(PhotoGroup group, int id, String path, String name) {
        this.group = group;
        this.id = id;
        this.path = path;
        this.name = name;
    }

    protected PhotoGroupItem(Parcel in) {
        path = in.readString();
        name = in.readString();
        isChecked = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(path);
        dest.writeString(name);
        dest.writeByte((byte) (isChecked ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoGroupItem> CREATOR = new Creator<PhotoGroupItem>() {
        @Override
        public PhotoGroupItem createFromParcel(Parcel in) {
            return new PhotoGroupItem(in);
        }

        @Override
        public PhotoGroupItem[] newArray(int size) {
            return new PhotoGroupItem[size];
        }
    };

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
        if (isChecked) {
            group.setChecked(true);
            PhotoCopyManager.getInstance().addFile(path);
        } else {
            PhotoCopyManager.getInstance().deleteFile(path);
            for (PhotoGroupItem item : group.getItems()) {
                if (item.isChecked) {
                    return;
                }
            }
            group.setChecked(false);
        }
    }
}
