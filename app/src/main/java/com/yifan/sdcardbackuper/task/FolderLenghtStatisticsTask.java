package com.yifan.sdcardbackuper.task;

import android.util.Log;

import com.yifan.sdcardbackuper.ui.main.info.InfoFragment;
import com.yifan.sdcardbackuper.utils.FileUtils;
import com.yifan.utils.base.BaseAsyncTask;

import java.io.File;

/**
 * 文件夹大小统计异步任务
 *
 * Created by yifan on 2016/12/20.
 */
public class FolderLenghtStatisticsTask extends BaseAsyncTask<Object, InfoFragment.FileInfo, Void> {

    private static final String TAG = "FolderLenghtStatistics";

    /**
     * 文件夹总大小
     */
    private long mTotalLenght;

    /**
     * 数据长度
     */
    private InfoFragment.FileInfo mLenghtInfo;

    @Override
    protected Void doInBackground(Object... objects) {
        if (null != objects && objects.length > 1 && null != objects[0] && null != objects[1]) {
            if (objects[0] instanceof String) {
                if (objects[1] instanceof InfoFragment.FileInfo) {
                    mLenghtInfo = (InfoFragment.FileInfo) objects[1];
                    folderStatistics((String) objects[0]);
                }
            }
        }
        return null;
    }

    private void folderStatistics(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            if (file.isFile()) {
                mTotalLenght += file.length();
                if (null == mLenghtInfo) {
                    Log.i(TAG, "folderStatistics: " + mLenghtInfo);
                }
                mLenghtInfo.value = FileUtils.formatFileLenght(mTotalLenght);
                onProgressUpdate(mLenghtInfo);
            } else {
                String[] list = file.list();
                for (String name : list) {
                    folderStatistics(new StringBuilder(
                            file.getAbsolutePath()).append(File.separator).
                            append(name).toString());
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(InfoFragment.FileInfo... values) {
        super.onProgressUpdate(values);
        if (null != getOnAsyncListener() && getOnAsyncListener() instanceof InfoFragment.OnCountLenghtListener) {
            ((InfoFragment.OnCountLenghtListener) getOnAsyncListener()).onProgressUpdate(values);
        }
    }
}
