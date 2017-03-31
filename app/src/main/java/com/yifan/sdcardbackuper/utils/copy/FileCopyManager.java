package com.yifan.sdcardbackuper.utils.copy;

/**
 * 文件拷贝工具类
 *
 * Created by yifan on 2016/11/17.
 */
public class FileCopyManager extends BaseCopyManager{

    public static final FileCopyManager getInstance() {
        return ManagerInatance.mInstance;
    }

    private FileCopyManager() {
        super();
    }

    private static class ManagerInatance {
        public static FileCopyManager mInstance = new FileCopyManager();
    }

}
