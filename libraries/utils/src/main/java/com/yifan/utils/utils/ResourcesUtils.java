package com.yifan.utils.utils;

import android.annotation.TargetApi;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ArrayRes;
import android.support.annotation.BoolRes;
import android.support.annotation.ColorRes;
import android.support.annotation.DimenRes;
import android.support.annotation.DrawableRes;
import android.support.annotation.IntegerRes;
import android.support.annotation.StringRes;
import android.util.DisplayMetrics;

import com.yifan.utils.base.BaseApplication;

import java.lang.ref.WeakReference;

/**
 * 资源获取统一入口
 *
 * Created by yifan on 2016/7/16.
 */
public class ResourcesUtils {

    private static WeakReference<BaseApplication> mApplication;

    public static void setApplication(BaseApplication application) {
        mApplication = new WeakReference<BaseApplication>(application);
    }

    public static Resources getResources() {
        if (null == mApplication.get()) {
            return null;
        }
        return mApplication.get().getInstance().getResources();
    }

    public static String getString(@StringRes int resID) {
        if (null == mApplication.get() || resID <= 0) {
            return null;
        }
        return mApplication.get().getInstance().getResources().getString(resID);
    }

    /**
     * 获取定义的字符串
     *
     * @param id
     * @return
     */
    public static String getString(@StringRes int id, Object... formatArgs) {
        if (null == mApplication.get() || id <= 0) {
            return null;
        }
        return mApplication.get().getInstance().getResources().getString(id, formatArgs);
    }


    public static String[] getStringArray(@ArrayRes int id) {
        if (null == mApplication.get() || id <= 0) {
            return null;
        }
        return mApplication.get().getInstance().getResources().getStringArray(id);
    }

    public static int[] getIntArray(@ArrayRes int id) {
        if (null == mApplication.get() || id <= 0) {
            return null;
        }
        return mApplication.get().getInstance().getResources().getIntArray(id);
    }


    public static float getDimension(@DimenRes int id) {
        if (null == mApplication.get() || id <= 0) {
            return 0f;
        }
        return mApplication.get().getInstance().getResources().getDimension(id);
    }

    public static int getDimensionPixelOffset(@DimenRes int id) {
        if (null == mApplication.get() || id <= 0) {
            return 0;
        }
        return mApplication.get().getInstance().getResources().getDimensionPixelOffset(id);
    }

    public static int getDimensionPixelSize(@DimenRes int id) {
        if (null == mApplication.get() || id <= 0) {
            return 0;
        }
        return mApplication.get().getInstance().getResources().getDimensionPixelSize(id);
    }

    public static DisplayMetrics getDisplayMetrics() {
        if (null == mApplication.get()) {
            return null;
        }
        return mApplication.get().getInstance().getResources().getDisplayMetrics();
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static Drawable getDrawable(@DrawableRes int id) {
        if (null == mApplication.get() || id <= 0) {
            return null;
        }
        return mApplication.get().getInstance().getResources().getDrawable(id);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static Drawable getDrawable(@DrawableRes int id, Resources.Theme theme) {
        if (null == mApplication.get() || id <= 0) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mApplication.get().getInstance().getResources().getDrawable(id, theme);
        }
        return mApplication.get().getInstance().getResources().getDrawable(id);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static int getColor(@ColorRes int id) {
        if (null == mApplication.get() || id <= 0) {
            return 0;
        }
        return mApplication.get().getInstance().getResources().getColor(id);
    }

    public static int getColor(@ColorRes int id, Resources.Theme theme) {
        if (null == mApplication.get() || id <= 0) {
            return 0;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mApplication.get().getInstance().getResources().getColor(id, theme);
        }
        return mApplication.get().getInstance().getResources().getColor(id);
    }

    /**
     * @deprecated
     */
    @Deprecated
    public static ColorStateList getColorStateList(@ColorRes int id) {
        if (null == mApplication.get() || id <= 0) {
            return null;
        }
        return mApplication.get().getInstance().getResources().getColorStateList(id);
    }

    public static ColorStateList getColorStateList(@ColorRes int id, Resources.Theme theme) {
        if (null == mApplication.get() || id <= 0) {
            return null;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return mApplication.get().getInstance().getResources().getColorStateList(id, theme);
        }
        return mApplication.get().getInstance().getResources().getColorStateList(id);
    }

    public static boolean getBoolean(@BoolRes int id) throws Resources.NotFoundException {
        if (null == mApplication.get() || id <= 0) {
            return false;
        }
        return mApplication.get().getInstance().getResources().getBoolean(id);
    }

    public static int getInteger(@IntegerRes int id) throws Resources.NotFoundException {
        if (null == mApplication.get() || id <= 0) {
            return 0;
        }
        return mApplication.get().getInstance().getResources().getInteger(id);
    }

    public static Bitmap openRawResource(int drawableID) {
        return BitmapFactory.decodeResource(getResources(), drawableID);
    }

    public static int getIdentifier(String name, String defType, String defPackage) {
        return mApplication.get().getInstance().getResources().getIdentifier(name, defType, defPackage);
    }
}
