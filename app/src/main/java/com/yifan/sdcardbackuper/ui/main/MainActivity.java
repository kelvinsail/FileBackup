package com.yifan.sdcardbackuper.ui.main;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.provider.DocumentFile;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.base.OnFunctionBarChangedListener;
import com.yifan.sdcardbackuper.model.CopyProgress;
import com.yifan.sdcardbackuper.task.BackupChannelTask;
import com.yifan.sdcardbackuper.task.backup.BackupTask;
import com.yifan.sdcardbackuper.task.backup.ThreadManager;
import com.yifan.sdcardbackuper.ui.main.file.FileListPagerFragment;
import com.yifan.sdcardbackuper.ui.main.photo.PhotoPagerFragment;
import com.yifan.sdcardbackuper.ui.main.setting.SettingFragment;
import com.yifan.sdcardbackuper.ui.widget.TSManager;
import com.yifan.sdcardbackuper.utils.Constants;
import com.yifan.sdcardbackuper.utils.MountUtils;
import com.yifan.sdcardbackuper.utils.UnSupportException;
import com.yifan.sdcardbackuper.utils.copy.FileCopyManager;
import com.yifan.sdcardbackuper.utils.copy.PhotoCopyManager;
import com.yifan.utils.base.BaseAsyncTask;
import com.yifan.utils.base.BaseFragment;
import com.yifan.utils.base.TitleBarActivity;
import com.yifan.utils.utils.ResourcesUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends TitleBarActivity implements OnFunctionBarChangedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_PERMISSION = 0x001;
    private static final int REQUEST_CODE_DOCUMENT_PROVIDER = 0x002;

    /**
     * 下拉刷新布局
     */
    private TabLayout mTabLayout;

    /**
     * 路径显示文本
     */
    private ViewPager mViewPager;

    /**
     * 底部工具栏布局
     */
    private LinearLayout mFunctionLayout;

    /**
     * {@link Snackbar} 弹出的位置布局
     */
    private CoordinatorLayout mSnackBarLayout;

    /**
     * 复制按钮
     */
    private Button mCopyBtn;

    /**
     * Fragment页卡数组
     */
    private List<BaseFragment> mFragments;

    /**
     * 复制文件 异步任务
     */
    private BackupChannelTask mCopyTask;

    /**
     * 复制 异步任务监听
     */
    private OnCopyListener mCopyListener;

    /**
     * 所选的文件保存挂载点路径
     */
    private String mTargetPath;

    /**
     * 挂载点信息
     */
    private List<MountUtils.MountPoint> mPoints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPoints = new ArrayList<>();
        mFragments = new ArrayList<>();
        mFragments.add(PhotoPagerFragment.newInstance());
        mFragments.add(FileListPagerFragment.newInstance(Environment.getExternalStorageDirectory().getAbsolutePath())
                .setOnFunctionBarChangedListener(this));

        setContentView(R.layout.activity_main, 0, false);

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void initView() {
        super.initView();
        getBarExpandStub().setLayoutResource(R.layout.layout_tab);
        mTabLayout = (TabLayout) getBarExpandStub().inflate().findViewById(R.id.layout_tab_main);
        mViewPager = (ViewPager) findViewById(R.id.vp_main);
        mFunctionLayout = (LinearLayout) findViewById(R.id.layout_main_function_bar);
        mSnackBarLayout = (CoordinatorLayout) findViewById(R.id.layout_main_snackbar);
        mCopyBtn = (Button) findViewById(R.id.btn_main_copy);

        LocalPagerAdapter adapter = new LocalPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(adapter);
        mTabLayout.setupWithViewPager(mViewPager);
        //请求权限
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String[] permissions = new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE_PERMISSION);
                    break;
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItemCompat.setShowAsAction(menu.findItem(R.id.action_settings), MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                getSupportFragmentManager().beginTransaction().replace(android.R.id.content, SettingFragment.newInstance(), SettingFragment.TAG).addToBackStack(SettingFragment.TAG).commit();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_PERMISSION) {
            Log.i(TAG, "onRequestPermissionsResult: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                Log.i(TAG, "onRequestPermissionsResult: " + permissions[i] + "," + grantResults[i]);
            }
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        mCopyBtn.setOnClickListener(this);
    }

    @Override
    public String getTAG() {
        return TAG;
    }

    @Override
    public void onBarStateChanged(boolean isShowBar) {
        //        if (isShowBar) {
        //            ObjectAnimator alpha = ObjectAnimator.ofFloat(mFunctionLayout, "alpha", 0.0f, 1.0f);
        //            AnimatorSet set = new AnimatorSet();
        //            set.setDuration(150);//设置播放时间
        //            set.setInterpolator(new LinearInterpolator());//设置播放模式，这里是平常模式
        //            set.playTogether(alpha);//设置一起播放
        //            set.start();
        //        } else {
        //            ObjectAnimator alpha = ObjectAnimator.ofFloat(mFunctionLayout, "alpha", 1.0f, 0.0f);
        //            AnimatorSet set = new AnimatorSet();
        //            set.setDuration(150);//设置播放时间
        //            set.setInterpolator(new LinearInterpolator());//设置播放模式，这里是平常模式
        //            set.playTogether(alpha);//设置一起播放
        //            set.start();
        //        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_main_copy:
                //判断是否已选择文件或照片
                long nodeCount;
                if (mViewPager.getCurrentItem() == 0) {
                    nodeCount = PhotoCopyManager.getInstance().getFileTree().getAllNodesCount();
                } else {
                    nodeCount = FileCopyManager.getInstance().getFileTree().getAllNodesCount();
                }
                if (nodeCount <= 0) {
                    TSManager.showSnackTips(mSnackBarLayout, (mViewPager.getCurrentItem() == 0 ? R.string.tips_please_select_photoes :
                            R.string.tips_please_select_files), Snackbar.LENGTH_SHORT);
                    return;
                }
                String[] pointNames = null;
                mPoints.clear();
                try {
                    //获取除了内置储存器之外的挂载点列表
                    mPoints.addAll(MountUtils.getInstance().getAllMountPoints(true, true));
                    pointNames = new String[mPoints.size()];
                    for (int i = 0; i < mPoints.size(); i++) {
                        MountUtils.MountPoint point = mPoints.get(i);
                        StringBuilder _str = new StringBuilder();
                        if (null != point.getLabel()) {
                            _str.append(point.getLabel());
                        } else {
                            _str.append(point.getFile().getAbsolutePath());
                        }
                        pointNames[i] = _str.append("(").append(point.getAvailableSize()).append("/").append(point.getTotalSize()).append(")").toString();
                    }
                } catch (UnSupportException e) {
                    e.printStackTrace();
                }

                //判断是否已有其他挂载点
                if (pointNames.length > 0) {
//                    File file = new File(mPoints.get(0).getFile().getAbsolutePath());
//                    if (file.canWrite()) {
                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        builder.setSingleChoiceItems(pointNames, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mTargetPath = mPoints.get(which).getFile().getAbsolutePath();
                            }
                        });
                        mTargetPath = mPoints.get(0).getFile().getAbsolutePath();
                        builder.setNegativeButton(R.string.cancel, null);
                        builder.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (null != mTargetPath) {
//                                    if (mPoints.size() > 0) {
//                                        if (null != mCopyTask) {
//                                            mCopyTask.cancel(true);
//                                            mCopyTask = null;
//                                        }
//                                        if (null == mCopyListener) {
//                                            mCopyListener = new OnCopyListener(new WeakReference<MainActivity>(MainActivity.this));
//                                        }
//                                        mCopyTask = new BackupChannelTask();
//                                        mCopyTask.setOnAsyncListener(mCopyListener);
//                                        mCopyTask.asyncExecute(mTargetPath,
//                                                mViewPager.getCurrentItem() == 0 ?
//                                                        BackupTask.BACKUP_TYPE_PHOTO :
//                                                        BackupTask.BACKUP_TYPE_FILE);
//                                    }
                                    BackupTask task = new BackupTask(mTargetPath,mViewPager.getCurrentItem() == 0 ?
                                                        BackupTask.BACKUP_TYPE_PHOTO :
                                                        BackupTask.BACKUP_TYPE_FILE,null);
                                    ThreadManager.getDefault().excuteAsync(task);
                                }
                            }
                        });
                        builder.create().show();
                    } else {
                        Toast.makeText(this, R.string.storage_permission_has_limited, Toast.LENGTH_SHORT).show();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                            startActivityForResult(
                                    Intent.createChooser(intent, "DocumentProvider"), REQUEST_CODE_DOCUMENT_PROVIDER);
                        }
                    }
                } else {
                    Toast.makeText(MainActivity.this, R.string.tips_no_outside_storage, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 页卡数据适配器
     */
    private class LocalPagerAdapter extends FragmentPagerAdapter {

        public LocalPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragments.get(position).getTitleName();
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    /**
     * 文件复制 异步任务监听
     */
    public static class OnCopyListener implements BaseAsyncTask.OnAsyncListener {

        private WeakReference<MainActivity> mActivity;
        private long startTime;

        public OnCopyListener(WeakReference<MainActivity> reference) {
            this.mActivity = reference;
        }

        @Override
        public void onAsyncSuccess(Object data) {
            if (null != mActivity.get() && null != data && data instanceof CopyProgress) {
                if (((CopyProgress) data).completedCount == ((CopyProgress) data).totalFileCount) {
                    Toast.makeText(mActivity.get(), R.string.end_copy, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mActivity.get(), ResourcesUtils.getString(R.string.end_copy_but_some_fail,
                            String.valueOf(((CopyProgress) data).totalFileCount - ((CopyProgress) data).completedCount))
                            , Toast.LENGTH_SHORT).show();
                }

            }
        }

        @Override
        public void onAsyncFail() {
            if (null != mActivity.get()) {
                Toast.makeText(mActivity.get(), R.string.end_copy_but_fail, Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onAsyncCancelled() {
        }

        @Override
        public void onAsyncStart() {
            if (null != mActivity.get()) {
                startTime = System.currentTimeMillis();
                mActivity.get().createLoadingdialog(ResourcesUtils.getString(R.string.start_to_statisitcs_file_count), false, false);
            }
        }

        @Override
        public void onAsyncCompleted() {
            if (null != mActivity.get()) {
                mActivity.get().dissmissLoadingDialog();
            }
        }

        /**
         * 复制进度
         *
         * @param copyProgresses
         */
        public void onUpdateProgress(CopyProgress... copyProgresses) {
            Log.i(TAG, "onUpdateProgress: " + copyProgresses[0].completedCount + " , " + copyProgresses[0].totalFileCount);
            if (null != mActivity.get()) {
                StringBuilder builder = new StringBuilder(ResourcesUtils.getString(R.string.start_to_copy_file,
                        copyProgresses[0].completedCount - copyProgresses[0].skipedCount, copyProgresses[0].totalFileCount));
                if (copyProgresses[0].skipedCount > 0) {
                    builder.append(ResourcesUtils.getString(R.string.start_to_copy_file_skip_count, copyProgresses[0].skipedCount));
                }
                mActivity.get().createLoadingdialog(builder.toString(), false, false);
            }
        }
    }

    @Override
    public boolean isTitleBarBackEnable() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_DOCUMENT_PROVIDER) {
            if (resultCode == RESULT_OK) {
                //获取返回的文件权限Uri
                Uri treeUri = data.getData();
                DocumentFile pickedDir = DocumentFile.fromTreeUri(this, treeUri);
                mTargetPath = pickedDir.getUri().getPath();
                //判断是否选择了内置储存器
                if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(Constants.KEY_PREFERENCES_COPY_TO_STORAGE,
                        Constants.VALUE_PREFERENCES_COPY_TO_STORAGE) && null != mTargetPath
                        && mTargetPath.equals("/tree/primary:/document/primary:")) {
                    Toast.makeText(this, R.string.unable_copy_to_storage, Toast.LENGTH_SHORT).show();
                    return;
                }
//                //开启异步任务 备份文件
//                if (null != mCopyTask) {
//                    mCopyTask.cancel(true);
//                    mCopyTask = null;
//                }
//                if (null == mCopyListener) {
//                    mCopyListener = new OnCopyListener(new WeakReference<MainActivity>(MainActivity.this));
//                }
//                mCopyTask = new BackupChannelTask();
//                mCopyTask.setOnAsyncListener(mCopyListener);
//                mCopyTask.asyncExecute(mTargetPath,
//                        mViewPager.getCurrentItem() == 0 ?
//                                BackupTask.BACKUP_TYPE_PHOTO :
//                                BackupTask.BACKUP_TYPE_FILE, pickedDir);
                BackupTask task = new BackupTask(mTargetPath,mViewPager.getCurrentItem() == 0 ?
                        BackupTask.BACKUP_TYPE_PHOTO :
                        BackupTask.BACKUP_TYPE_FILE,pickedDir);
                ThreadManager.getDefault().excuteAsync(task);
            }
        } else {//获取失败
            Toast.makeText(this, R.string.cancel_copy, Toast.LENGTH_SHORT).show();
        }
    }

}
