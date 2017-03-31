package com.yifan.utils.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
 * TitleBarActivity 封装了TitleBar的Acitity，主要负责提供了封装的TitleBar及与其相关的界面处理，其他功能函数应放在{@link BaseActivity}中实现
 *
 * Created by yifan on 2016/7/18.
 */
public abstract class TitleBarActivity extends BaseActivity implements TitleImpl {

    /**
     * 包含TitleBar的根布局
     */
    private ViewGroup mRootView;

    /**
     * 加载视图的布局
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
     * 主布局
     */
    private View mContentView;

    /**
     * TitleBar控件
     */
    private TitleBar mTitleBar;

    /**
     * 正在加载布局
     */
    private LoadingView mLoadingView;

//    /**
//     * ToolBar是否联动
//     */
//    private boolean isToolBarLinkage;

    /**
     * 主界面抽屉引用
     */
    WeakReference<DrawerLayout> mDrawerLayout;

    /**
     * 主题资源ID
     */
    private int mStyleID;

    /**
     * 状态栏是否透明
     */
    private boolean isToolBarTranslaction;

    /**
     * 布局加载器
     */
    private LayoutInflater mLayoutInflater;

    /**
     * 加载弹窗
     */
    private LoadingDialogFragment mLoadingDialogFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(View v, ViewGroup.LayoutParams params) {
        super.setContentView(initContentView(v, 0, 0, false), params);
    }

    @Override
    public void setContentView(int id) {
        setContentView(id, 0, false);
    }

    @Override
    public void setContentView(View v) {
        setContentView(v, 0, false);
    }

    /**
     * 加载布局
     *
     * @param layoutID         布局ID
     * @param styleID          主题id
     * @param isToolBarLinkage ToolBar是否联动
     */
    public void setContentView(@LayoutRes int layoutID, int styleID, boolean isToolBarLinkage) {
        super.setContentView(initContentView(null, layoutID, styleID, isToolBarLinkage));
    }

    /**
     * 加载布局
     *
     * @param v                布局
     * @param styleID          主题id
     * @param isToolBarLinkage 导航栏是否联动
     */
    public void setContentView(View v, int styleID, boolean isToolBarLinkage) {
        super.setContentView(initContentView(v, 0, styleID, isToolBarLinkage));
    }

    /**
     * 加载布局
     *
     * @param drawerLayoutID   DrawerLayout布局
     * @param contentID        主内容布局
     * @param styleID          主题ID
     * @param isToolBarLinkage 导航栏是否联动
     */
    public void setContentViewWithDrawer(@LayoutRes int drawerLayoutID, @LayoutRes int contentID, int styleID, boolean isToolBarLinkage) {
        View content = initContentView(null, contentID, styleID, isToolBarLinkage);
        DrawerLayout drawerLayout = (DrawerLayout) mLayoutInflater.inflate(drawerLayoutID, null, false);
        drawerLayout.addView(content, 0);
        super.setContentView(drawerLayout);
    }

    /**
     * 初始化布局
     *
     * @param v
     * @param styleID
     * @param isToolBarLinkage
     * @return
     */
    private View initContentView(View v, @LayoutRes int layoutID, int styleID, boolean isToolBarLinkage) {
        //初始化布局加载适配器
        mLayoutInflater = getLayoutInflater();
        //判断当前Fragment是否需要改变主题,即改变Titlebar颜色主题
        if (styleID > 0) {
            mStyleID = styleID;
            // 构建新主题的Context上下文环境对象
            Context themeContext = new ContextThemeWrapper(getApplicationContext(), styleID);
            // 通过context构建新的inflater
            // 再通过应用新主题的inflater加载视图
            mLayoutInflater = mLayoutInflater.cloneInContext(themeContext);
        }
        //判断是否联动TitleBar
        if (isToolBarLinkage) {
            if (hasNavigationView()) {
                mRootView = (DrawerLayout) mLayoutInflater.inflate(R.layout.activity_navigation, null);
                mRootView.addView(mLayoutInflater.inflate(R.layout.activity_titlebar_linkage, null), 0);
            } else {
                mRootView = (ViewGroup) mLayoutInflater.inflate(R.layout.activity_titlebar_linkage, null);
            }
        } else {//非联动
            if (hasNavigationView()) {
                mRootView = (DrawerLayout) mLayoutInflater.inflate(R.layout.activity_navigation, null);
                mRootView.addView(mLayoutInflater.inflate(R.layout.activity_titlebar, null), 0);
            } else {
                mRootView = (ViewGroup) mLayoutInflater.inflate(R.layout.activity_titlebar, null);
            }
        }
        //初始化TitleBar
        mTitleBar = (TitleBar) mRootView.findViewById(R.id.titlebar_toolbar_activity);
        setSupportActionBar(mTitleBar);
        if (isTitleBarBackEnable()) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        mAppBarLayout = (AppBarLayout) mRootView.findViewById(R.id.abl_toolbar_activity);
        mExpandStub = (ViewStubCompat) mRootView.findViewById(R.id.vsc_toolbar_activity);
        //主布局容器
        mContentLayout = (ViewStubCompat) mRootView.findViewById(R.id.vsc_toolbar_activity_content);
        //初始化并添加加载提示布局
        mLoadingView = (LoadingView) mRootView.findViewById(R.id.loadingview_toolbar_activity);
        //矫正TitleBar联动时，LoadingView的居中问题
        if (isToolBarLinkage) {
            mLoadingView.offsetMarginTop();
        }
        //加载主布局
        //判断是否映射解析布局文件，还是直接添加布局
        if (layoutID > 0) {
            //解析主布局
            mContentLayout.setLayoutInflater(mLayoutInflater);
            mContentLayout.setLayoutResource(layoutID);
            mContentView = mContentLayout.inflate();
        } else if (v != null) {
            //直接添加布局
            mContentView = v;
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
        //5.0以下设置沉浸式状态栏
        View statusView = mRootView.findViewById(R.id.view_starus_bar_replace);
        if (null != statusView && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明状态栏
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            //透明导航栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
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
     * 获取TitleBar对象
     *
     * @return
     */
    @Override
    public TitleBar getSupportTitleBar() {
        return mTitleBar;
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
     * 获取AppBar高度
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
     * 获取包含抽屉的布局
     *
     * @return
     */
    public DrawerLayout getDrawerLayout() {
        return (DrawerLayout) findViewById(R.id.drawer_layout);
    }

    /**
     * 获取抽屉控件
     *
     * @return
     */
    public NavigationView getNavigationView() {
        return (NavigationView) findViewById(R.id.nav_view);
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
    public synchronized void createLoadingdialog() {
        createLoadingdialog(getString(R.string.loading));
    }

    @Override
    public synchronized void createLoadingdialog(String message) {
        createLoadingdialog(message, true, true);
    }

    @Override
    public synchronized void createLoadingdialog(String message, boolean canelable, boolean touchOutsideCancelable) {
        createLoadingdialog(message, canelable, touchOutsideCancelable, true);
    }

    @Override
    public synchronized void createLoadingdialog(String message, boolean canelable, boolean touchOutsideCancelable, boolean isFullScreen) {
        Log.i(getTAG(), "createLoadingdialog: " + message);
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
                mLoadingDialogFragment.show(getSupportFragmentManager(), LoadingDialogFragment.TAG);
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
    public synchronized void dissmissLoadingDialog() {
        Log.i(getTAG(), "dissmissLoadingDialog: ");
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
     * 是否具有抽屉
     *
     * @return
     */
    public boolean hasNavigationView() {
        return false;
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
                    this, mDrawerLayout.get(),
                    getSupportTitleBar(), R.string.navigation_drawer_open,
                    R.string.navigation_drawer_close);
            mDrawerLayout.get().addDrawerListener(toggle);
            toggle.syncState();
        }
    }

    /**
     * 导航栏返回键是否可用
     */
    public boolean isTitleBarBackEnable() {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (isTitleBarBackEnable() && item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
