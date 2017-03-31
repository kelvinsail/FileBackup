package com.yifan.preferencesadapter.model;

import android.graphics.drawable.Drawable;
import android.support.annotation.IdRes;

/**
 * 设置项model类
 *
 * Created by yifan on 2016/12/22.
 */

public class Preferences {

    /**
     * 设置项ID
     */
    @IdRes
    private int id;

    /**
     * 项目类型
     */
    private Type type;

    /**
     * 设置项名称
     */
    private String title;

    /**
     * 设置项副标题
     */
    private String subTitle;

    /**
     * 设置项logo
     */
    private Drawable logo;

    /**
     * 本地储存参数key
     */
    private String preferencesKey;

    /**
     * 本地储存参数Value
     */
    private Object preferencesValue;

    /**
     * 本地储存参数类型
     */
    private PreserencesValueType preferencesValueType;

    /**
     * 本地储存参数默认值
     */
    private Object preferencesDefault;

    /**
     * 是否为单项
     */
    private boolean isItem;

    /**
     * 是否开启
     */
    private boolean isEnable;

    /**
     * 父集合
     */
    private PreferencesGroup parent;

    public Type getType() {
        if (null == type) {
            return Type.normal;
        }
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public Drawable getLogo() {
        return logo;
    }

    public void setLogo(Drawable logo) {
        this.logo = logo;
    }

    public String getPreferencesKey() {
        return preferencesKey;
    }

    public void setPreferencesKey(String preferencesKey) {
        this.preferencesKey = preferencesKey;
    }

    public Object getPreferencesValue() {
        return preferencesValue;
    }

    public void setPreferencesValue(Object preferencesValue) {
        this.preferencesValue = preferencesValue;
    }

    public PreserencesValueType getPreferencesValueType() {
        return preferencesValueType;
    }

    public void setPreferencesValueType(PreserencesValueType preferencesValueType) {
        this.preferencesValueType = preferencesValueType;
    }

    public Object getPreferencesDefault() {
        return preferencesDefault;
    }

    public void setPreferencesDefault(Object preferencesDefault) {
        this.preferencesDefault = preferencesDefault;
    }

    public boolean isItem() {
        return isItem;
    }

    public void setItem(boolean item) {
        isItem = item;
    }

    public boolean isEnable() {
        return isEnable;
    }

    public void setEnable(boolean enable) {
        isEnable = enable;
    }

    public PreferencesGroup getParent() {
        return parent;
    }

    public void setParent(PreferencesGroup parent) {
        this.parent = parent;
    }

    /**
     * 创建器
     */
    public static class Builder {

        @IdRes
        private int id;
        private Type type;
        private String title;
        private String subTitle;
        private Drawable logo;
        private String preferencesKey;
        private Object preferencesValue;
        private PreserencesValueType preferencesValueType;
        private Object preferencesDefault;

        @Deprecated
        private boolean isItem;
        private boolean isEnable;
        private PreferencesGroup parent;

        public Builder() {
            isEnable = true;
        }

        public Builder setId(int id) {
            this.id = id;
            this.isEnable = true;
            return this;
        }

        public Builder setType(Type type) {
            this.type = type;
            return this;
        }

        public Builder setTitle(String title) {
            this.title = title;
            return this;
        }

        public Builder setSubTitle(String subTitle) {
            this.subTitle = subTitle;
            return this;
        }

        public Builder setLogo(Drawable logo) {
            this.logo = logo;
            return this;
        }

        public Builder setPreferencesKey(String preferencesKey) {
            this.preferencesKey = preferencesKey;
            return this;
        }

        public Builder setPreferencesValue(Object preferencesValue) {
            this.preferencesValue = preferencesValue;
            return this;
        }

        public Builder setPreferencesValueType(PreserencesValueType preferencesValueType) {
            this.preferencesValueType = preferencesValueType;
            return this;
        }

        public Builder setPreferencesDefault(Object preferencesDefault) {
            this.preferencesDefault = preferencesDefault;
            return this;
        }

        public Builder setItem(boolean item) {
            isItem = item;
            return this;
        }

        public Builder setEnable(boolean enable) {
            isEnable = enable;
            return this;
        }

        public Builder setParent(PreferencesGroup parent) {
            this.parent = parent;
            return this;
        }

        public Preferences build() {
            return new Preferences(this);
        }
    }

    public Preferences(Builder builder) {
        this.id = builder.id;
        this.type = builder.type;
        this.title = builder.title;
        this.subTitle = builder.subTitle;
        this.logo = builder.logo;
        this.preferencesKey = builder.preferencesKey;
        this.preferencesValue = builder.preferencesValue;
        this.preferencesValueType = builder.preferencesValueType;
        this.preferencesDefault = builder.preferencesDefault;
        this.isItem = builder.isItem;
        this.isEnable = builder.isEnable;
        this.parent = builder.parent;
    }
}
