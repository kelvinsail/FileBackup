package com.yifan.sdcardbackuper.task.backup;

import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.util.Log;

import com.yifan.sdcardbackuper.ApplicationContext;
import com.yifan.sdcardbackuper.model.FailLog;
import com.yifan.sdcardbackuper.utils.Constants;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * 文件复制 线程
 *
 * Created by wuyifan on 2017/4/21.
 */

public class CopyTask extends Thread {

    private static final String TAG = "CopyTask";

    /**
     * 文件路径
     */
    private String mFilePath;

    /**
     * 目标路径
     */
    private String mTargetPath;

    /**
     * SAF 根文件对象
     */
    private DocumentFile mRootDirFile;

    /**
     * 是否跳过已存在的文件
     */
    public boolean isSkipExistedFiles;

    /**
     * 文件大小
     */
    private long mFileSize;

    public CopyTask(String mFilePath, String mTargetPath, DocumentFile mRootDirFile) {
        this.mFilePath = mFilePath;
        this.mTargetPath = mTargetPath;
        this.mRootDirFile = mRootDirFile;
    }

    @Override
    public void run() {
        Log.i(TAG, "run: " + mFilePath);
        //声明流、管道对象
        FileInputStream inputStream = null;
        FileOutputStream outputStream = null;
        FileChannel fileChannelInput = null;
        FileChannel fileChannelOutput = null;
        //记录开始时间
        long stratTime = System.currentTimeMillis();
        try {
            inputStream = new FileInputStream(mFilePath);
            File inputFile = new File(mFilePath);
            mFileSize = inputFile.length();
            boolean isSkip = false;
            if (null != mRootDirFile) {
                String[] paths = inputFile.getParentFile().getAbsolutePath().split(File.separator);
                DocumentFile targetDir = mRootDirFile;
                //判断路径文件大小写、是否存在等
                for (String dirName : paths) {
                    if (!TextUtils.isEmpty(dirName)) {
                        String[] files = new String[]{dirName, dirName.substring(0, 1).toUpperCase() + dirName.substring(1),
                                dirName.toUpperCase(), dirName.toLowerCase()};
                        boolean isExisted = false;
                        DocumentFile temp = null;
                        for (String name : files) {
                            temp = targetDir.findFile(name);
                            if (null != temp && temp.exists()) {
                                isExisted = true;
                                break;
                            }
                        }
                        if (!isExisted) {
                            targetDir = targetDir.createDirectory(dirName);
                        } else {
                            targetDir = temp;
                        }
                    }
                }
                DocumentFile targetFile = targetDir.findFile(inputFile.getName());
                //判断是否跳过
                if (isSkipExistedFiles && com.yifan.sdcardbackuper.utils.FileUtils.compareTwoFiles(inputFile, targetFile)) {
                    isSkip = true;
                } else if (null != targetFile && targetFile.exists()) {//删除目标路径存在的文件
                    targetFile.delete();
                }
                targetFile = targetDir.createFile(com.yifan.sdcardbackuper.utils.FileUtils.getMimeType(new File(mFilePath)), inputFile.getName());
                outputStream = (FileOutputStream) ApplicationContext.getInstance().getContentResolver().openOutputStream(targetFile.getUri());
            } else {
                File file = new File(new StringBuilder().append(mTargetPath).append(File.separator).append(mFilePath).toString());
                if (isSkipExistedFiles && com.yifan.sdcardbackuper.utils.FileUtils.compareTwoFiles(new File(mFilePath), file)) {
                    isSkip = true;
                } else if (null != file && file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                outputStream = new FileOutputStream(file);
            }
            if (!isSkip) {
                //获得文件通道
                fileChannelInput = inputStream.getChannel();
                fileChannelOutput = outputStream.getChannel();
                //通过管道复制
                fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
            }
            BackupTask.PROGRESS.fileListCopy.add(mFilePath);
            BackupTask.PROGRESS.completedCount++;
            if (isSkip){
                BackupTask.PROGRESS.skipedCount++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            //关闭流、对象
            closeObject(inputStream, outputStream, fileChannelInput, fileChannelOutput);
            try {
                Thread.sleep(1l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BackupTask.isCopying = false;
        }
        long cost = System.currentTimeMillis() - stratTime;
        Log.i(TAG, "copy to targetDir cost: " + cost + " ,speed: " + (0 == cost ? 0 : mFileSize / cost / 100));
    }

    /**
     * 批量关闭管道、流对象
     *
     * @param closeables
     */
    private void closeObject(Closeable... closeables) {
        if (null != closeables && closeables.length > 0) {
            for (Closeable closeable : closeables) {
                try {
                    if (null != closeable && closeable instanceof Flushable) {
                        ((Flushable) closeable).flush();
                    }
                    if (null != closeable) {
                        closeable.close();
                        closeables = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
