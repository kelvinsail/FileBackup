package com.yifan.sdcardbackuper.utils.copy;

/**
 * 照片拷贝工具类
 *
 * Created by yifan on 2016/12/12.
 */
public class PhotoCopyManager extends BaseCopyManager {

    public static final PhotoCopyManager getInstance() {
        return ManagerInatance.mInstance;
    }

    private PhotoCopyManager() {
        super();
    }

    private static class ManagerInatance {
        public static PhotoCopyManager mInstance = new PhotoCopyManager();
    }

}
