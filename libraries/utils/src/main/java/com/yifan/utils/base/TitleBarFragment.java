package com.yifan.utils.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ViewStubCompat;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.yifan.utils.R;
import com.yifan.utils.base.impl.TitleImpl;
import com.yifan.utils.utils.WidgetUtils;
import com.yifan.utils.widget.LoadingView;
import com.yifan.utils.widget.TitleBar;

import java.lang.ref.WeakReference;


/**
 * TitleBarFragment 封装了TitleBar的Fragment，主要负责提供了封装的TitleBar及与其相关的界面处理，其他功能函数应放在{@link BaseFragment}中实现
 *
 * Created by yifan on 2016/7/18.
 */
public abstract class TitleBarFragment extends BaseFragment implements TitleImpl {

    /**
     * 导航栏
     */
    private TitleBar mTitleBar;

    /**
     * 正在加载布局
     */
    private LoadingView mLoadingView;

    /**
     * 根布局
     */
    private ViewGroup mRootView;

    /**
     * 视图根布局
     */
    private ViewStubCompat mContentLayout;

    /**
     * 拓展布局
     */
    private ViewStubCompat mExpandStub;

    /**
     * ToolBar联动布局
     */
    private AppBarLayout mAppBarLayout;

    /**
     * 主布局ID
     */
    private int mContentLayoutID;

    /**
     * 主题资源ID
     */
    private int mStyleID;

    /**
     * 主布局
     */
    private View mContentView;

    /**
     * ToolBar是否联动
     */
    private boolean isToolBarLinkage;

    /**
     * 状态栏是否透明
     */
    private boolean isToolBarTranslaction;

    /**
     * 主界面抽屉引用
     */
    WeakReference<DrawerLayout> mDrawerLayout;

    /**
     * 加载弹窗
     */
    private LoadingDialogFragment mLoadingDialogFragment;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //判断当前Fragment是否需要改变主题,即改变Titlebar颜色主题
        if (mStyleID > 0) {
            // 构建新主题的Context上下文环境对象
            Context themeContext = new ContextThemeWrapper(getActivity()
                    .getApplicationContext(), mStyleID);
            // 通过context构建新的inflater
            // 再通过应用新主题的inflater加载视图
            inflater = getActivity().getLayoutInflater().cloneInContext(themeContext);
        }
        //判断是否加载联动布局
        if (isToolBarLinkage) {//联动
            mRootView = (ViewGroup) inflater.inflate(R.layout.activity_titlebar_linkage, container, false);
        } else {//非联动
            mRootView = (ViewGroup) inflater.inflate(R.layout.activity_titlebar, container, false);
        }
        //初始化TitleBar控件
        mTitleBar = (TitleBar) mRootView.findViewById(R.id.titlebar_toolbar_activity);
        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).setSupportActionBar(mTitleBar);
        if (isTitleBarBackEnable()) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mAppBarLayout = (AppBarLayout) mRootView.findViewById(R.id.abl_toolbar_activity);
        mExpandStub = (ViewStubCompat) mRootView.findViewById(R.id.vsc_toolbar_activity);
        //初始化主布局
        mContentLayout = (ViewStubCompat) mRootView.findViewById(R.id.vsc_toolbar_activity_content);
        //初始化并添加加载提示布局
        mLoadingView = (LoadingView) mRootView.findViewById(R.id.loadingview_toolbar_activity);
        //矫正TitleBar联动时，LoadingView的居中问题
        if (isToolBarLinkage) {
            mLoadingView.offsetMarginTop();
        }
        //加载主布局
        //判断是否映射解析布局文件，还是直接添加布局
        if (mContentLayoutID > 0) {
            //解析布局
            mContentLayout.setLayoutInflater(inflater);
            mContentLayout.setLayoutResource(mContentLayoutID);
            mContentView = mContentLayout.inflate();
        } else if (mContentView != null && null == mContentView.getParent()) {
            //直接添加布局
            if (isToolBarLinkage) {
                mRootView.addView(mContentView, 0);
            } else {
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.addRule(RelativeLayout.BELOW, mAppBarLayout.getId());
                mRootView.addView(mContentView, 2, layoutParams);
            }
        }
        if (isToolBarTranslaction && null != mContentView.getLayoutParams() &&
                mContentView.getLayoutParams() instanceof RelativeLayout.LayoutParams) {
            RelativeLayout.LayoutParams layoutParams =
                    (RelativeLayout.LayoutParams) mContentView.getLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, -1);
            layoutParams.topMargin = WidgetUtils.getStatusBarHeight();
            mTitleBar.setBackgroundResource(R.color.background_black_alpha_light);
            mAppBarLayout.setBackgroundResource(R.color.background_black_alpha_light);
            mRootView.removeView(mContentView);
            mRootView.addView(mContentView, 0, layoutParams);
        }
        //设置导航栏与抽屉的联动
        setTitleBarToggle();
        //5.0以下设置沉浸式状态栏
        View statusView = mRootView.findViewById(R.id.view_starus_bar_replace);
        if (null != statusView && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //透明导航栏
//            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            //设置替代状态栏背景的布局
            statusView.setLayoutParams(new AppBarLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, WidgetUtils.getStatusBarHeight()));
            statusView.setVisibility(View.VISIBLE);
        } else {
            if (null != statusView) {
                statusView.setMinimumHeight(0);
                statusView.setVisibility(View.GONE);
            }
        }
        return mRootView;
    }

    /**
     * 设置布局(在onCreate中调用)
     *
     * @param resID 主布局ID
     */
    public void setContentView(int resID) {
        setContentView(resID, 0, false);
    }


    /**
     * 设置布局(在onCreate中调用)
     *
     * @param resID            主布局ID
     * @param styleID          主题资源ID
     * @param isToolBarLinkage ToolBar是否联动
     */
    public void setContentView(int resID, int styleID, boolean isToolBarLinkage) {
        this.mContentLayoutID = resID;
        this.mStyleID = styleID;
        this.isToolBarLinkage = isToolBarLinkage;
    }

    /**
     * 设置布局(在onCreate中调用)
     *
     * @param v 主布局
     */
    public void setContentView(View v) {
        setContentView(v, 0, false);
    }

    /**
     * 设置布局(在onCreate中调用)
     *
     * @param v                主布局
     * @param styleID          主题资源ID
     * @param isToolBarLinkage ToolBar是否联动
     */
    public void setContentView(View v, int styleID, boolean isToolBarLinkage) {
        if (v != null) {
            this.mContentView = v;
            this.mStyleID = styleID;
            this.isToolBarLinkage = isToolBarLinkage;
        }
    }

    /**
     * 获取TitleBar对象
     *
     * @return
     */
    @Override
    public TitleBar getSupportTitleBar() {
        return mTitleBar;
    }

    /**
     * 设置主界面抽屉
     *
     * @param drawerLayout
     */
    @Override
    public void setDrawerLayout(WeakReference<DrawerLayout> drawerLayout) {
        this.mDrawerLayout = drawerLayout;
    }

    /**
     * 设置导航栏与抽屉的联动
     */
    @Override
    public void setTitleBarToggle() {
        if (null != getSupportTitleBar() && null != mDrawerLayout) {
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                    this.getActivity(), mDrawerLayout.get(),
                    getSupportTitleBar(), R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            mDrawerLayout.get().addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    /**
     * 获取拓展布局ViewStubCompat，可能为空，非联动布局下为空
     *
     * @return @nullable ViewStubCompat
     */
    @Override
    public ViewStubCompat getBarExpandStub() {
        return mExpandStub;
    }

    /**
     * 获取
     *
     * @return
     */
    @Override
    public int getAppBarHeight() {
        if (mAppBarLayout != null) {
            return mAppBarLayout.getHeight();
        } else if (mTitleBar != null) {
            return mTitleBar.getHeight();
        }
        return 0;
    }

    /**
     * 获取AppBarLayout ,未联动情况下获取为空
     *
     * @return
     */
    @Override
    public AppBarLayout getAppBarLayout() {
        return mAppBarLayout;
    }

    @Override
    public void setToolBarTranslaction(boolean toolBarTranslaction) {
        isToolBarTranslaction = toolBarTranslaction;
    }

    /**
     * 获取根布局
     *
     * @return
     */
    @Override
    public View getContentView() {
        return mRootView;
    }

    @Override
    public void createLoadingdialog() {
        createLoadingdialog(getString(R.string.loading));
    }

    @Override
    public void createLoadingdialog(String message) {
        createLoadingdialog(message, true, true);
    }

    @Override
    public void createLoadingdialog(String message, boolean canelable, boolean touchOutsideCancelable) {
        createLoadingdialog(message, canelable, touchOutsideCancelable, true);
    }

    @Override
    public void createLoadingdialog(String message, boolean canelable, boolean touchOutsideCancelable, boolean isFullScreen) {
        if (!isActived()) {
            return;
        }
        if (isFullScreen) {
            if (null != mLoadingDialogFragment) {
                Log.i(getTAG(), "createLoadingdialog: " + mLoadingDialogFragment);
                mLoadingDialogFragment.setMessage(message);
                mLoadingDialogFragment.setCancelable(canelable);
                mLoadingDialogFragment.setTouchOutsideCancelable(touchOutsideCancelable);
            } else {
                mLoadingDialogFragment = LoadingDialogFragment.newInstance(message, canelable, touchOutsideCancelable);
                mLoadingDialogFragment.show(getFragmentManager(), LoadingDialogFragment.TAG);
            }
        } else {
            if (mLoadingView != null) {
                mLoadingView.setCancelable(canelable);
                mLoadingView.setCanceledOnTouchOutside(touchOutsideCancelable);
                mLoadingView.setMessage(message);
                mLoadingView.show();
            }
        }
    }

    @Override
    public void dissmissLoadingDialog() {
        if (!isActived()) {
            return;
        }
        if (mLoadingView != null && mLoadingView.isShowing()) {
            mContentView.setVisibility(View.VISIBLE);
            mLoadingView.hide();
        }
        if (null != mLoadingDialogFragment) {
            mLoadingDialogFragment.dismiss();
            mLoadingDialogFragment = null;
        }
    }

    @Override
    public abstract String getTAG();

    /**
     * 导航栏返回键是否可用
     */
    public boolean isTitleBarBackEnable() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isTitleBarBackEnable() && item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
