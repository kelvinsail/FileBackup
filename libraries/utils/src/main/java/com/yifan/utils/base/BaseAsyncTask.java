package com.yifan.utils.base;

import android.os.AsyncTask;

/**
 * BaseAsyncTask 全局异步任务基类
 *
 * Created by yifan on 2016/8/30.
 */
public abstract class BaseAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    /**
     * 最短运行时间
     */
    public static final long ASYNC_MINI_RUN_TIME = 1000L;

    /**
     * 异步任务监听器
     */
    OnAsyncListener mAsyncListener;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        if (null != mAsyncListener) {
            mAsyncListener.onAsyncStart();
        }
    }

    @Override
    protected abstract Result doInBackground(Params... paramses);

    @Override
    protected void onPostExecute(Result result) {
        super.onPostExecute(result);
        if (null != mAsyncListener) {
            //判断任务是否已取消
            if (isCancelled()) {
                mAsyncListener.onAsyncCancelled();
                return;
            } else {
                //通知执行结果
                if (null != result) {
                    mAsyncListener.onAsyncSuccess(result);
                } else {
                    mAsyncListener.onAsyncFail();
                }
            }
            mAsyncListener.onAsyncCompleted();
            mAsyncListener = null;
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if (null != mAsyncListener) {
            mAsyncListener.onAsyncCancelled();
            mAsyncListener.onAsyncCompleted();
            mAsyncListener = null;
        }
    }

    /**
     * 异步执行
     *
     * @param params
     */
    public void asyncExecute(Params... params) {
        executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
    }

    /**
     * 设置异步任务监听器
     *
     * @param listener {@link OnAsyncListener}
     */
    public void setOnAsyncListener(OnAsyncListener listener) {
        this.mAsyncListener = listener;
    }

    /**
     * 获取异步任务监听器
     *
     * @return {@link OnAsyncListener}
     */
    public OnAsyncListener getOnAsyncListener() {
        return mAsyncListener;
    }

    /**
     * 异步任务监听器
     */
    public interface OnAsyncListener {

        /**
         * 数据处理结束或提交之后有回应
         *
         * @param data result from {@linkplain #doInBackground(Object[])}
         */
        void onAsyncSuccess(Object data);

        /**
         * 数据处理出现异常或提交之后无响应、超时
         */
        void onAsyncFail();

        /**
         * 任务取消
         */
        void onAsyncCancelled();

        /**
         * 任务开始
         */
        void onAsyncStart();

        /**
         * 任务结束
         */
        void onAsyncCompleted();
    }
}
