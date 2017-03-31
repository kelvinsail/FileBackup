package com.yifan.preferencesadapter.holder;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import com.yifan.preferencesadapter.model.Preferences;
import com.yifan.utils.base.widget.BaseRecyclerHolder;

/**
 * Holder基类
 *
 * Created by yifan on 2016/12/29.
 */
public abstract class BasePrefHolder extends BaseRecyclerHolder {


    public static final String POSITION_SUB = "sub_position";

    public BasePrefHolder(View itemView) {
        super(itemView);
    }

    public void setData(int position, Preferences preferences) {
        boolean isEnable = preferences.isEnable();
        if (null != preferences.getParent()) {
            if (!preferences.getParent().isEnable()) {
                isEnable = false;
            }
        }
        itemView.setEnabled(isEnable);
        if (itemView instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) itemView).getChildCount(); i++) {
                ((ViewGroup) itemView).getChildAt(i).setEnabled(isEnable);
            }
        }
    }

    /**
     * 设置子序号等数据
     *
     * @param position
     */
    public void setSubPosition(int position) {
        setPosition(itemView, position);
    }

    /**
     * 设置序号
     *
     * @param view     控件
     * @param position 序号
     */
    public static void setSubPosition(View view, int position) {
        Bundle data;
        if (null != view
                && null != view.getTag()
                && view.getTag() instanceof Bundle) {
            data = (Bundle) view.getTag();
        } else {
            data = new Bundle();
        }
        data.putInt(POSITION_SUB, position);
        view.setTag(data);
    }


    /**
     * 从View的tag中取出序号
     *
     * @param view
     * @return
     */
    public static int getSubPositionFromView(View view) {
        int posotion = -1;
        //取出序号
        if (null != view && null != view.getTag()
                && view.getTag() instanceof Bundle) {
            posotion = ((Bundle) view.getTag()).getInt(POSITION_SUB, -1);
        }
        return posotion;
    }
}