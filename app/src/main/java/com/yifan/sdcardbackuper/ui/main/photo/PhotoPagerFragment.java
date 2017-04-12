package com.yifan.sdcardbackuper.ui.main.photo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.ui.widget.PullToRefreshLayout;
import com.yifan.sdcardbackuper.utils.PhotoDataManager;
import com.yifan.sdcardbackuper.model.photo.PhotoGroupItem;
import com.yifan.sdcardbackuper.task.GetPhotoTask;
import com.yifan.sdcardbackuper.utils.copy.FileCopyManager;
import com.yifan.sdcardbackuper.utils.copy.PhotoCopyManager;
import com.yifan.utils.base.BaseAsyncTask;
import com.yifan.utils.base.BaseFragment;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.utils.utils.ResourcesUtils;

import java.lang.ref.WeakReference;

/**
 * 照片Fragment
 *
 * Created by yifan on 2016/11/15.
 */
public class PhotoPagerFragment extends BaseFragment implements
        BaseRecyclerAdapter.OnItemClickListener, BaseRecyclerAdapter.OnItemCheckedListener {

    private static final String TAG = "PhotoPagerFragment";

    /**
     * 下拉刷新控件
     */
    private PullToRefreshLayout mPullToRefreshLayout;

    /**
     * 列表控件
     */
    private RecyclerView mListView;

    /**
     * 图片数据适配器
     */
    private PhotoGroupAdapter mAdapter;

    /**
     * 获取图片集合 异步任务
     */
    private GetPhotoTask mGetPhotoTask;

    /**
     * 获取图片异步任务 监听器
     */
    private OnGetPhotoGroupListener mGetPhotoGroupListener;

    @Override
    public String getTAG() {
        return TAG;
    }

    public static PhotoPagerFragment newInstance() {
        Bundle args = new Bundle();
        PhotoPagerFragment fragment = new PhotoPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoPagerFragment() {
    }

    @Override
    public String getTitleName() {
        return ResourcesUtils.getString(R.string.photo);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mAdapter.notifyDataSetChanged();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mPullToRefreshLayout = new PullToRefreshLayout(getActivity());
        mListView = (RecyclerView) inflater.inflate(R.layout.fragment_file_list, container, false);
        mPullToRefreshLayout.addView(mListView);
        return mPullToRefreshLayout;
    }

    @Override
    public void initView() {
        super.initView();
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        mListView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        mAdapter = new PhotoGroupAdapter(PhotoDataManager.getInstance().getGroups());
        mListView.setAdapter(mAdapter);
        loadData();
    }

    private void loadData() {
        if (null != mGetPhotoTask) {
            mGetPhotoTask.cancel(true);
            mGetPhotoTask = null;
        }
        if (null == mGetPhotoGroupListener) {
            mGetPhotoGroupListener = new OnGetPhotoGroupListener(new WeakReference<PhotoPagerFragment>(this));
        }
        //清空已选择的文件、照片数据
        FileCopyManager.getInstance().getFileTree().clearAll();
        mGetPhotoTask = new GetPhotoTask();
        mGetPhotoTask.setOnAsyncListener(mGetPhotoGroupListener);
        mGetPhotoTask.asyncExecute();
    }

    @Override
    public void setListener() {
        super.setListener();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemCheckedListener(this);
    }

    @Override
    public void onItemChecked(View view, boolean isChecked, int position) {
        PhotoDataManager.getInstance().getGroups().get(position).setCheckedAll(isChecked);
        for (PhotoGroupItem item : PhotoDataManager.getInstance().getGroups().get(position).getItems()) {
            if (isChecked) {
                PhotoCopyManager.getInstance().addFile(item.path);
            } else {
                PhotoCopyManager.getInstance().deleteFile(item.path);
            }
        }
    }

    @Override
    public void onItemClick(View view, int itemType, int position) {
        getFragmentManager().beginTransaction().hide(this).replace(android.R.id.content,
                PhotoGroupFragment.newInstance(PhotoDataManager.getInstance().getGroups().
                        get(position).getItems()), PhotoGroupFragment.TAG).addToBackStack(PhotoGroupFragment.TAG)
                .commit();
    }

    /**
     * 获取图片集合 异步任务监听器
     */
    private static class OnGetPhotoGroupListener implements BaseAsyncTask.OnAsyncListener {

        private WeakReference<PhotoPagerFragment> mFragment;

        public OnGetPhotoGroupListener(WeakReference<PhotoPagerFragment> mFragment) {
            this.mFragment = mFragment;
        }

        @Override
        public void onAsyncSuccess(Object data) {
            if (null != mFragment.get()) {
                mFragment.get().mAdapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onAsyncFail() {

        }

        @Override
        public void onAsyncCancelled() {

        }

        @Override
        public void onAsyncStart() {
            if (null != mFragment.get()) {
                mFragment.get().mPullToRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        public void onAsyncCompleted() {
            if (null != mFragment.get()) {
                mFragment.get().mPullToRefreshLayout.setRefreshing(false);
            }
        }
    }

    @Override
    public boolean isPrintLifeCycle() {
        return true;
    }
}
