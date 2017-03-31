package com.yifan.utils.utils.menu;

import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import com.yifan.utils.utils.ResourcesUtils;
import com.yifan.utils.utils.menu.impl.Checkable;
import com.yifan.utils.utils.menu.impl.MenuItemImpl;

import java.util.ArrayList;
import java.util.List;

/**
 * 菜单项创建工具类
 *
 * Created by yifan on 2016/8/2.
 */
public class MenuBuilder {

    /**
     * 菜单数据
     */
    List<MenuItemImpl> mMenu;

    public MenuBuilder() {
        mMenu = new ArrayList<MenuItemImpl>();
    }

    public List<MenuItemImpl> builder() {
        return mMenu;
    }

    /**
     * 添加一个新的菜单项
     *
     * @param item
     * @return
     */
    public MenuBuilder addItem(MenuItemImpl item) {
        if (null != mMenu) {
            mMenu.add(item);
        }
        return this;
    }

    /**
     * 添加一个新的菜单项
     *
     * @param titleID
     * @param iconID
     * @return
     */
    public MenuBuilder addItem(@StringRes int titleID, @DrawableRes int iconID, @IdRes int menuID) {
        addItem(new BaseMenuItem(titleID, iconID, menuID));
        return this;
    }

    /**
     * 添加一个新的菜单项
     *
     * @param title
     * @param iconID
     * @return
     */
    public MenuBuilder addItem(String title, @DrawableRes int iconID, @IdRes int menuID) {
        addItem(new BaseMenuItem(title, iconID, menuID));
        return this;
    }

    /**
     * 添加一个新的菜单项
     *
     * @param title
     * @param icon
     * @return
     */
    public MenuBuilder addItem(String title, Drawable icon, @IdRes int menuID) {
        addItem(new BaseMenuItem(title, icon, menuID));
        return this;
    }

    /**
     * 添加一个新的菜单项
     *
     * @param title
     * @param icon
     * @param isChecked
     * @param isCheckable
     * @param type
     * @param menuID
     * @return
     */
    public MenuBuilder addItem(@StringRes int title, @DrawableRes int icon,
                               boolean isChecked, boolean isCheckable, int type, int menuID) {
        BaseMenuItem item = new BaseMenuItem(title, icon, menuID);
        item.setCheckable(isCheckable);
        item.setChecked(isChecked);
        item.setType(type);
        addItem(item);
        return this;
    }


    /**
     * 添加一个新的菜单项
     *
     * @param title
     * @param icon
     * @param isChecked
     * @param isCheckable
     * @param type
     * @param menuID
     * @return
     */
    public MenuBuilder addItem(String title, Drawable icon, boolean isChecked,
                               boolean isCheckable, int type, int menuID) {
        BaseMenuItem item = new BaseMenuItem(title, icon, menuID);
        item.setCheckable(isCheckable);
        item.setChecked(isChecked);
        item.setType(type);
        addItem(item);
        return this;
    }

    /**
     * 获取菜单数据
     *
     * @return
     */
    public List<MenuItemImpl> getData() {
        return mMenu;
    }

    /**
     * 获取菜单数据的长度
     *
     * @return
     */
    public int getSize() {
        if (null == mMenu) {
            return 0;
        }
        return mMenu.size();
    }

    /**
     * 获取数据项类型
     *
     * @param position
     * @return
     */
    public int getItemType(int position) {
        if (position <= 0 || null == mMenu || (null != mMenu && position >= mMenu.size())) {
            return 0;
        }
        return mMenu.get(position).getType();
    }

    /**
     * 获取条目总数量
     *
     * @return
     */
    public int getTotalItemCount() {
        int totalCount = 0;
        if (null == mMenu) {
            return totalCount;
        }
        for (int i = 0; i < mMenu.size(); i++) {
            MenuItemImpl item = mMenu.get(i);
            if (null != item) {
                totalCount++;
                if (item.isGroup() && null != item.getSubMenu() &&
                        item.getSubMenu().getSize() > 0) {
                    totalCount += item.getSubMenu().getSize();
                }
            }
        }
        return totalCount;
    }

    /**
     * 获取所有数据包括子菜单
     *
     * @return
     */
    public List<MenuItemImpl> getAllData() {
        List<MenuItemImpl> list = new ArrayList<MenuItemImpl>();
        if (null != mMenu) {
            for (int i = 0; i < mMenu.size(); i++) {
                MenuItemImpl item = mMenu.get(i);
                if (null != item) {
                    list.add(item);
                    if (item.isGroup() && null != item.getSubMenu() &&
                            item.getSubMenu().getSize() > 0) {
                        list.addAll(item.getSubMenu().getAllData());
                    }
                }
            }
        }
        return list;
    }

    /**
     * 默认菜单项
     */
    public static class BaseMenuItem implements MenuItemImpl, Checkable {

        /**
         * 标题
         */
        String title;

        /**
         * 图标
         */
        Drawable icon;

        /**
         * 是否已选中
         */
        boolean isChecked;

        /**
         * 是否可选
         */
        boolean isCheckable;

        /**
         * 数据类型
         */
        int type;

        /**
         * 菜单项ID
         */
        int menuID;

        public BaseMenuItem(String title, Drawable icon, @IdRes int menuID) {
            init(title, icon, menuID);
        }

        public BaseMenuItem(String title, @DrawableRes int iconID, @IdRes int menuID) {
            init(title, ResourcesUtils.getDrawable(iconID), menuID);
        }

        public BaseMenuItem(@StringRes int titleID, @DrawableRes int iconID, @IdRes int menuID) {
            init(ResourcesUtils.getString(titleID), ResourcesUtils.getDrawable(iconID), menuID);
        }

        @Override
        public void init(String title, Drawable drawable, int menuID) {
            this.title = title;
            this.icon = drawable;
            this.menuID = menuID;
        }

        @Override
        public MenuItemImpl setIcon(Drawable drawable) {
            this.icon = drawable;
            return this;
        }

        @Override
        public Drawable getIcon() {
            return icon;
        }

        @Override
        public MenuItemImpl setTitle(String title) {
            this.title = title;
            return this;
        }

        @Override
        public String getTitle() {
            return title;
        }

        @Override
        public boolean isChecked() {
            return isChecked;
        }

        @Override
        public MenuItemImpl setChecked(boolean isChecked) {
            this.isChecked = isChecked;
            return this;
        }

        @Override
        public boolean isCheckable() {
            return isCheckable;
        }

        @Override
        public MenuItemImpl setCheckable(boolean isCheckable) {
            this.isCheckable = isCheckable;
            return this;
        }

        @Override
        public boolean toggle() {
            this.isChecked = !isChecked;
            return true;
        }

        @Override
        public int getType() {
            return type;
        }

        @Override
        public MenuItemImpl setType(int type) {
            this.type = type;
            return this;
        }

        @Override
        public int getMenuID() {
            return menuID;
        }

        @Override
        public MenuItemImpl setMenuID(int id) {
            menuID = id;
            return this;
        }

        @Override
        public boolean isGroup() {
            return false;
        }

        @Override
        public MenuBuilder getSubMenu() {
            return null;
        }

        @Override
        public MenuItemImpl setPreferencesKey(String key) {
            return null;
        }

        @Override
        public String getPreferencesKey() {
            return null;
        }

    }
}
