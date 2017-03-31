package com.yifan.utils.utils.menu;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import com.yifan.utils.utils.ResourcesUtils;

/**
 * 包含子菜单的菜单项
 *
 * Created by yifan on 2016/8/1.
 */
public class MenuGroupItem extends MenuBuilder.BaseMenuItem {

    /**
     * 子菜单项
     */
    MenuBuilder mMenuBuilder;

    public MenuGroupItem(String title, @DrawableRes int iconID, @IdRes int menuID) {
        this(title, ResourcesUtils.getDrawable(iconID), menuID);
    }

    public MenuGroupItem(@StringRes int titleID, @DrawableRes int iconID, @IdRes int menuID) {
        this(ResourcesUtils.getString(titleID), ResourcesUtils.getDrawable(iconID), menuID);
    }

    public MenuGroupItem(String title, Drawable icon, @IdRes int menuID) {
        super(title, icon, menuID);
        initMenu();
    }

    /**
     * 初始化菜单
     */
    private void initMenu() {
        mMenuBuilder = new MenuBuilder();
    }

    @Override
    public boolean isGroup() {
        return true;
    }

    @Override
    public MenuBuilder getSubMenu() {
        return mMenuBuilder;
    }
}
