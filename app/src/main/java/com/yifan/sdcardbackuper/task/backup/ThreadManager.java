package com.yifan.sdcardbackuper.task.backup;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 线程管理类
 *
 * Created by wuyifan on 2017/4/21.
 */

public class ThreadManager {

    /**
     * 默认最大核心线程数量
     */
    private static final int DEFAULt_MAX_THREAD_CONUNT =
            Runtime.getRuntime().availableProcessors() * 2 + 1;

    /**
     * 单线程线程池
     */
    private ExecutorService mSingleThreadPool;

    /**
     * 混合线程池
     */
    private ExecutorService mFixThreadPool;

    private ThreadManager() {
        mSingleThreadPool = Executors.newSingleThreadExecutor();
        mFixThreadPool = Executors.newFixedThreadPool(DEFAULt_MAX_THREAD_CONUNT);
    }

    private static class Instance {
        private static ThreadManager defaultInstance = new ThreadManager();
    }

    /**
     * 获取单例对象
     *
     * @return
     */
    public static ThreadManager getDefault() {
        return Instance.defaultInstance;
    }

    /**
     * 顺序执行
     *
     * @param runnable
     */
    public void execute(Runnable runnable) {
        mSingleThreadPool.execute(runnable);
    }

    /**
     * 异步执行
     *
     * @param runnable
     */
    public void excuteAsync(Runnable runnable) {
        mFixThreadPool.execute(runnable);
    }

}
