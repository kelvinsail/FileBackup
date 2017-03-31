package com.yifan.sdcardbackuper.task;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.util.Log;

import com.yifan.sdcardbackuper.ApplicationContext;
import com.yifan.sdcardbackuper.utils.Constants;
import com.yifan.sdcardbackuper.utils.PhotoDataManager;
import com.yifan.utils.base.BaseAsyncTask;
import com.yifan.utils.utils.Constant;

/**
 * 获取本地图片异步任务
 *
 * Created by yifan on 2016/12/12.
 */
public class GetPhotoTask extends BaseAsyncTask<Object, Void, Boolean> {

    private static final String TAG = "GetPhotoTask";

    @Override
    protected Boolean doInBackground(Object... objects) {
        //先清空数据
        PhotoDataManager.getInstance().clear();
        //媒体图片查询URI
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
        ContentResolver contentResolver = ApplicationContext.getInstance().getContentResolver();

//        Cursor cursor = contentResolver.query(uri, null, MediaStore.Images.Media.CONTENT_TYPE + "=? or"
//                + MediaStore.Images.Media.CONTENT_TYPE + "=?", new String[]{"image/jpeg", "image/png","image/bmp",}, MediaStore.Images.Media.DEFAULT_SORT_ORDER);
        //查询所有相册图片
        String order = PreferenceManager.getDefaultSharedPreferences(ApplicationContext.getInstance())
                .getString(Constants.KEY_PREFERENCES_FILE_ORDER, Constants.FileOrderType.getDefaultKey());
        Cursor cursor = contentResolver.query(uri, null, null, null, order.equals(Constants.FileOrderType.TYPE_TIME)
                ? MediaStore.Images.Media.DATE_MODIFIED : MediaStore.Images.Media.TITLE);
        //内置储存空间路径，用来判断相册图片位置，加以分类
        String strogePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        //遍历数据
        while (null != cursor && cursor.moveToNext()) {
            //取出图片路径
            String path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            //取出图片位于数据库的id
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.Images.Media._ID));
            //判断是否来自外置SDcard，是否展示
            boolean isShowFromSDcard = PreferenceManager.getDefaultSharedPreferences(
                    ApplicationContext.getInstance()).getBoolean(Constants.KEY_PREFERENCES_SHOW_PHOTOES_FROM_SD,
                    Constants.VALUE_PREFERENCES_SHOW_PHOTOES_FROM_SD);
            //添加到图片数据管理器中
            if (path.startsWith(strogePath) || isShowFromSDcard) {
                PhotoDataManager.getInstance().addPhoto(id, path);
            }
        }
        cursor.close();
        return true;
    }

    /**
     * 异步执行
     *
     * @param params [0]:boolean,是否显示其他储存空间里的图片，默认不展示
     */
    @Override
    public void asyncExecute(Object... params) {
        super.asyncExecute(params);
    }
}
