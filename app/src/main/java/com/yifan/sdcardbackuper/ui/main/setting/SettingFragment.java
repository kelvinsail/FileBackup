package com.yifan.sdcardbackuper.ui.main.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

import com.yifan.preferencesadapter.PreferencesAdapter;
import com.yifan.preferencesadapter.model.Preferences;
import com.yifan.preferencesadapter.model.PreferencesCheckGroup;
import com.yifan.preferencesadapter.model.PreferencesGroup;
import com.yifan.preferencesadapter.model.PreserencesValueType;
import com.yifan.preferencesadapter.model.Type;
import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.utils.Constants;
import com.yifan.utils.base.TitleBarFragment;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.utils.utils.Constant;
import com.yifan.utils.utils.ResourcesUtils;

/**
 * 设置界面
 *
 * Created by yifan on 2016/12/22.
 */
public class SettingFragment extends TitleBarFragment implements BaseRecyclerAdapter.OnItemCheckedListener, BaseRecyclerAdapter.OnItemClickListener {

    public static final String TAG = "SettingFragment";

    /**
     * 列表页控件
     */
    private RecyclerView mListView;

    /**
     * 设置项数据适配器
     */
    private PreferencesAdapter mAdapter;

    /**
     * 设置项
     */
    private PreferencesGroup mSettingGroup;

    @Override
    public String getTAG() {
        return TAG;
    }

    public SettingFragment() {
    }

    public static SettingFragment newInstance() {
        Bundle args = new Bundle();
        SettingFragment fragment = new SettingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        initSetting();
        setContentView(R.layout.fragment_setting_main, 0, false);
    }

    /**
     * 初始化设置项内容
     */
    private void initSetting() {
        // 照片设置项
        PreferencesGroup photoGroup = new PreferencesGroup.GroupBuilder().setTitle(ResourcesUtils.getString(R.string.setting_photo))
                .addSubItem(R.id.setting_photo_show_from_sdcard, ResourcesUtils.getDrawable(R.drawable.icon_polaroid),
                        Type.switchable, ResourcesUtils.getString(R.string.setting_photo_show_from_sdcard), Constants.KEY_PREFERENCES_SHOW_PHOTOES_FROM_SD,
                        PreserencesValueType.Boolean, Constants.VALUE_PREFERENCES_SHOW_PHOTOES_FROM_SD, true).build();
        //文档设置
        PreferencesGroup fileGroup = new PreferencesGroup.GroupBuilder().setTitle(ResourcesUtils.getString(R.string.setting_file))
                .addSubItem(R.id.setting_file_show_hidden_file, ResourcesUtils.getDrawable(R.drawable.icon_folder),
                        Type.switchable, ResourcesUtils.getString(R.string.setting_file_show_hidden_file), Constants.KEY_PREFERENCES_SHOW_HIDDEN_FILES,
                        PreserencesValueType.Boolean, Constants.VALUE_PREFERENCES_SHOW_HIDDEN_FILES, true).build();
        //文件排序
        PreferencesCheckGroup sortGroup = new PreferencesCheckGroup.CheckGroupBuilder().setTitle(ResourcesUtils.getString(R.string.setting_file_order)).
                setPreferencesValueType(PreserencesValueType.String).setPreferencesKey(Constants.KEY_PREFERENCES_FILE_ORDER).
                setCheckGroup(Constants.FileOrderType.getOrderSet()).setPreferencesDefault(Constants.FileOrderType.getDefaultKey()).build();

        //其他设置项
        PreferencesGroup otherGroup = new PreferencesGroup.GroupBuilder().setTitle(ResourcesUtils.getString(R.string.setting_other))
                .addSubItem(R.id.setting_backup_copy_to_storage, null, Type.switchable,
                        ResourcesUtils.getString(R.string.unable_copy_to_storage),
                        Constants.KEY_PREFERENCES_COPY_TO_STORAGE,
                        PreserencesValueType.Boolean, Constants.VALUE_PREFERENCES_COPY_TO_STORAGE, true)
                .addSubItem(R.id.setting_backup_skip_existed_files, null, Type.switchable,
                        ResourcesUtils.getString(R.string.skip_existed_files),
                        Constants.KEY_PREFERENCES_SKIP_EXISTED_FILES,
                        PreserencesValueType.Boolean, Constants.VALUE_PREFERENCES_SKIP_EXISTED_FILES, true)
                .build();
//                .addSubItem(R.id.setting_other_be_careof, null, Type.normal,
//                        ResourcesUtils.getString(R.string.setting_other_be_careof), null, null, null, true)
//                .addSubItem(R.id.setting_other_about, null, Type.normal,
//                        ResourcesUtils.getString(R.string.setting_other_about), null, null, null, true).build();
        //添加到根项中
        mSettingGroup = new PreferencesGroup.GroupBuilder()
                .addSubItem(photoGroup).addSubItem(fileGroup)
                .addSubItem(sortGroup).addSubItem(otherGroup)
                .build();
    }

    @Override
    public void initView() {
        super.initView();
        getSupportTitleBar().setTitle(R.string.setting);
        mListView = (RecyclerView) getContentView().findViewById(R.id.rv_setting_main);
        mAdapter = new PreferencesAdapter(this.getActivity(), mSettingGroup);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mListView.setAdapter(mAdapter);
    }

    @Override
    public void setListener() {
        super.setListener();
        mAdapter.setOnItemCheckedListener(this);
        mAdapter.setOnItemClickListener(this);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public void onItemChecked(View view, boolean isChecked, int position) {
        Log.i(TAG, "onItemChecked: " + mAdapter.getItem(position).getTitle() + " , " + isChecked);
        Preferences preferences = mAdapter.getItem(position);
        if (null != preferences && null != mAdapter.getPreferences()) {
            mAdapter.getPreferences().edit().putBoolean(preferences.getPreferencesKey(), isChecked).commit();
        }
    }

    @Override
    public void onItemClick(View view, int itemType, int position) {
        switch (itemType) {
            case PreferencesAdapter.ITEM_TYPE_SWITCHABLE:
                SwitchCompat switchCompat = (SwitchCompat) view.findViewById(R.id.switch_setting_item_switchable_toggle);
                if (null != switchCompat) {
                    switchCompat.toggle();
                }
                break;
            default:
                Log.i(TAG, "onItemClick: " + mAdapter.getItem(position).getTitle());
                break;
        }
    }
}
