package com.yifan.utils.utils.menu.impl;

import android.graphics.drawable.Drawable;

import com.yifan.utils.utils.menu.MenuBuilder;

/**
 * 菜单项数据接口
 *
 * Created by yifan on 2016/8/2.
 */
public interface MenuItemImpl {

    /**
     * 初始化内容
     *
     * @param title
     * @param drawable
     */
    void init(String title, Drawable drawable, int menuID);

    /**
     * 设置图标
     *
     * @param drawable
     */
    MenuItemImpl setIcon(Drawable drawable);

    /**
     * 获取图标
     *
     * @return
     */
    Drawable getIcon();

    /**
     * 设置标题
     *
     * @param title
     */
    MenuItemImpl setTitle(String title);

    /**
     * 获取标题
     *
     * @return
     */
    String getTitle();

    /**
     * 类型
     *
     * @return
     */
    int getType();

    /**
     * 设置数据类型
     *
     * @param type
     */
    MenuItemImpl setType(int type);

    /**
     * 获取菜单项ID
     *
     * @return
     */
    int getMenuID();

    /**
     * 设置菜单项ID
     *
     * @param id
     */
    MenuItemImpl setMenuID(int id);

    /**
     * 是否包含子菜单
     *
     * @return
     */
    boolean isGroup();

    /**
     * 获得子菜单
     *
     * @return
     */
    MenuBuilder getSubMenu();

    /**
     * 设置 本地共享资源文件中存放数据的key
     *
     * @param key
     */
    MenuItemImpl setPreferencesKey(String key);

    /**
     * 本地共享资源文件中存放数据的key
     *
     * @return
     */
    String getPreferencesKey();
}
