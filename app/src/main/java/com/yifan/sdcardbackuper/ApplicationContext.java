package com.yifan.sdcardbackuper;

import android.app.Application;
import android.os.Environment;

import com.yifan.utils.base.BaseApplication;

import java.io.File;

/**
 * Created by yifan on 2016/11/15.
 */
public class ApplicationContext extends BaseApplication {

    /**
     * 单一实例
     */
    private static ApplicationContext mInstances;

    public static ApplicationContext getInstance() {
        return mInstances;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstances = this;
    }
}
