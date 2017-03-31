package com.yifan.utils.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yifan.utils.R;
import com.yifan.utils.utils.ResourcesUtils;

/**
 * 加载Loading界面
 *
 * Created by yifan on 2016/7/18.
 */
public class LoadingView extends LinearLayout implements View.OnClickListener {

    public LoadingView(Context context) {
        this(context, null);
    }

    public LoadingView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    /**
     * 主布局
     */
    View mContentView;

    /**
     * 圆形进度条
     */
    ProgressBar mProgressBar;
    /**
     * 提示文本控件
     */
    TextView mTvMessage;

    /**
     * 提示语
     */
    String mMessage;

    /**
     * 是否可消除
     */
    private boolean isCancelable = true;
    /**
     * 点击布局外区域是否可消除
     */
    private boolean isCanceledOnTouchOutside = true;

    /**
     * 取消加载布局回调接口
     */
    private OnCancelListener mOnCancelListener;

    /**
     * 初始化UI
     */
    private void initUI() {
        //加载布局
        LayoutInflater mLayoutInflater = LayoutInflater.from(getContext());
        Context themeContext = new ContextThemeWrapper(getContext(), R.style.LoadingDialog);
        // 通过context构建新的inflater
        // 再通过应用新主题的inflater加载视图
        mLayoutInflater = mLayoutInflater.cloneInContext(themeContext);
        mContentView = mLayoutInflater.inflate(R.layout.layout_loading_dialog, this);
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.pb_loading_dialog);
        mTvMessage = (TextView) mContentView.findViewById(R.id.tv_loading_dialog_message);
        setMessage(null);

        //设置背景色,居中
        setBackgroundResource(R.color.background_black_alpha_light);
        setGravity(Gravity.CENTER);
        //设置隐藏,可获取焦点点击事件
        setVisibility(View.GONE);
        setFocusable(true);
        setClickable(true);
        setEnabled(true);
        //设置点击事件
        setOnClickListener(this);
    }

    /**
     * 设置文本
     *
     * @param text
     */
    public void setMessage(String text) {
        if (text != null && !TextUtils.isEmpty(text)) {
            this.mMessage = text;
        } else {
            this.mMessage = ResourcesUtils.getString(
                    R.string.loading);
        }
        this.mTvMessage.setText(mMessage);
    }

    /**
     * 显示布局
     */
    public void show() {
        startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
        this.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏布局
     */
    public void hide() {
        startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_out));
        this.setVisibility(View.GONE);
    }

    /**
     * 矫正顶部ToolBar的高度差
     */
    public void offsetMarginTop() {
        TypedArray array = getContext().getTheme().obtainStyledAttributes(new int[]{android.R.attr.actionBarSize});
        int heightOffset = array.getDimensionPixelSize(0, 0);
        array.recycle();
        setPadding(0, heightOffset, 0, 0);
    }

    /**
     * 是否正在展示
     *
     * @return
     */
    public boolean isShowing() {
        return getVisibility() == View.VISIBLE;
    }

    /**
     * 设置是否可消除
     *
     * @param flag
     */
    public void setCancelable(boolean flag) {
        this.isCancelable = flag;
    }

    /**
     * 设置点击布局外区域是否可消除
     *
     * @param cancel
     */
    public void setCanceledOnTouchOutside(boolean cancel) {
        this.isCanceledOnTouchOutside = cancel;
    }

    @Override
    public void onClick(View v) {
        if (isShowing() && isCanceledOnTouchOutside && isCancelable) {
            hide();
            if (mOnCancelListener != null) {
                mOnCancelListener.onCancel(this);
            }
        }
    }

    /**
     * 取消加载布局回调接口
     */
    public interface OnCancelListener {
        void onCancel(View v);
    }

    /**
     * 是否可消除
     *
     * @return
     */
    public boolean isCancelable() {
        return isCancelable;
    }

    /**
     * 点击布局外区域是否可消除
     *
     * @return
     */
    public boolean isCanceledOnTouchOutside() {
        return isCanceledOnTouchOutside;
    }

    /**
     * 取消加载布局回调接口
     *
     * @return
     */
    public OnCancelListener getOnCancelListener() {
        return mOnCancelListener;
    }

    /**
     * 设置取消加载布局回调接口
     *
     * @param mOnCancelListener
     */
    public void setOnCancelListener(OnCancelListener mOnCancelListener) {
        this.mOnCancelListener = mOnCancelListener;
    }
}
