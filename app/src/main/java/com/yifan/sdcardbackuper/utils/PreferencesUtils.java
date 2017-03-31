package com.yifan.sdcardbackuper.utils;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.yifan.sdcardbackuper.ApplicationContext;
import com.yifan.utils.base.BaseApplication;

import java.util.prefs.Preferences;

/**
 * {@link SharedPreferences}工具类
 *
 * Created by yifan on 2016/12/24.
 */
public class PreferencesUtils {

    /**
     * 获取{@link SharedPreferences}实例
     *
     * @param fileName
     * @return
     */
    public static SharedPreferences getPreferences(String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return PreferenceManager.getDefaultSharedPreferences(BaseApplication.getInstance());
        } else {
            return ApplicationContext.getInstance().getSharedPreferences(fileName, Context.MODE_PRIVATE);
        }
    }

    /**
     * 获取boolean参数
     *
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultValue) {
        return getBoolean(null, key, defaultValue);
    }

    /**
     * 获取boolean参数
     *
     * @param fileName
     * @param key
     * @param defaultValue
     * @return
     */
    public static boolean getBoolean(String fileName, String key, boolean defaultValue) {
        SharedPreferences preferences = getPreferences(fileName);
        if (null == preferences) {
            return defaultValue;
        }
        return preferences.getBoolean(key, defaultValue);
    }

}
