package com.yifan.sdcardbackuper.utils;

import java.io.Closeable;
import java.io.IOException;

/**
 * Created by yifan on 2016/11/17.
 */
public class CloseUtils {

    /**
     * 用于关闭继承{@link Closeable} 的可关闭对象，比如输出输入流
     *
     * @param closeables
     */
    public static void close(Closeable... closeables) {
        for (Closeable closeable : closeables) {
            if (null != closeable) {
                try {
                    closeable.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
