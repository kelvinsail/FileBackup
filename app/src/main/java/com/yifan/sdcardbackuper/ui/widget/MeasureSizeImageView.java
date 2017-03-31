package com.yifan.sdcardbackuper.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 宽高一致的ImageView
 *
 * Created by yifan on 2016/12/15.
 */
public class MeasureSizeImageView extends ImageView {

    public MeasureSizeImageView(Context context) {
        super(context);
    }

    public MeasureSizeImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MeasureSizeImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, widthMeasureSpec);
    }
}
