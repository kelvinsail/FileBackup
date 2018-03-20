package cn.yifan.fileiterator;

import android.support.v4.provider.DocumentFile;

import java.io.File;

/**
 * Created by wuyifan on 2017/9/8.
 */

public class IFile {

    private File mFile;

    private DocumentFile mDcFile;

    public IFile(File file) {
        this.mFile = file;
    }

    public IFile(DocumentFile dcFile) {
        this.mDcFile = dcFile;
    }

    /**
     * 是否为SAF框架
     *
     * @return
     */
    public boolean isSAF() {
        return null != mDcFile && null == mFile;
    }

    /**
     * 获取路径
     *
     * @return
     */
    public String getAbsolutePath() {
        if (isSAF()) {
            return mDcFile.getUri().getPath();
        } else {
            return mFile.getAbsolutePath();
        }
    }

    /**
     * 删除
     */
    public void delete() {
        if (isSAF()) {
            mDcFile.delete();
        } else {
            mFile.delete();
        }
    }

    /**
     * 判定是否存在
     *
     * @return
     */
    public boolean exists() {
        if (isSAF()) {
            return mDcFile.exists();
        } else {
            return mFile.exists();
        }
    }

    /**
     * 是否为文件夹
     *
     * @return
     */
    public boolean isDirectory() {
        if (isSAF()) {
            return mDcFile.isDirectory();
        } else {
            return mFile.isDirectory();
        }
    }

    /**
     * 是否为文件
     *
     * @return
     */
    public boolean isFile() {
        if (isSAF()) {
            return mDcFile.isFile();
        } else {
            return mFile.isFile();
        }
    }

    /**
     * 列出文件夹所包含的子文件数组对象
     *
     * @return
     */
    public IFile[] listFiles() {
        IFile[] result = null;
        if (isSAF()) {
            DocumentFile[] dc = mDcFile.listFiles();
            if (null != dc && dc.length > 0) {
                result = new IFile[dc.length];
                for (int i = 0; i < dc.length; i++) {
                    result[i] = new IFile(dc[i]);
                }
            }
        } else {
            File[] files = mFile.listFiles();
            if (null != files && files.length > 0) {
                result = new IFile[files.length];
                for (int i = 0; i < files.length; i++) {
                    result[i] = new IFile(files[i]);
                }
            }
        }
        return result;
    }

    /**
     * 获取文件名
     *
     * @return
     */
    public String getName() {
        if (isSAF()) {
            return mDcFile.getName();
        } else {
            return mFile.getName();
        }
    }

    /**
     * 获取文件长度
     *
     * @return
     */
    public long length() {
        if (isSAF()) {
            return mDcFile.length();
        } else {
            return mFile.length();
        }
    }

}
