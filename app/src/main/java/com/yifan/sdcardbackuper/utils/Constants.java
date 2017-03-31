package com.yifan.sdcardbackuper.utils;

import android.os.Environment;

import com.yifan.sdcardbackuper.R;
import com.yifan.utils.utils.ResourcesUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by yifan on 2016/11/15.
 */
public class Constants {

    /**
     * 键值对key - Path
     */
    public static final String KEY_PATH = "path";

    /**
     * 文件存放根目录
     */
    public static final String PATH_STORAGE_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath().concat(File.separator).concat(ResourcesUtils.getString(R.string.app_name));

    /**
     * 备份根目录文件夹名
     */
    public static final String FILE_BACKUP_ROOT_NAME = "BackupDir";

    /**
     * PreferencesKey - 展示外置SDcard的照片
     */
    public static final String KEY_PREFERENCES_SHOW_PHOTOES_FROM_SD = "key_preferences_show_photoes_from_sd";
    public static final boolean VALUE_PREFERENCES_SHOW_PHOTOES_FROM_SD = false;

    /**
     * PreferencesKey - 展示隐藏文件/文件夹
     */
    public static final String KEY_PREFERENCES_SHOW_HIDDEN_FILES = "key_preferences_show_hidden_files";
    public static final boolean VALUE_PREFERENCES_SHOW_HIDDEN_FILES = false;

    /**
     * PreferencesKey - 文件排序方式
     */
    public static final String KEY_PREFERENCES_FILE_ORDER = "key_preferences_file_order";

    /**
     * 排序方式类
     */
    public static class FileOrderType {

        /**
         * key - 按时间排序
         */
        public static final String TYPE_TIME = "time";
        /**
         * key - 按名称排序
         */
        public static final String TYPE_NAME = "name";

        /**
         * 获取排序
         *
         * @return
         */
        public static HashMap<String, String> getOrderSet() {
            HashMap<String, String> orderSet = new HashMap<>();
            orderSet.put(TYPE_NAME, ResourcesUtils.getString(R.string.setting_file_order_file_name));
            orderSet.put(TYPE_TIME, ResourcesUtils.getString(R.string.setting_file_order_create_time));
            return orderSet;
        }

        /**
         * 默认选中key
         *
         * @return
         */
        public static String getDefaultKey() {
            return TYPE_NAME;
        }

    }

    /**
     * PreferencesKey - 展示隐藏文件/文件夹
     */
    public static final String KEY_PREFERENCES_COPY_TO_STORAGE = "key_preferences_copy_to_storage";
    public static final boolean VALUE_PREFERENCES_COPY_TO_STORAGE = true;
}
