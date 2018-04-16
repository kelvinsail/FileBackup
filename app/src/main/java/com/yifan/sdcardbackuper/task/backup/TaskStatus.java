package com.yifan.sdcardbackuper.task.backup;

import com.yifan.sdcardbackuper.model.CopyProgress;

/**
 * Created by wuyifan on 2018/4/16.
 */

public class TaskStatus {
    public enum Status {
        START,
        FAIL,
        COMPLETED,
        SUCCESS,
        CANCEL;
    }

    public Status status;
    public CopyProgress copyProgresses;

    public TaskStatus(Status status, CopyProgress copyProgresses) {
        this.status = status;
        this.copyProgresses = copyProgresses;
    }
}
