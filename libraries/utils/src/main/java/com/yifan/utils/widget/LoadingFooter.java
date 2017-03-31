package com.yifan.utils.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yifan.utils.R;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.utils.utils.ResourcesUtils;
import com.yifan.utils.utils.WidgetUtils;

/**
 * 全局列表底部加载更多控件
 *
 * Created by yifan on 2016/8/24.
 */
public class LoadingFooter extends LinearLayout {

    /**
     * 控件ID，用于点击事件判断等
     */
    public static final int LOADINGFOOTER_VIEW_ID = R.id.loading_footer_view;

    /**
     * 进度条
     */
    private ProgressBar mProgressBar;
    /**
     * 文本提示
     */
    private TextView mTipsText;

    /**
     * 加载状态
     */
    private BaseRecyclerAdapter.LoadingState mState;

    public LoadingFooter(Context context) {
        this(context, null);
    }

    public LoadingFooter(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingFooter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    /**
     * 初始化布局、样式及ID
     */
    private void init() {
        //设置点击透明底色的效果
        WidgetUtils.setItemClickBackgroundTransParent(this);
        //设置ID
        setId(LOADINGFOOTER_VIEW_ID);
        //设置居中
        setGravity(Gravity.CENTER);
        //设置边距
        int paddingV = ResourcesUtils.getDimensionPixelSize(R.dimen.list_footer_top_bottom_padding);
        int paddingH = ResourcesUtils.getDimensionPixelSize(R.dimen.list_footer_left_right_padding);
        setPadding(paddingH, paddingV, paddingH, paddingV);
        //加载布局
        LayoutInflater.from(getContext()).inflate(R.layout.layout_list_foot_loading, this, true);
        mProgressBar = (ProgressBar) findViewById(R.id.loading_progress);
        mTipsText = (TextView) findViewById(R.id.loading_text);
        //默认隐藏
        showLayout(false);
    }

    /**
     * 设置布局
     *
     * @param show
     */
    public void showLayout(boolean show) {
        ViewGroup.LayoutParams lp = getLayoutParams();
        if (null != lp) {
            if (show) {
                lp.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                setVisibility(View.VISIBLE);
            } else {
                setVisibility(View.GONE);
                lp.height = 0;
            }
            requestLayout();
        }
    }

    /**
     * 正在加载
     */
    public void showLoading() {
        showView(ResourcesUtils.getString(R.string.loading));
        setEnabled(false);
        mProgressBar.setVisibility(VISIBLE);
    }

    /**
     * 加载失败
     */
    public void showError() {
        showView(ResourcesUtils.getString(R.string.load_fail));
        mProgressBar.setVisibility(GONE);
        setEnabled(true);
    }

    /**
     * 加载结束，无更多数据
     */
    public void showEnd() {
        showView(ResourcesUtils.getString(R.string.not_more_data));
        mProgressBar.setVisibility(GONE);
        setEnabled(false);
    }

    /**
     * 展示自定义文本
     *
     * @param string
     */
    public void showView(String string) {
        showLayout(true);
        mTipsText.setText(string);
        setEnabled(true);
        mTipsText.setVisibility(VISIBLE);
    }

    /**
     * 根据状态设置样式
     *
     * @param state
     */
    public void setState(BaseRecyclerAdapter.LoadingState state, String text) {
        this.mState = state;
        if (mState == BaseRecyclerAdapter.LoadingState.loading) {
            showLoading();
        } else if (mState == BaseRecyclerAdapter.LoadingState.loadError) {
            showError();
        } else if (mState == BaseRecyclerAdapter.LoadingState.loadEnd) {
            showEnd();
        } else if (mState == BaseRecyclerAdapter.LoadingState.custom) {
            showView(text);
        } else {
            showLayout(false);
        }
    }

    /**
     * 获取当前的底部加载控件状态
     *
     * @return
     */
    public BaseRecyclerAdapter.LoadingState getState() {
        return mState;
    }
}
