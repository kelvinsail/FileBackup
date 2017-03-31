package com.yifan.utils.base.impl;

import android.support.design.widget.AppBarLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.ViewStubCompat;
import android.view.View;


import com.yifan.utils.widget.TitleBar;

import java.lang.ref.WeakReference;

/**
 * TitleBarPager接口抽取
 * <p>
 * Created by yifan on 2016/7/18.
 */
public interface TitleImpl {

    /**
     * 获取TitleBar对象
     *
     * @return
     */
    TitleBar getSupportTitleBar();

    /**
     * 获取拓展布局ViewStubCompat，可能为空，非联动布局下为空
     *
     * @return @nullable ViewStubCompat
     */
    ViewStubCompat getBarExpandStub();

    /**
     * 获取AppBar高度
     *
     * @return
     */
    int getAppBarHeight();


    /**
     * 获取AppBarLayout ,未联动情况下获取为空
     *
     * @return
     */
    AppBarLayout getAppBarLayout();

    /**
     * 获取根布局
     *
     * @return
     */
    View getContentView();

    /**
     * 设置主界面抽屉
     *
     * @param drawerLayout
     */
    void setDrawerLayout(WeakReference<DrawerLayout> drawerLayout);

    /**
     * 设置导航栏与抽屉的联动
     */
    void setTitleBarToggle();

    /**
     * 设置导航栏是否透明
     *
     * @param isToolBarTranslaction
     */
    void setToolBarTranslaction(boolean isToolBarTranslaction);
}
