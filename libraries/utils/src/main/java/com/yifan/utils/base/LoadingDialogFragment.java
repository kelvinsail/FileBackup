package com.yifan.utils.base;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.yifan.utils.R;
import com.yifan.utils.utils.Constant;
import com.yifan.utils.utils.ResourcesUtils;

/**
 * Created by yifan on 2016/12/9.
 */

public class LoadingDialogFragment extends BaseDialogFragment {

    public static final String TAG = "LoadingDialogFragment";
    public static final String IS_CANCELABLE = "isCancelable";
    public static final String IS_TOUCHOUTSIDE_CANCELABLE = "isTouchOusideCancelable";

    /**
     * 正在加载布局
     */
    private View mContentView;

    /**
     * 进度条
     */
    private ProgressBar mProgressBar;

    /**
     * 文本控件
     */
    private TextView mTextView;

    public LoadingDialogFragment() {
    }

    public static LoadingDialogFragment newInstance(String msg, boolean isCancelable, boolean isTouchOutsideCancelable) {
        Bundle args = new Bundle();
        LoadingDialogFragment fragment = new LoadingDialogFragment();
        args.putString(Constant.MESSAGE, msg);
        args.putBoolean(IS_CANCELABLE, isCancelable);
        args.putBoolean(IS_TOUCHOUTSIDE_CANCELABLE, isTouchOutsideCancelable);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public View getLayoutView() {
        //加载布局
        LayoutInflater mLayoutInflater = LayoutInflater.from(getActivity());
        Context themeContext = new ContextThemeWrapper(getContext(), R.style.LoadingDialog);
        // 通过context构建新的inflater
        // 再通过应用新主题的inflater加载视图
        mLayoutInflater = mLayoutInflater.cloneInContext(themeContext);
        mContentView = mLayoutInflater.inflate(R.layout.layout_loading_dialog, null);
        mProgressBar = (ProgressBar) mContentView.findViewById(R.id.pb_loading_dialog);
        mTextView = (TextView) mContentView.findViewById(R.id.tv_loading_dialog_message);
        return mContentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mTextView.setText(getArguments().getString(Constant.MESSAGE, ResourcesUtils.getString(R.string.loading)));
        setCancelable(getArguments().getBoolean(IS_CANCELABLE, true));
        if (null != getDialog()) {
            getDialog().setCancelable(getArguments().getBoolean(IS_CANCELABLE, true));
            getDialog().setCanceledOnTouchOutside(getArguments().getBoolean(IS_TOUCHOUTSIDE_CANCELABLE, true));
        }
    }

    /**
     * 设置提示信息
     *
     * @param msg
     */
    public void setMessage(String msg) {
        if (null != mTextView) {
            mTextView.setText(msg);
        }
    }

    @Override
    public void setCancelable(boolean cancelable) {
        super.setCancelable(cancelable);
    }

    /**
     * 设置点击窗外区域是否消除弹窗
     *
     * @param touchOutsideCancelable
     */
    public void setTouchOutsideCancelable(boolean touchOutsideCancelable) {
        if (null != getDialog()) {
            getDialog().setCanceledOnTouchOutside(touchOutsideCancelable);
        }
    }

    @Override
    public boolean isPrintLifeCycle() {
        return true;
    }
}
