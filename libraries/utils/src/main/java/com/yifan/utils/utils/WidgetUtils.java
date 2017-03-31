package com.yifan.utils.utils;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.TypedValue;
import android.view.View;

import com.yifan.utils.R;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * WidgetUtils 控件工具类
 *
 * Created by yifan on 2016/8/18.
 */
public class WidgetUtils {

    /**
     * 获取状态栏高度
     *
     * @return
     */
    public static int getStatusBarHeight() {
        Class<?> c = null;
        Object obj = null;
        Field field = null;
        int x = 0, statusbarHeight = 0;
        try {
            c = Class.forName("com.android.internal.R$dimen");
            obj = c.newInstance();
            field = c.getField("status_bar_height");
            x = Integer.parseInt(field.get(obj).toString());
            statusbarHeight = ResourcesUtils.getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            statusbarHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                    24, ResourcesUtils.getDisplayMetrics());
        }
        return statusbarHeight;
    }

    /**
     * 获取虚拟按键栏高度
     *
     * @return 高度
     */
    public static int getNavigationBarHeight() {
        int navigationBarHeight = 0;
        int id = ResourcesUtils.getIdentifier("navigation_bar_height", "dimen", "android");
        if (id > 0 && checkDeviceHasNavigationBar()) {
            navigationBarHeight = ResourcesUtils.getDimensionPixelSize(id);
        }
        return navigationBarHeight;
    }

    /**
     * 判断系统是否拥有虚拟按键栏
     *
     * @return true：有；false：没有
     */
    public static boolean checkDeviceHasNavigationBar() {
        boolean hasNavigationBar = false;
        int id = ResourcesUtils.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = ResourcesUtils.getBoolean(id);
            try {
                Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
                Method m = systemPropertiesClass.getDeclaredMethod("get", String.class);
                m.setAccessible(true);
                String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
                if ("1".equals(navBarOverride)) {
                    hasNavigationBar = false;
                } else if ("0".equals(navBarOverride)) {
                    hasNavigationBar = true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return hasNavigationBar;

    }


    /**
     * 根据系统版本自动设置布局点击背景效果
     *
     * @param view
     */
    public static void setItemClickBackground(View view) {
        //获取主题定义的点击效果
        TypedArray array = view.getContext().getTheme().obtainStyledAttributes(
                new int[]{R.attr.item_click_background});
        Drawable background = (Drawable) array
                .getDrawable(0);
        //设置点击效果
        if (null != background) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(background);
            } else {
                view.setBackgroundDrawable(background);
            }
        }
        array.recycle();
    }

    /**
     * 根据系统版本自动设置布局点击背景效果(透明底色)
     *
     * @param view
     */
    public static void setItemClickBackgroundTransParent(View view) {
        //获取主题定义的点击效果
        TypedArray array = view.getContext().getTheme().obtainStyledAttributes(
                new int[]{R.attr.item_click_background_transparent});
        Drawable background = (Drawable) array
                .getDrawable(0);
        //设置点击效果
        if (background != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                view.setBackground(background);
            } else {
                view.setBackgroundDrawable(background);
            }
        }
        array.recycle();
    }

}
