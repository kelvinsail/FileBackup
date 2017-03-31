package com.yifan.sdcardbackuper.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;

import com.yifan.sdcardbackuper.R;

/**
 * Created by yifan on 2016/12/25.
 */

public class PullToRefreshLayout extends SwipeRefreshLayout {

    public PullToRefreshLayout(Context context) {
        this(context, null);
    }

    public PullToRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setColorSchemeResources(R.color.colorAccent);
    }
}
