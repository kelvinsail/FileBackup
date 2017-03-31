package com.yifan.sdcardbackuper.utils;

/**
 * Created by yifan on 2016/11/15.
 */
public class UnSupportException extends Exception {

    public UnSupportException() {
        super("Does not support the current system version!!!");
    }

}
