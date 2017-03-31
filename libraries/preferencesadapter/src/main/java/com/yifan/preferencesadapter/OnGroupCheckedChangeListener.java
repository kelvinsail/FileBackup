package com.yifan.preferencesadapter;

import android.view.View;

import com.yifan.preferencesadapter.widget.CheckButton;

/**
 * 选项选中事件监听器
 *
 * Created by yifan on 2016/12/26.
 */
public interface OnGroupCheckedChangeListener {

    /**
     * 选项变动
     *
     * @param view           {@link CheckButton}
     * @param preferencesKey 选项参数储存key
     * @param value          选中的参数
     * @param name           选中的参数名称
     */
    void onGroupCheckedChange(View view, String preferencesKey, String value, String name);
}