package com.yifan.utils.widget;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.AttributeSet;

/**
 * 自定义导航栏
 *
 * Created by yifan on 2016/7/18.
 */
public class TitleBar extends Toolbar {

    public TitleBar(Context context) {
        super(context);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
