package com.yifan.sdcardbackuper.ui.main.photo;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.model.photo.PhotoGroupItem;
import com.yifan.sdcardbackuper.ui.main.info.InfoFragment;
import com.yifan.utils.base.TitleBarFragment;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.utils.utils.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 图片列表Fragment
 *
 * Created by yifan on 2016/12/15.
 */
public class PhotoGroupFragment extends TitleBarFragment implements BaseRecyclerAdapter.OnItemCheckedListener,
        BaseRecyclerAdapter.OnItemLongClickListener, BaseRecyclerAdapter.OnItemClickListener {

    public static final String TAG = "PhotoGroupFragment";

    /**
     * 列表页控件
     */
    private RecyclerView mListView;

    /**
     * 数据适配器
     */
    private PhotoGroupAdapter mAdapter;

    /**
     * 数据
     */
    private List<PhotoGroupItem> mDatas;

    @Override
    public String getTAG() {
        return TAG;
    }

    public static PhotoGroupFragment newInstance(List<PhotoGroupItem> list) {
        Bundle args = new Bundle();
        args.putParcelableArrayList(Constant.DATA, (ArrayList<? extends Parcelable>) list);
        PhotoGroupFragment fragment = new PhotoGroupFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PhotoGroupFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatas = getArguments().getParcelableArrayList(Constant.DATA);
        mListView = (RecyclerView) LayoutInflater.from(getActivity()).inflate(R.layout.fragment_list_sub, null, false);
        setContentView(mListView);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void initView() {
        super.initView();
        mListView.setLayoutManager(new GridLayoutManager(getActivity(), 3));
        mListView.setBackgroundResource(R.color.background_white);
        mAdapter = new PhotoGroupAdapter(mDatas);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        super.setListener();
        mAdapter.setOnItemCheckedListener(this);
        mAdapter.setOnItemClickListener(this);
        mAdapter.setOnItemLongClickListener(this);
    }

    @Override
    public void onItemChecked(View view, boolean isChecked, int position) {
        mDatas.get(position).setChecked(isChecked);
    }

    @Override
    public void onItemClick(View view, int itemType, int position) {
        getFragmentManager().beginTransaction().hide(this).
                add(android.R.id.content, PhotoPreviewFragment.newInstance(mDatas.get(position)),
                        PhotoPreviewFragment.TAG).addToBackStack(PhotoPreviewFragment.TAG).commit();
    }

    @Override
    public boolean onItemLongClick(View v, int itemType, int position) {
        if (position < mDatas.size()) {
            File file = new File(mDatas.get(position).path);
            if (file.exists()) {
                InfoFragment.newInstance(InfoFragment.INFO_TYPE_PHOTO, file.getAbsolutePath()).show(getFragmentManager(), InfoFragment.TAG);
                return true;
            }
        }
        return false;
    }
}
