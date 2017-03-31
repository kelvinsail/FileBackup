package com.yifan.utils.base.impl;

/**
 * LifeCycleable
 *
 * Created by yifan on 2016/8/4.
 */
public interface LifeCycleable {

    /**
     * 是否打印生命周期
     *
     * @return
     */
    boolean isPrintLifeCycle();

    /**
     * 是否处于活动状态
     *
     * @return
     */
    boolean isActived();

    /**
     * 获取界面标记标签字符串，用于日志输出或友盟统计，一般为类名，但不可用class.getSimpleName()
     *
     * @return
     */
    String getTAG();

}
