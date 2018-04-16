package com.yifan.sdcardbackuper.task.backup;

import android.support.v4.provider.DocumentFile;

/**
 * Created by wuyifan on 2018/4/16.
 */

public class Backup {


    /**
     * 备份方式 - 文件
     */
    public static final int BACKUP_TYPE_FILE = 0x101;
    /**
     * 备份方式 - 图片
     */
    public static final int BACKUP_TYPE_PHOTO = 0x102;

    /**
     * 目标路径
     */
    public String targetPath;

    /**
     * 备份类型
     */
    public int type;

    /**
     * SAF 根文件对象
     */
    public DocumentFile rootDirFile;
//
//    /**
//     * 文件总数
//     */
//    public long fileTotalCount;

    public Backup(String targetPath, int type, DocumentFile rootDirFile) {
        this.targetPath = targetPath;
        this.type = type;
        this.rootDirFile = rootDirFile;
    }

    /**
     * 是否使用SAF框架
     *
     * @return
     */
    public boolean isSAFUseing() {
        return null != rootDirFile && rootDirFile instanceof DocumentFile;
    }
}
