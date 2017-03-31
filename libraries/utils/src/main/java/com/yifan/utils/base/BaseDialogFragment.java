package com.yifan.utils.base;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;

import com.yifan.utils.R;
import com.yifan.utils.base.lifecycle.LifeCycleDialogFragment;

/**
 * BaseDialogFragment DialogFragment基类
 *
 * Created by yifan on 2016/9/18.
 */
public abstract class BaseDialogFragment extends LifeCycleDialogFragment {

    /**
     * 根布局
     */
    private View mRootViwe;

    @Override
    public abstract String getTAG();

    @Override
    public void initView() {
    }

    @Override
    public void setListener() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.BaseDialog);
        if (getLayoutResID() > 0) {
            mRootViwe = LayoutInflater.from(getActivity()).inflate(getLayoutResID(), null);
        } else {
            mRootViwe = getLayoutView();
        }
        dialog.getWindow().setContentView(mRootViwe);
        dialog.setCancelable(isDialogCancelable());
        setCancelable(isDialogCancelable());
        dialog.setCanceledOnTouchOutside(isDialogCanceledOnTouchOutside());
        return dialog;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        setListener();
    }

    /**
     * 获取资源布局ID
     *
     * @return 资源布局ID
     */
    public int getLayoutResID() {
        return 0;
    }

    /**
     * 获取布局控件对象
     *
     * @return
     */
    public View getLayoutView() {
        return null;
    }

    /**
     * 窗口是否可以消除
     *
     * @return
     */
    public boolean isDialogCancelable() {
        return true;
    }


    /**
     * 点击窗口外区域是否可以消除
     *
     * @return
     */
    public boolean isDialogCanceledOnTouchOutside() {
        return true;
    }

    /**
     * 获取根布局
     *
     * @return
     */
    public View getRootView() {
        return mRootViwe;
    }
}
