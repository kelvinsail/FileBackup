package com.yifan.sdcardbackuper.utils;

import com.yifan.sdcardbackuper.model.photo.PhotoGroup;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yifan on 2016/12/12.
 */

public class PhotoDataManager {

    /**
     * 图片集合
     */
    private List<PhotoGroup> mGroups;

    public static PhotoDataManager getInstance() {
        return ManagerInstance.mInstances;
    }

    private static class ManagerInstance {
        public static PhotoDataManager mInstances = new PhotoDataManager();
    }

    private PhotoDataManager() {
        this.mGroups = new ArrayList<>();
    }

    /**
     * 清空数据
     */
    public void clear() {
        if (null != mGroups) {
            mGroups.clear();
        } else {
            mGroups = new ArrayList<>();
        }
    }

    public int getGroupsSize() {
        return mGroups.size();
    }

    public List<PhotoGroup> getGroups() {
        return mGroups;
    }

    public void addPhoto(int id, String path) {
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        PhotoGroup parentGroup = null;
        for (PhotoGroup group : mGroups) {
            if (file.getParentFile().getAbsolutePath().equals(group.path)) {
                parentGroup = group;
                break;
            }
        }
        if (null != parentGroup) {
            parentGroup.addPhoto(id, path);
        } else {
            parentGroup = new PhotoGroup(file.getParentFile().getAbsolutePath());
            parentGroup.addPhoto(id, path);
            mGroups.add(parentGroup);
        }
    }
}
