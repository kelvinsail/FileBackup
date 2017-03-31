package com.yifan.sdcardbackuper.task;

import android.preference.PreferenceManager;

import com.yifan.sdcardbackuper.ApplicationContext;
import com.yifan.sdcardbackuper.model.FileItem;
import com.yifan.sdcardbackuper.model.FileTreeNode;
import com.yifan.sdcardbackuper.ui.main.file.impl.FileCheckedImpl;
import com.yifan.sdcardbackuper.utils.Constants;
import com.yifan.sdcardbackuper.utils.copy.FileCopyManager;
import com.yifan.utils.base.BaseAsyncTask;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by yifan on 2016/12/2.
 */

public class GetFileListTask extends BaseAsyncTask<String, Void, List<FileItem>> {


    public GetFileListTask() {
    }

    @Override
    protected List<FileItem> doInBackground(String... strings) {
        List<FileItem> items = new ArrayList<>();
        if (null != strings && null != strings[0]) {
            File file = new File(strings[0]);
            FileTreeNode node = FileCopyManager.getInstance().findNodeByPath(strings[0]);
            if (file.exists() && file.isDirectory()) {
                String[] filePaths = file.list();
                if (null != filePaths && filePaths.length > 0) {
                    for (String name : filePaths) {
                        FileItem item = new FileItem(strings[0], name);
                        File subFile = new File(item.getPath());
                        boolean isShowHiddenFile = PreferenceManager.getDefaultSharedPreferences(ApplicationContext.getInstance())
                                .getBoolean(Constants.KEY_PREFERENCES_SHOW_HIDDEN_FILES, Constants.VALUE_PREFERENCES_SHOW_HIDDEN_FILES);
                        if (!subFile.exists() || (subFile.isHidden() && !isShowHiddenFile)) {
                            continue;
                        }
                        if (null != node && node.isSelectedDir) {
                            FileCopyManager.getInstance().addFile(new StringBuilder().append(strings[0]).append(File.separator).append(name).toString());
                            item.setChecked(true);
                        } else {
                            item.setChecked(FileCopyManager.getInstance().
                                    isFileExisted(new StringBuilder().append(strings[0]).
                                            append(File.separator).append(name).toString()));
                        }
                        items.add(item);
                    }
                    final String order = PreferenceManager.getDefaultSharedPreferences(ApplicationContext.getInstance())
                            .getString(Constants.KEY_PREFERENCES_FILE_ORDER, Constants.FileOrderType.getDefaultKey());
                    Collections.sort(items, new Comparator<FileItem>() {
                        @Override
                        public int compare(FileItem o1, FileItem o2) {
                            File file1 = new File(o1.getPath());
                            File file2 = new File(o2.getPath());
                            if (file1.isDirectory() && file2.isFile())
                                return -1;
                            if (file1.isFile() && file2.isDirectory())
                                return 1;
                            if (order.equals(Constants.FileOrderType.TYPE_TIME)) {
                                return (file1.lastModified() + "").compareTo(file2.lastModified() + "");
                            } else {
                                return o1.getName().compareToIgnoreCase(o2.getName());
                            }
                        }
                    });
                }
            }
        }
        return items;
    }

    /**
     * 获取文件列表异步任务
     */
    public static class OnGetFileListListener implements BaseAsyncTask.OnAsyncListener {

        private WeakReference<FileCheckedImpl> mFragment;

        /**
         * 是否显示加载进度
         */
        private boolean isShowLoading;

        public OnGetFileListListener(WeakReference<FileCheckedImpl> fragment) {
            this.mFragment = fragment;
        }

        @Override
        public void onAsyncSuccess(Object data) {
            if (null != mFragment.get() && null != data) {
                if (data instanceof List && ((List) data).size() > 0
                        && ((List) data).get(0) instanceof FileItem) {
                    mFragment.get().getList().clear();
                    mFragment.get().getList().addAll(((List) data));
                    mFragment.get().getAdapter().notifyDataSetChanged();
                }
            }
        }

        @Override
        public void onAsyncFail() {

        }

        @Override
        public void onAsyncCancelled() {

        }

        @Override
        public void onAsyncStart() {
        }

        @Override
        public void onAsyncCompleted() {
        }

        public boolean isShowLoading() {
            return isShowLoading;
        }

        public void setShowLoading(boolean showLoading) {
            isShowLoading = showLoading;
        }

        public WeakReference<FileCheckedImpl> getFragment() {
            return mFragment;
        }
    }
}