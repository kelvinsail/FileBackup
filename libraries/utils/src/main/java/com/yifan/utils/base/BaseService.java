package com.yifan.utils.base;

import com.yifan.utils.base.lifecycle.LifeCycleService;

/**
 * BaseService 后台服务基类，功能实现
 *
 * Created by yifan on 2016/8/4.
 */
public abstract class BaseService extends LifeCycleService {

    @Override
    public abstract String getTAG();

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
