package com.yifan.preferencesadapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yifan.preferencesadapter.holder.BasePrefHolder;
import com.yifan.preferencesadapter.model.Preferences;
import com.yifan.preferencesadapter.model.PreferencesCheckGroup;
import com.yifan.preferencesadapter.model.PreferencesGroup;
import com.yifan.preferencesadapter.model.PreserencesValueType;
import com.yifan.preferencesadapter.model.Type;
import com.yifan.preferencesadapter.widget.CheckGroupItem;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.utils.base.widget.BaseRecyclerHolder;
import com.yifan.utils.utils.Constant;

import java.util.ArrayList;
import java.util.List;

/**
 * 设置项数据适配器
 *
 * Created by yifan on 2016/12/22.
 */
public class PreferencesAdapter extends BaseRecyclerAdapter<BasePrefHolder>
        implements CompoundButton.OnCheckedChangeListener, OnGroupCheckedChangeListener {

    /**
     * 普通群组设置项
     */
    public static final int ITEM_TYPE_GROUP = 0x001;
    /**
     * 普通点击项
     */
    public static final int ITEM_TYPE_NORMAL = 0x002;
    /**
     * 带单选的选择设置项
     */
    public static final int ITEM_TYPE_CHECK_GROUP = 0x003;
    /**
     * 带开关设置项
     */
    public static final int ITEM_TYPE_SWITCHABLE = 0x004;

    /**
     * 设置项集合
     */
    private PreferencesGroup mGroup;

    /**
     * 设置项整合成的数组
     */
    private List<Preferences> mList;

    /**
     * 布局加载器
     */
    private LayoutInflater mLayoutInflater;

    /**
     * 上下文对象
     */
    private Context mContext;

    public PreferencesAdapter(Context context, PreferencesGroup mGroup) {
        this.mContext = context;
        this.mLayoutInflater = LayoutInflater.from(context);
        this.mGroup = mGroup;
    }

    public Preferences getItem(int position) {
        return mList.get(position);
    }

    /**
     * 获取{@link SharedPreferences}实例
     *
     * @return
     */
    public SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    @Override
    public void onGroupCheckedChange(View view, String preferencesKey, String value, String name) {
        if (null != preferencesKey && null != value) {
            getPreferences().edit().putString(preferencesKey, value).commit();
        }
    }

    @Override
    public void onClick(View view) {
        super.onClick(view);
        if (null != view && null != getOnItemClickListener()) {
            int position = BaseRecyclerHolder.getPositionFromView(view);
            int subPosition = BasePrefHolder.getSubPositionFromView(view);
            if (position >= 0) {
                int type = getItemViewType(position);
                if (subPosition >= 0) {
                    Preferences preferences = getItem(position);
                    if (preferences instanceof PreferencesGroup
                            && ((PreferencesGroup) preferences).getItems().size() > 0) {
                        switch (((PreferencesGroup) preferences).getItems().get(subPosition).getType()) {
                            case normal:
                                type = ITEM_TYPE_NORMAL;
                                break;
                            case switchable:
                                type = ITEM_TYPE_SWITCHABLE;
                                break;
                            default:
                                return;
                        }
                    }
                }
                getOnItemClickListener().onItemClick(view, type, position);
            }
        }
    }

    @Override
    public boolean onLongClick(View view) {
        super.onLongClick(view);
        if (null != view && null != getOnItemLongClickListener()) {
            int position = BaseRecyclerHolder.getPositionFromView(view);
            if (position >= 0) {
                return getOnItemLongClickListener().onItemLongClick(view, getItemViewType(position), position);
            }
        }
        return false;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        super.onCheckedChanged(buttonView, isChecked);
        if (null != getOnItemCheckedListener()) {
            int position = BaseRecyclerHolder.getPositionFromView(buttonView);
            if (position >= 0) {
                getOnItemCheckedListener().onItemChecked(buttonView, isChecked, position);
            }
        }
    }

    @Override
    public BasePrefHolder onCreate(ViewGroup parent, int viewType) {
        switch (viewType) {
            case ITEM_TYPE_GROUP:
                return getGroupHolder(parent);
            case ITEM_TYPE_NORMAL:
                return getNormalHolder(parent);
            case ITEM_TYPE_CHECK_GROUP:
                return getCheckGroupHolder(parent);
            case ITEM_TYPE_SWITCHABLE:
                return getSwitchableHolder(parent);
        }
        return null;
    }

    @Override
    public void onBind(BasePrefHolder viewHolder, int realPosition) {
        if (null != viewHolder) {
            viewHolder.setData(realPosition, mList.get(realPosition));
        }
    }

    @Override
    public int getRealItemCount() {
        if (null != mList) {
            mList.clear();
        } else {
            mList = new ArrayList<>();
        }
        if (null == mGroup) {
            return 0;
        }
        mList.addAll(mGroup.getAllDatas());
        return mList.size();
    }

    @Override
    public BasePrefHolder getFakeHolder(View view) {
        return null;
    }

    @Override
    public int getItemViewType(int position) {
        switch (mList.get(position).getType()) {
            case normal:
                return ITEM_TYPE_NORMAL;
            case group:
                return ITEM_TYPE_GROUP;
            case checkGroup:
                return ITEM_TYPE_CHECK_GROUP;
            case switchable:
                return ITEM_TYPE_SWITCHABLE;
        }
        return super.getItemViewType(position);
    }

    /**
     * 设置项群组标题Holder类
     */
    public class GroupHolder extends BasePrefHolder {

        private TextView textView;

        public GroupHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.tv_setting_item_group_title);
        }

        @Override
        public void setData(int position, Preferences preferences) {
            super.setData(position, preferences);
            textView.setText(preferences.getTitle());
            //移除旧组件
            if (((ViewGroup) itemView).getChildCount() > 1) {
                for (int i = 0; i < ((ViewGroup) itemView).getChildCount(); i++) {
                    View view = ((ViewGroup) itemView).getChildAt(i);
                    if (null != view && view.getId() != R.id.tv_setting_item_group_title) {
                        ((ViewGroup) itemView).removeView(view);
                    }
                }
            }
            if (preferences instanceof PreferencesGroup) {
                for (int i = 0; i < ((PreferencesGroup) preferences).getItems().size(); i++) {
                    Preferences item = ((PreferencesGroup) preferences).getItems().get(i);
                    switch (item.getType()) {
                        case normal:
                            NormalHolder normalHolder = (NormalHolder) getNormalHolder(null);
                            normalHolder.setData(position, item);
                            normalHolder.setSubPosition(i);
                            ((ViewGroup) itemView).addView(normalHolder.itemView);
                            break;
                        case switchable:
                            SwitchHolder switchableHolder = (SwitchHolder) getSwitchableHolder(null);
                            switchableHolder.setData(0, item);
                            switchableHolder.setSubPosition(i);
                            ((ViewGroup) itemView).addView(switchableHolder.itemView);
                            break;
                        default:
                            break;
                    }
                }
            }
        }
    }

    /**
     * 带有单选的设置项Holder类
     */
    public class CheckGroupHolder extends BasePrefHolder {

        private CheckGroupItem item;

        public CheckGroupHolder(View itemView) {
            super(itemView);
            item = (CheckGroupItem) itemView;
            ((CheckGroupItem) itemView).setOnGroupCheckedChangeListener(PreferencesAdapter.this);
        }

        @Override
        public void setData(int position, Preferences preferences) {
            super.setData(position, preferences);
            if (null != preferences && preferences instanceof PreferencesCheckGroup) {
                preferences.setPreferencesValue(getPreferences().getString(preferences.getPreferencesKey(),
                        (String) preferences.getPreferencesDefault()));
                item.setData((PreferencesCheckGroup) preferences);
            }
        }
    }

    /**
     * 带有开关的设置项Holder类
     */
    public class SwitchHolder extends BasePrefHolder {

        private ImageView imageView;
        private TextView textView;
        private SwitchCompat switchCompat;

        public SwitchHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_setting_item_switchable_icon);
            textView = (TextView) itemView.findViewById(R.id.tv_setting_item_switchable_title);
            switchCompat = (SwitchCompat) itemView.findViewById(R.id.switch_setting_item_switchable_toggle);
        }

        @Override
        public void setData(int position, Preferences preferences) {
            super.setData(position, preferences);
            imageView.setVisibility(null != preferences.getLogo() ? View.VISIBLE : View.GONE);
            imageView.setImageDrawable(preferences.getLogo());
            textView.setText(preferences.getTitle());
            setPosition(switchCompat, position);
            if (preferences.getPreferencesValueType() == PreserencesValueType.Boolean) {
                switchCompat.setOnCheckedChangeListener(null);
                boolean isChecked = false;
                if (null != preferences.getPreferencesKey()) {
                    if (null != preferences.getPreferencesDefault()) {
                        isChecked = (boolean) preferences.getPreferencesDefault();
                    }
                    isChecked = getPreferences().getBoolean(preferences.getPreferencesKey(), isChecked);
                }
                switchCompat.setChecked(isChecked);
                switchCompat.setOnCheckedChangeListener(PreferencesAdapter.this);
            }
        }
    }

    /**
     * 普通点击设置项的Holder类
     */
    public class NormalHolder extends BasePrefHolder {

        private ImageView imageView;
        private TextView textView;

        public NormalHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_setting_item_normal_icon);
            textView = (TextView) itemView.findViewById(R.id.tv_setting_item_normal_title);
        }

        @Override
        public void setData(int position, Preferences preferences) {
            super.setData(position, preferences);
            imageView.setVisibility(null != preferences.getLogo() ? View.VISIBLE : View.GONE);
            imageView.setImageDrawable(preferences.getLogo());
            textView.setText(preferences.getTitle());
        }
    }

    /**
     * 获取布局加载器
     *
     * @return
     */
    public LayoutInflater getLayoutInflater() {
        return mLayoutInflater;
    }

    /**
     * 创建并初始化 群组设置项的标题类对象实例
     *
     * @param viewParent
     * @return
     */
    public BasePrefHolder getGroupHolder(ViewGroup viewParent) {
        return new GroupHolder(getLayoutInflater().inflate(R.layout.item_setting_group_title, viewParent, false));
    }

    /**
     * 创建并初始化 普通点击的设置项
     *
     * @param viewParent
     * @return
     */
    public BasePrefHolder getNormalHolder(ViewGroup viewParent) {
        return new NormalHolder(getLayoutInflater().inflate(R.layout.item_setting_normal, viewParent, false));
    }

    /**
     * 创建并初始化 带单选项的设置项
     *
     * @param viewParent
     * @return
     */
    public BasePrefHolder getCheckGroupHolder(ViewGroup viewParent) {
        return new CheckGroupHolder(new CheckGroupItem(viewParent.getContext()));
    }

    /**
     * 创建并初始化 带开关的设置项
     *
     * @param viewParent
     * @return
     */
    public BasePrefHolder getSwitchableHolder(ViewGroup viewParent) {
        return new SwitchHolder(getLayoutInflater().inflate(R.layout.item_setting_switchable, viewParent, false));
    }

}
