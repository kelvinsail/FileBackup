package com.yifan.utils.base;

/**
 * Created by yifan on 2016/11/15.
 */
public interface ViewImpl {

    /**
     * 获取界面标签，用于log输出、标记等
     *
     * @return
     */
    String getTAG();

    /**
     * 是否打印生命周期日志
     *
     * @return
     */
    boolean isPrintCyclerLife();
}
