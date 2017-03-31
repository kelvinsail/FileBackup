package com.yifan.sdcardbackuper.ui.main.file.impl;

import android.content.Context;

import com.yifan.sdcardbackuper.model.FileItem;
import com.yifan.sdcardbackuper.ui.main.file.FileListAdapter;

import java.util.List;

/**
 * Created by yifan on 2016/11/18.
 */
public interface FileCheckedImpl {

    /**
     * 获取文件列表
     *
     * @return
     */
    List<FileItem> getList();

    /**
     * 获取数据适配器
     *
     * @return
     */
    FileListAdapter getAdapter();

    /**
     * 获取上下文环境
     *
     * @return
     */
    Context getContext();
}
