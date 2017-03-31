package com.yifan.preferencesadapter.model;

import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 带单选的选择设置项
 *
 * Created by yifan on 2016/12/23.
 */
public class PreferencesCheckGroup extends PreferencesGroup {

    private HashMap<String, String> checkGroup;

    public PreferencesCheckGroup(CheckGroupBuilder builder) {
        super(builder);
        this.checkGroup = builder.checkGroup;
    }

    public static class CheckGroupBuilder extends GroupBuilder {

        private HashMap<String, String> checkGroup;

        public CheckGroupBuilder() {
            super();
            setType(Type.checkGroup);
        }

        @Override
        public PreferencesCheckGroup build() {
            return new PreferencesCheckGroup(this);
        }

        @Override
        @Deprecated
        public CheckGroupBuilder setItems(List<Preferences> items) {
            super.setItems(items);
            return this;
        }

        @Override
        @Deprecated
        public CheckGroupBuilder addSubItem(Preferences item) {
//            super.addSubItem(item);
            return this;
        }

        @Override
        @Deprecated
        public CheckGroupBuilder addSubItem(@IdRes int id, Drawable logo, Type type, String title, String preferencesKsy, PreserencesValueType valueType, Object defaultValue, boolean isEnable) {
//            super.addSubItem(id, logo, type, title, preferencesKsy, valueType, defaultValue, isEnable);
            return this;
        }

        @Override
        public CheckGroupBuilder setId(int id) {
            super.setId(id);
            return this;
        }

        @Override
        public CheckGroupBuilder setType(Type type) {
            super.setType(type);
            return this;
        }

        @Override
        public CheckGroupBuilder setTitle(String title) {
            super.setTitle(title);
            return this;
        }

        @Override
        public CheckGroupBuilder setSubTitle(String subTitle) {
            super.setSubTitle(subTitle);
            return this;
        }

        @Override
        public CheckGroupBuilder setLogo(Drawable logo) {
            super.setLogo(logo);
            return this;
        }

        @Override
        public CheckGroupBuilder setPreferencesKey(String preferencesKey) {
            super.setPreferencesKey(preferencesKey);
            return this;
        }

        @Override
        public CheckGroupBuilder setPreferencesValue(Object preferencesValue) {
            super.setPreferencesValue(preferencesValue);
            return this;
        }

        @Override
        public CheckGroupBuilder setPreferencesValueType(PreserencesValueType preferencesValueType) {
            super.setPreferencesValueType(preferencesValueType);
            return this;
        }

        @Override
        public CheckGroupBuilder setPreferencesDefault(Object preferencesDefault) {
            super.setPreferencesDefault(preferencesDefault);
            return this;
        }

        @Override
        @Deprecated
        public CheckGroupBuilder setItem(boolean item) {
//            super.setItem(item);
            return this;
        }

        @Override
        @Deprecated
        public CheckGroupBuilder setEnable(boolean enable) {
//            super.setEnable(enable);
            return this;
        }

        @Override
        public CheckGroupBuilder setParent(PreferencesGroup parent) {
            super.setParent(parent);
            return this;
        }

        public CheckGroupBuilder setCheckGroup(HashMap<String, String> checkGroup) {
            this.checkGroup = checkGroup;
            return this;
        }

    }

    public HashMap<String, String> getCheckGroup() {
        return checkGroup;
    }

    public PreferencesCheckGroup setCheckGroup(HashMap<String, String> checkGroup) {
        this.checkGroup = checkGroup;
        return this;
    }

    @Override
    public List<Preferences> getAllDatas() {
        List<Preferences> list = new ArrayList<>();
        if (null != getTitle()) {
            list.add(this);
        }
        return list;
    }
}
