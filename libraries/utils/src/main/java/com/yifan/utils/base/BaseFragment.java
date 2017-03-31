package com.yifan.utils.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.yifan.utils.base.impl.LoadImpl;
import com.yifan.utils.base.lifecycle.LifeCycleFragment;

/**
 * Fragmnet基类
 * <p>
 * Created by yifan on 2016/7/16.
 */
public abstract class BaseFragment extends LifeCycleFragment implements LoadImpl {

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        setListener();
    }

    @Override
    public void initView() {

    }

    @Override
    public void setListener() {

    }

    @Override
    public void createLoadingdialog() {

    }

    @Override
    public void createLoadingdialog(String message) {
    }

    @Override
    public void createLoadingdialog(String message, boolean canelable, boolean touchOutsideCancelable) {

    }

    @Override
    public void createLoadingdialog(String message, boolean canelable, boolean touchOutsideCancelable, boolean isFullScreen) {

    }

    @Override
    public void dissmissLoadingDialog() {

    }

    /**
     * 获取标题的名字
     *
     * @return
     */
    public String getTitleName() {
        return null;
    }
}
