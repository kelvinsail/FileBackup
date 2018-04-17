package com.yifan.sdcardbackuper.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 文件复制进度数据model
 *
 * Created by yifan on 2016/12/8.
 */
public class CopyProgress {

    /**
     * 是否在统计文件数量
     */
    public boolean isStatistics;

    /**
     * 文件总数
     */
    public long totalFileCount;

    /**
     * 已复制完成数量
     */
    public long completedCount;

    /**
     * 跳过的文件数量
     */
    public long skipedCount;

    /**
     * 当前文件路径
     */
    public String currentFilePath;

    /**
     * 复制目标路径
     */
    public String copyingToPath;

    /**
     * 复制失败列表
     */
    public List<FailLog> failList;

    /**
     * 统计获得的文件列表
     */
    public List<String> fileListStaticisces;

    /**
     * 已复制的文件列表
     */
    public List<String> fileListCopy;

    public CopyProgress() {
        this.failList = new ArrayList<>();
        this.fileListStaticisces = new ArrayList<>();
        this.fileListCopy = new ArrayList<>();
    }

}
