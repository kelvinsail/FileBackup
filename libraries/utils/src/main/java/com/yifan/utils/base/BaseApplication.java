package com.yifan.utils.base;

import android.app.Application;

import com.yifan.utils.utils.CrashHandler;
import com.yifan.utils.utils.ResourcesUtils;

/**
 * BaseApplication Application基类
 *
 * Created by yifan on 2016/8/3.
 */
public abstract class BaseApplication extends Application {

    /**
     * 调试模式
     */
    public static final boolean DEBUG = true;

    /**
     * Application单例
     */
    private static BaseApplication mInstance;

    /**
     * 全局异常捕获
     */
    private static CrashHandler mCrashHandler;

    /**
     * 获取Application单一实例
     *
     * @return
     */
    public static BaseApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //初始化单一实例
        mInstance = this;
        //资源获取管理
        ResourcesUtils.setApplication(this);
        //异常捕获
        mCrashHandler = new CrashHandler();
    }
}
