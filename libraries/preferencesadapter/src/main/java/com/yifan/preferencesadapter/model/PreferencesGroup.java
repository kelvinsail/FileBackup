package com.yifan.preferencesadapter.model;

import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;
import android.support.annotation.StringRes;

import java.util.ArrayList;
import java.util.List;

/**
 * 选项群组
 *
 * Created by yifan on 2016/12/22.
 */
public class PreferencesGroup extends Preferences {

    /**
     * 子设置项数组
     */
    private List<Preferences> items;

    public PreferencesGroup(GroupBuilder builder) {
        super(builder);
        items = builder.items;
    }

    public void setItems(List<Preferences> items) {
        this.items = items;
    }

    public List<Preferences> getItems() {
        return items;
    }

    /**
     * 获取设置项数量
     *
     * @return
     */
    public int getSize() {
        int size = null == getTitle() ? 0 : 1;
        if (items.size() > 0) {
            for (Preferences preferences : items) {
                if (null != preferences) {
                    if (preferences instanceof PreferencesGroup) {
                        size += ((PreferencesGroup) preferences).getSize();
                    } else {
                        size++;
                    }
                }
            }
        }
        return size;
    }

    /**
     * 拉取所有设置项数据整合成数组
     *
     * @return
     */
    public List<Preferences> getAllDatas() {
        List<Preferences> list = new ArrayList<>();
        if (null != getTitle()) {//标题不为空，普通集合项
            list.add(this);
        } else {//标题为空，根设置项
            if (null != items) {
                for (Preferences preferences : items) {
                    if (null != preferences) {
                        if (preferences instanceof PreferencesGroup) {
                            list.addAll(((PreferencesGroup) preferences).getAllDatas());
                        } else {
                            list.add(preferences);
                        }
                    }
                }
            }
        }
        return list;
    }

    /**
     * 设置项群组创建器
     */
    public static class GroupBuilder extends Builder {

        List<Preferences> items;

        public GroupBuilder() {
            super();
            setType(Type.group);
            items = new ArrayList<>();
        }

        @Override
        public PreferencesGroup build() {
            PreferencesGroup group = new PreferencesGroup(this);
            if (null != group.items && items.size() > 0) {
                for (Preferences item : group.items) {
                    item.setParent(group);
                }
            }
            return group;
        }

        public GroupBuilder setItems(List<Preferences> items) {
            this.items = items;
            return this;
        }

        /**
         * 添加子项目
         *
         * @param item
         * @return
         */
        public GroupBuilder addSubItem(Preferences item) {
            if (null != item.getParent() && !item.getParent().isEnable()) {
                item.setEnable(false);
            }
            items.add(item);
            return this;
        }

        /**
         * 添加新的子项目
         *
         * @param id
         * @param logo
         * @param type
         * @param title
         * @param preferencesKsy
         * @param valueType
         * @param defaultValue
         * @param isEnable
         * @return
         */
        public GroupBuilder addSubItem(@IdRes int id, Drawable logo, Type type, String title,
                                       String preferencesKsy, PreserencesValueType valueType,
                                       Object defaultValue, boolean isEnable) {
            Preferences item = new Builder().setId(id).setLogo(logo).setType(type).setTitle(title).setPreferencesKey(preferencesKsy)
                    .setPreferencesValueType(valueType).setPreferencesDefault(defaultValue).setEnable(isEnable).build();
            items.add(item);
            return this;
        }


        @Override
        public GroupBuilder setId(int id) {
            return this;
        }

        @Override
        public GroupBuilder setType(Type type) {
            super.setType(type);
            return this;
        }

        @Override
        public GroupBuilder setTitle(String title) {
            super.setTitle(title);
            return this;
        }

        @Override
        public GroupBuilder setSubTitle(String subTitle) {
            super.setSubTitle(subTitle);
            return this;
        }

        @Override
        public GroupBuilder setLogo(Drawable logo) {
            super.setLogo(logo);
            return this;
        }

        @Override
        public GroupBuilder setPreferencesKey(String preferencesKey) {
            super.setPreferencesKey(preferencesKey);
            return this;
        }

        @Override
        public GroupBuilder setPreferencesValue(Object preferencesValue) {
            super.setPreferencesValue(preferencesValue);
            return this;
        }

        @Override
        public GroupBuilder setPreferencesValueType(PreserencesValueType preferencesValueType) {
            super.setPreferencesValueType(preferencesValueType);
            return this;
        }

        @Override
        public GroupBuilder setPreferencesDefault(Object preferencesDefault) {
            super.setPreferencesDefault(preferencesDefault);
            return this;
        }

        @Override
        public GroupBuilder setItem(boolean item) {
            super.setItem(item);
            return this;
        }

        @Override
        public GroupBuilder setEnable(boolean enable) {
            super.setEnable(enable);
            return this;
        }

        @Override
        public GroupBuilder setParent(PreferencesGroup parent) {
            super.setParent(parent);
            return this;
        }
    }

}
