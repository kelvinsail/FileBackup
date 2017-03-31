package com.yifan.utils.utils.menu.impl;

/**
 * 数据项选择接口
 *
 * Created by yifan on 2016/8/2.
 */
public interface Checkable {

    /**
     * 是否已选中
     *
     * @return
     */
    boolean isChecked();

    /**
     * 设置是否已选中
     *
     * @param isChecked
     */
    MenuItemImpl setChecked(boolean isChecked);

    /**
     * 是否可选
     *
     * @return
     */
    boolean isCheckable();

    /**
     * 设置是否可选
     */
    MenuItemImpl setCheckable(boolean isCheckable);

    /**
     * 开关
     *
     * @return true:开启成功;false:开启失败
     */
    boolean toggle();
}
