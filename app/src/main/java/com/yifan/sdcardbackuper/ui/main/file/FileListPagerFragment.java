package com.yifan.sdcardbackuper.ui.main.file;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.base.OnFunctionBarChangedListener;
import com.yifan.sdcardbackuper.model.FileItem;
import com.yifan.sdcardbackuper.task.GetFileListTask;
import com.yifan.sdcardbackuper.ui.main.file.impl.FileCheckedImpl;
import com.yifan.sdcardbackuper.ui.main.file.impl.OnFileCheckedListener;
import com.yifan.sdcardbackuper.ui.main.info.InfoFragment;
import com.yifan.sdcardbackuper.ui.widget.PullToRefreshLayout;
import com.yifan.sdcardbackuper.utils.Constants;
import com.yifan.utils.base.BaseFragment;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.utils.utils.ResourcesUtils;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件列表Fragment
 *
 * Created by yifan on 2016/11/15.
 */
public class FileListPagerFragment extends BaseFragment implements BaseRecyclerAdapter.OnItemClickListener,
        BaseRecyclerAdapter.OnItemLongClickListener, FileCheckedImpl {

    private static final String TAG = "FileListPagerFragment";

    /**
     * 下拉刷新控件
     */
    private PullToRefreshLayout mPullToRefreshLayout;

    /**
     * 列表页控件
     */
    private RecyclerView mListView;

    /**
     * 根目录路径
     */
    private String mRootPath;

    /**
     * 文件列表
     */
    private List<FileItem> mFileList;

    /**
     * 列表数据适配器
     */
    private FileListAdapter mAdapter;

    /**
     * 底部工具栏事件监听器
     */
    private OnFunctionBarChangedListener mBarChangedListener;

    /**
     * 文件选择监听器
     */
    private OnFileCheckedListener mFileCheckedListener;

    /**
     * 记录每一次最后可见的Item的Position
     */
    private int mLastVisibleItemPosition;

    /**
     * 获取文件列表Task
     */
    private GetFileListTask mTask;

    /**
     * 获取文件列表异步任务
     */
    private OnGetFileListPagerListener mPagerListener;

    @Override
    public String getTAG() {
        return TAG;
    }

    public FileListPagerFragment() {
    }

    public static FileListPagerFragment newInstance(String rootPath) {
        Bundle args = new Bundle();
        args.putString(Constants.KEY_PATH, rootPath);
        FileListPagerFragment fragment = new FileListPagerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public String getTitleName() {
        return ResourcesUtils.getString(R.string.file);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null != savedInstanceState) {
            mFileList = savedInstanceState.getParcelableArrayList("saveData");
        }
        if (null == mFileList) {
            mFileList = new ArrayList<>();
        }
        mFileCheckedListener = new OnFileCheckedListener(new WeakReference<FileCheckedImpl>(this));
        mRootPath = getArguments().getString(Constants.KEY_PATH);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelableArrayList("saveData", (ArrayList<? extends Parcelable>) mFileList);
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
    public void onResume() {
        super.onResume();
        loadData(false);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            loadData(false);
        }
    }

    /**
     * 显示加载进度
     *
     * @param isShowLoading
     */
    public void loadData(boolean isShowLoading) {
        if (null != mTask) {
            mTask.cancel(true);
            mTask = null;
        }
        if (null == mPagerListener) {
            mPagerListener = new OnGetFileListPagerListener(new WeakReference<FileCheckedImpl>(this));
        }
        mTask = new GetFileListTask();
        mTask.setOnAsyncListener(mPagerListener);
        mPagerListener.setShowLoading(isShowLoading);
        mTask.asyncExecute(mRootPath);
    }

    @Override
    public void initView() {
        super.initView();
        mPullToRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData(true);
            }
        });
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new FileListAdapter(mFileList);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mTask) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    @Override
    public void setListener() {
        super.setListener();
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
        mAdapter.setOnItemCheckedListener(mFileCheckedListener);
        mListView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                //屏幕中最后一个可见子项的position
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                //当前屏幕所看到的子项个数
                int visibleItemCount = layoutManager.getChildCount();
                //当前RecyclerView的所有子项个数
                int totalItemCount = layoutManager.getItemCount();
                //是否已到达底部
                boolean isBottom = visibleItemCount > 0 && lastVisibleItemPosition == totalItemCount - 1;
                switch (newState) {
                    case RecyclerView.SCROLL_STATE_IDLE:
                        if (null != mBarChangedListener) {
                            mBarChangedListener.onBarStateChanged(true);
                        }
                        break;
                    case RecyclerView.SCROLL_STATE_DRAGGING:
                        if (null != mBarChangedListener && !isBottom) {
                            mBarChangedListener.onBarStateChanged(false);
                        }
                        break;
                }
                mLastVisibleItemPosition = lastVisibleItemPosition;
            }

        });
    }

    @Override
    public void onItemClick(View view, int itemType, int position) {
        if (position < mFileList.size()) {
            File file = new File(mFileList.get(position).getPath());
            if (file.exists()) {
                if (file.isDirectory()) {
                    getFragmentManager().beginTransaction().hide(this).add(android.R.id.content, FileListFragment.newInstance(file.getAbsolutePath()),
                            FileListFragment.TAG).addToBackStack(file.getName()).commit();
                } else {
                    InfoFragment.newInstance(InfoFragment.INFO_TYPE_FILE, file.getAbsolutePath()).show(getFragmentManager(), InfoFragment.TAG);
                }
            }
        }
    }

    /**
     * 设置底部工具栏事件监听器
     *
     * @param listener
     */
    public BaseFragment setOnFunctionBarChangedListener(OnFunctionBarChangedListener listener) {
        this.mBarChangedListener = listener;
        return this;
    }

    @Override
    public List<FileItem> getList() {
        return mFileList;
    }

    @Override
    public FileListAdapter getAdapter() {
        return mAdapter;
    }

    @Override
    public boolean isPrintLifeCycle() {
        return true;
    }

    @Override
    public boolean onItemLongClick(View v, int itemType, int position) {
        if (position < mFileList.size()) {
            File file = new File(mFileList.get(position).getPath());
            if (file.exists()) {
                InfoFragment.newInstance(InfoFragment.INFO_TYPE_FILE, file.getAbsolutePath()).show(getFragmentManager(), InfoFragment.TAG);
                return true;
            }
        }
        return false;
    }

    /**
     * 获取文件列表异步任务
     */
    private static class OnGetFileListPagerListener extends GetFileListTask.OnGetFileListListener {

        public OnGetFileListPagerListener(WeakReference<FileCheckedImpl> fragment) {
            super(fragment);
        }

        @Override
        public void onAsyncStart() {
            if (isShowLoading() && null != getFragment().get() && getFragment().get() instanceof FileListPagerFragment) {
                ((FileListPagerFragment) getFragment().get()).mPullToRefreshLayout.setRefreshing(true);
            }
        }

        @Override
        public void onAsyncCompleted() {
            if (null != getFragment().get() && getFragment().get() instanceof FileListPagerFragment) {
                ((FileListPagerFragment) getFragment().get()).mPullToRefreshLayout.setRefreshing(false);
            }
        }

    }
}
