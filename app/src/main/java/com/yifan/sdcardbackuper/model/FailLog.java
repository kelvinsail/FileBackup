package com.yifan.sdcardbackuper.model;

/**
 * 复制失败日志类
 *
 * Created by yifan on 2016/12/13.
 */
public class FailLog {

    /**
     * 文件路径
     */
    public String path;

    /**
     * 失败原因
     */
    public String cause;

    public FailLog(String path, String cause) {
        this.path = path;
        this.cause = cause;
    }

}
