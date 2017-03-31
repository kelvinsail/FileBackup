package com.yifan.preferencesadapter.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.yifan.preferencesadapter.OnGroupCheckedChangeListener;
import com.yifan.preferencesadapter.R;
import com.yifan.preferencesadapter.model.PreferencesCheckGroup;

import java.util.Iterator;

/**
 * CheckGroupItem 设置界面单选框
 *
 * Created by yifan on 2016/9/18.
 */
public class CheckGroupItem extends LinearLayout implements RadioGroup.OnCheckedChangeListener {

    private static final String TAG = "CheckGroupItem";
    /**
     * 标题
     */
    private TextView mTitleText;

    /**
     * 选项
     */
    private RadioGroup mCheckGroup;

    /**
     * 选项数据
     */
    private PreferencesCheckGroup mGroup;

    /**
     * 选中项变动事件监听器
     */
    private OnGroupCheckedChangeListener mListener;

    public CheckGroupItem(Context context) {
        this(context, null);
    }

    public CheckGroupItem(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CheckGroupItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView();
    }

    /**
     * 初始化控件
     */
    private void initView() {
        inflate(getContext(), R.layout.item_setting_check_group_with_title, this);
        mTitleText = (TextView) findViewById(R.id.tv_title_ckeck_group);
        mCheckGroup = (RadioGroup) findViewById(R.id.rg_content_ckeck_group);
        mCheckGroup.setOnCheckedChangeListener(this);
    }

    /**
     * 设置数据
     *
     * @param group
     */
    public void setData(PreferencesCheckGroup group) {
        mGroup = group;
        mTitleText.setText(mGroup.getTitle());
        mCheckGroup.removeAllViews();
        if (null != mGroup && null != mGroup.getCheckGroup()) {
            Iterator<String> iterator = mGroup.getCheckGroup().keySet().iterator();
            int id = 0;
            while (iterator.hasNext()) {
                String key = iterator.next();
                String value = mGroup.getCheckGroup().get(key);
                if (null != value) {
                    CheckButton button = (CheckButton) LayoutInflater.from(getContext()).inflate(R.layout.item_check_group_sub, null);
                    button.setId(id++);
                    button.setText(value);
                    button.setTag(key);
                    button.setChecked(key.equals(mGroup.getPreferencesValue()));
                    mCheckGroup.addView(button, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
                }
            }
        }
    }

    /**
     * 设置选中项变动事件监听器
     *
     * @param listener
     */
    public void setOnGroupCheckedChangeListener(OnGroupCheckedChangeListener listener) {
        this.mListener = listener;
    }

    @Override
    public void setLayoutParams(ViewGroup.LayoutParams params) {
        if (null != params) {
            params.width = LayoutParams.MATCH_PARENT;
        }
        super.setLayoutParams(params);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (null != mListener) {
            View view = radioGroup.findViewById(i);
            if (null != view) {
                Object key = view.getTag();
                if (null != key && key instanceof String) {
                    mListener.onGroupCheckedChange(view, mGroup.getPreferencesKey(), (String) key, mGroup.getCheckGroup().get(key));
                }
            }
        }
    }

}
