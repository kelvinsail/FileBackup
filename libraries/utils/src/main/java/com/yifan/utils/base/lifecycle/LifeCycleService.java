package com.yifan.utils.base.lifecycle;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.yifan.utils.base.impl.LifeCycleable;

/**
 * LifeCycleService 后台服务生命周期基类
 *
 * Created by yifan on 2016/8/4.
 */
public abstract class LifeCycleService extends Service implements LifeCycleable {


    /**
     * 是否存活
     */
    public boolean isActived;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (isPrintLifeCycle()) {
            Log.i(getTAG(), "onCreate");
        }
        isActived = true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (isPrintLifeCycle()) {
            Log.i(getTAG(), "onStartCommand");
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        super.onStart(intent, startId);
        if (isPrintLifeCycle()) {
            Log.i(getTAG(), "onStart");
        }
    }

    @Override
    public void onDestroy() {
        if (isPrintLifeCycle()) {
            Log.i(getTAG(), "onDestroy");
        }
        isActived = false;
        super.onDestroy();
    }

    @Override
    public boolean isPrintLifeCycle() {
        return false;
    }

    @Override
    public boolean isActived() {
        return isActived;
    }

    @Override
    public abstract String getTAG();
}
