package cn.yifan.fileiterator;

/**
 * Created by yifan on 2017/8/17.
 */

public class TreeIterator {

    private static final String TAG = TreeIterator.class.getSimpleName();

    /**
     * 是否取消
     */
    private boolean isCancel;

    /**
     * 根目录
     */
    private IFile mFile;

    /**
     * 遍历回调，异步
     */
    private OnAsyncIteratorListener mListener;

    /**
     * 数据或对象、接口
     */
    private Object mTag;

    /**
     * @param file     根目录
     * @param listener 异步处理监听器，传入则使用异步处理文件，不传则必须重写相关处理函数进行处理
     */
    public TreeIterator(IFile file, OnAsyncIteratorListener listener) {
        this.mFile = file;
        this.mListener = listener;
    }

    /**
     * 遍历文件
     */
    public void startIterator() {
        if (null != mListener) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    startIterator(mFile, mListener);
                    if (null != mListener) {
                        mListener.onCompleted(mTag);
                    }
                }
            }).start();
        } else {
            startIterator(mFile, mListener);
            onCompleted(mTag);
        }
    }

    /**
     * 开始迭代进行遍历
     *
     * @param file
     * @param listener
     */
    private void startIterator(IFile file, OnAsyncIteratorListener listener) {
        if (null != file & file.exists()) {
            if (file.isFile()) {
                if (null != listener) {
                    listener.onNextFile(mTag, file.getAbsolutePath(), file.getName(), file.length());
                } else {
                    onNextFile(mTag, file.getAbsolutePath(), file.getName(), file.length());
                }
                return;
            }
            IFile[] files = file.listFiles();
            if (null != listener) {
                listener.onNextDir(mTag, file.getAbsolutePath());
            } else {
                onNextDir(mTag, file.getAbsolutePath());
            }
            if (null != files && files.length > 0) {
                for (IFile temp : files) {
                    if (isCancel) {
                        return;
                    }
                    if (null != temp && temp.exists()) {
                        if (temp.isDirectory()) {
                            startIterator(temp, listener);
                        } else {
                            if (null != listener) {
                                listener.onNextFile(mTag, temp.getAbsolutePath(), temp.getName(), temp.length());
                            } else {
                                onNextFile(mTag, temp.getAbsolutePath(), temp.getName(), temp.length());
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * 取消遍历
     */
    public void cancel() {
        isCancel = true;
    }

    /**
     * @param tag
     */
    public void setTag(Object tag) {
        mTag = tag;
    }

    /**
     * 处理文件夹，非异步
     *
     * @param Tag
     * @param path
     */
    protected void onNextDir(Object Tag, String path) {
    }

    /**
     * 处理文件，非异步
     *
     * @param Tag
     * @param path
     * @param fileName
     * @param fileSize
     */
    protected void onNextFile(Object Tag, String path, String fileName, long fileSize) {
    }

    /**
     * 本次遍历结束，非异步
     *
     * @param Tag
     */
    protected void onCompleted(Object Tag) {
    }

    /**
     * 遍历文件处理监听器，异步
     *
     * @param <T> 传入{@link TreeIterator}的Tag对象，可使用对象、接口作为tag
     */
    public interface OnAsyncIteratorListener<T extends Object> {

        void onNextDir(T tag, String path);

        void onNextFile(T tag, String path, String fileName, long fileSize);

        void onCompleted(T tag);
    }
}
