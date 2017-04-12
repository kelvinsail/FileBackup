package com.yifan.sdcardbackuper.ui.widget;

import android.support.annotation.StringRes;
import android.support.design.widget.Snackbar;
import android.view.View;

import com.yifan.sdcardbackuper.R;
import com.yifan.utils.utils.ResourcesUtils;

/**
 * 通知提示统一入口
 *
 * Created by wuyifan on 2017/4/12.
 */

public class TSManager {

    /**
     * 通过SnackBar显示通知提示
     *
     * @param view     绑定的布局控件
     * @param resID    提示文本资源id
     * @param duration 时长
     */
    public static void showSnackTips(View view, @StringRes int resID, int duration) {
        showSnackTips(view, ResourcesUtils.getString(resID), duration);
    }


    /**
     * 通过SnackBar显示通知提示
     *
     * @param view     绑定的布局控件
     * @param msg      提示文本
     * @param duration 时长
     */
    public static void showSnackTips(View view, String msg, int duration) {
        showSnackTips(view, msg, null, null, duration);
    }

    /**
     * 通过SnackBar显示通知提示
     *
     * @param view     绑定的布局控件
     * @param msg      提示文本
     * @param resID    交互按钮文本资源ID
     * @param l        交互按钮点击事件监听器
     * @param duration 时长
     */
    public static void showSnackTips(View view, String msg, @StringRes int resID, View.OnClickListener l, int duration) {
        showSnackTips(view, msg, ResourcesUtils.getString(resID), l, duration);
    }

    /**
     * 通过SnackBar显示通知提示
     *
     * @param view       绑定的布局控件
     * @param msgResId   提示文本
     * @param actionName 交互按钮文本
     * @param l          交互按钮点击事件监听器
     * @param duration   时长
     */
    public static void showSnackTips(View view, @StringRes int msgResId, String actionName, View.OnClickListener l, int duration) {
        showSnackTips(view, ResourcesUtils.getString(msgResId), actionName, l, duration);
    }

    /**
     * 通过SnackBar显示通知提示
     *
     * @param view     绑定的布局控件
     * @param msgResId 提示文本资源ID
     * @param resID    交互按钮文本资源ID
     * @param l        交互按钮点击事件监听器
     * @param duration 时长
     */

    public static void showSnackTips(View view, @StringRes int msgResId, @StringRes int resID, View.OnClickListener l, int duration) {
        showSnackTips(view, ResourcesUtils.getString(msgResId), resID, l, duration);
    }

    /**
     * 通过SnackBar显示通知提示
     *
     * @param view       绑定的布局控件
     * @param msg        提示文本
     * @param actionName 交互按钮文本
     * @param l          交互按钮点击事件监听器
     * @param duration   时长
     */
    public static void showSnackTips(View view, String msg, String actionName, View.OnClickListener l, int duration) {
        Snackbar.make(view, msg, duration).setAction(actionName, l)
                .setActionTextColor(ResourcesUtils.getColor(R.color.colorPrimary)).show();
    }
}
