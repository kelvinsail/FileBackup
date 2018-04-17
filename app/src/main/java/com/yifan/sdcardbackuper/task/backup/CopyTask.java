package com.yifan.sdcardbackuper.task.backup;

import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.util.Log;

import com.yifan.sdcardbackuper.ApplicationContext;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Flushable;
import java.io.IOException;
import java.math.BigDecimal;
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
//        BufferedInputStream bis = null;
//        BufferedOutputStream bos = null;
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
                //判断路径、文件大小写、是否存在等
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
                Log.i(TAG, "dfile 创建路径 cost: "+new BigDecimal(System.currentTimeMillis() - stratTime)
                        .divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
                DocumentFile targetFile = targetDir.findFile(inputFile.getName());
                //判断是否跳过
                if (isSkipExistedFiles && com.yifan.sdcardbackuper.utils.FileUtils.compareTwoFiles(inputFile, targetFile)) {
                    isSkip = true;
                } else if (null != targetFile && targetFile.exists()) {//删除目标路径存在的文件
                    targetFile.delete();
                }
                targetFile = targetDir.createFile(com.yifan.sdcardbackuper.utils.FileUtils.getMimeType(new File(mFilePath)), inputFile.getName());

                Log.i(TAG, "dfile 创建文件 cost: "+new BigDecimal(System.currentTimeMillis() - stratTime)
                        .divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).doubleValue());

                outputStream = (FileOutputStream) ApplicationContext.getInstance().getContentResolver().openOutputStream(targetFile.getUri());
                Log.i(TAG, "dfile final cost: "+new BigDecimal(System.currentTimeMillis() - stratTime)
                        .divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
            } else {
                File file = new File(new StringBuilder().append(mTargetPath).append(File.separator).append(mFilePath).toString());
                if (isSkipExistedFiles && com.yifan.sdcardbackuper.utils.FileUtils.compareTwoFiles(new File(mFilePath), file)) {
                    isSkip = true;
                } else if (null != file && file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                outputStream = new FileOutputStream(file);
                Log.i(TAG, "file 判断cost: "+new BigDecimal(System.currentTimeMillis() - stratTime)
                        .divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP).doubleValue());
            }
            if (!isSkip) {
                //第一种方法字节流传输
//                bis = new BufferedInputStream(inputStream);
//                bos = new BufferedOutputStream(outputStream);
//                byte[] bys = new byte[4096];
//                int len = 0;
//                while ((len = bis.read(bys)) != -1) {
//                    bos.write(bys, 0, len);
//                }
//                bos.flush();

                //第三种方法：管道传输
                //获得文件通道
                fileChannelInput = inputStream.getChannel();
                fileChannelOutput = outputStream.getChannel();
                //通过管道复制
                fileChannelInput.transferTo(0, fileChannelInput.size(), fileChannelOutput);
            }
            BackupTask.PROGRESS.fileListCopy.add(mFilePath);
            BackupTask.PROGRESS.completedCount++;
            if (isSkip) {
                BackupTask.PROGRESS.skipedCount++;
            }
//            bis.close();
//            bos.close();
            fileChannelInput.close();
            fileChannelOutput.close();
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                Thread.sleep(1l);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            BackupTask.isCopying = false;
        }
        //计算时间效率
        mFileSize = new BigDecimal(mFileSize).divide(new BigDecimal(1024), 2, BigDecimal.ROUND_HALF_UP).longValue();

        BigDecimal cost = new BigDecimal(System.currentTimeMillis() - stratTime).divide(new BigDecimal(1000), 2, BigDecimal.ROUND_HALF_UP);
        double speed = new BigDecimal(mFileSize).divide(cost, 2, BigDecimal.ROUND_HALF_UP).doubleValue();

        Log.i(TAG, mFileSize + "KB copy to targetDir cost: " + cost + "s ,speed: " + speed + "KB/s");
    }

}
