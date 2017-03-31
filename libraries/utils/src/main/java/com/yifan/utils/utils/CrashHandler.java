package com.yifan.utils.utils;

import android.util.Log;

import com.yifan.utils.R;
import com.yifan.utils.base.BaseApplication;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * CrashHandler 全局异常捕获Handler
 *
 * Created by yifan on 2016/8/3.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {

    /**
     * 全局异常捕获Handler
     */
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    public CrashHandler() {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        if (null != mDefaultHandler && handleException(throwable)) {
            mDefaultHandler.uncaughtException(thread, throwable);
        } else {
            System.exit(0);
        }
    }

    /**
     * 处理异常
     *
     * @param throwable
     * @return
     */
    private boolean handleException(final Throwable throwable) {
        if (BaseApplication.DEBUG) {
            //保存异常
            return FileUtils.printDataToFile("log", "log", getCrashInfo(throwable));
        }
        return false;
    }

    /**
     * 获取异常信息
     *
     * @param ex
     * @return
     */
    private String getCrashInfo(Throwable ex) {
        //取出异常信息
        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String message = writer.toString();
        Log.e(ResourcesUtils.getString(R.string.app_name), message);
        return message;

    }
}
