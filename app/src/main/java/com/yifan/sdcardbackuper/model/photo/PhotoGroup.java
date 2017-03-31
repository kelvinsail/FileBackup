package com.yifan.sdcardbackuper.model.photo;

import com.yifan.sdcardbackuper.utils.copy.PhotoCopyManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 照片集合
 *
 * Created by yifan on 2016/12/12.
 */
public class PhotoGroup {

    /**
     * 文件夹路径
     */
    public String path;

    /**
     * 照片集合
     */
    private List<PhotoGroupItem> mItems;

    /**
     * 是否选中
     */
    private boolean isChecked;

    public PhotoGroup(String path) {
        this.path = path;
        this.mItems = new ArrayList<>();
    }

    /**
     * 添加一个图片
     *
     * @param id
     * @param path
     */
    public void addPhoto(int id, String path) {
        File file = new File(path);
        //判断该集合里是否已有该图片
        if (!isPhotoExisted(path)) {
            mItems.add(new PhotoGroupItem(this, id, path, file.getName()));
        }
    }

    /**
     * 判断图片是否存在
     *
     * @param photoPath
     * @return
     */
    public boolean isPhotoExisted(String photoPath) {
        for (PhotoGroupItem item : mItems) {
            if (item.path.equals(photoPath)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 获取图片列表
     *
     * @return
     */
    public List<PhotoGroupItem> getItems() {
        return mItems;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public void setCheckedAll(boolean checked) {
        isChecked = checked;
        for (PhotoGroupItem item : mItems) {
            item.setChecked(checked);
            if (isChecked) {
                PhotoCopyManager.getInstance().addFile(item.path);
            } else {
                PhotoCopyManager.getInstance().deleteFile(item.path);
            }
        }
    }
}
