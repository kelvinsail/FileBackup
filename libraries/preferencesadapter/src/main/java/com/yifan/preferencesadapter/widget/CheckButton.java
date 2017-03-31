package com.yifan.preferencesadapter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.yifan.preferencesadapter.R;
import com.yifan.utils.utils.ResourcesUtils;

/**
 * 自定义样式的RadioButton
 *
 * Created by yifan on 2016/12/26.
 */

public class CheckButton extends RadioButton {

    public CheckButton(Context context) {
        this(context, null);
    }

    public CheckButton(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化控件
     */
    public void initView() {
//            Drawable drawable = getButtonDrawable();
//            int size = ResourcesUtils.getDimensionPixelSize(R.dimen.setting_notifier_item_checked_size);
//            drawable.setBounds(0, 0, size, size);
//            setCompoundDrawables(null, null, drawable, null);
        int padding = ResourcesUtils.getDimensionPixelSize(R.dimen.base_padding_large);
        setPadding(padding, getPaddingTop(), padding, getPaddingBottom());
    }

}