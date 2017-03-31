package com.yifan.sdcardbackuper.task;

import android.support.v4.provider.DocumentFile;
import android.text.TextUtils;
import android.util.Log;

import com.yifan.sdcardbackuper.ApplicationContext;
import com.yifan.sdcardbackuper.model.CopyProgress;
import com.yifan.sdcardbackuper.model.FailLog;
import com.yifan.sdcardbackuper.model.FileTree;
import com.yifan.sdcardbackuper.model.FileTreeNode;
import com.yifan.sdcardbackuper.ui.main.MainActivity;
import com.yifan.sdcardbackuper.utils.Constants;
import com.yifan.sdcardbackuper.utils.copy.FileCopyManager;
import com.yifan.sdcardbackuper.utils.copy.PhotoCopyManager;
import com.yifan.utils.base.BaseAsyncTask;
import com.yifan.utils.utils.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 文件备份异步任务
 *
 * Created by yifan on 2016/12/5.
 */
public class BackupTask extends BaseAsyncTask<Object, CopyProgress, CopyProgress> {

    public static final String TAG = "BackupTask";

    /**
     * 备份方式 - 文件
     */
    public static final String BACKUP_TYPE_FILE = "files";
    /**
     * 备份方式 - 图片
     */
    public static final String BACKUP_TYPE_PHOTO = "photoes";

    /**
     * 文件总数量
     */
    private long mFileTotalCount;

    /**
     * 是否为检查文件数量
     */
    private boolean isCheckCount;

    /**
     * 复制进度数据类
     */
    private CopyProgress mCopyProgress;

    /**
     * 文件总数
     */
    private long mTotalCount;

    /**
     * 已复制完文件数量
     */
    private long mCompletedCount;

    /**
     * 以DocumentProvider操作文件时的备份文件根目录对象
     */
    private DocumentFile mRootDirFile;

    /**
     * 判断是否使用SAF DocumentProvider操作文件
     */
    private boolean isSAFUseing;

    public BackupTask() {
        this.mCopyProgress = new CopyProgress();
    }

    @Override
    protected CopyProgress doInBackground(Object... objects) {
        if (null != objects && null != objects[0] && objects[0] instanceof String) {
            //目标文件夹路径
            String copyToPath = String.valueOf(objects[0]);
            //判断数据复制类型
            String type;
            if (objects.length >= 2 && null != objects[1] && objects[1] instanceof String) {
                type = String.valueOf(objects[1]);
            } else {
                type = BACKUP_TYPE_FILE;
            }
            //判断是否使用SAF框架操作文件
            if (objects.length >= 3 && null != objects[2] && objects[2] instanceof DocumentFile) {
                isSAFUseing = true;
                mRootDirFile = (DocumentFile) objects[2];
            }
            //根据类型取出相应数据
            FileTree fileTree;
            if (BACKUP_TYPE_PHOTO.equals(type)) {
                fileTree = PhotoCopyManager.getInstance().getFileTree();
            } else {
                fileTree = FileCopyManager.getInstance().getFileTree();
            }
            // 确定路径备份目标文件夹是否已建立
            String targetPath = null;
            if (isSAFUseing) {
                DocumentFile targetFile = mRootDirFile.findFile(Constants.FILE_BACKUP_ROOT_NAME);
                if (null == targetFile || !targetFile.exists()) {
                    mRootDirFile = mRootDirFile.createDirectory(Constants.FILE_BACKUP_ROOT_NAME);
                } else {
                    mRootDirFile = targetFile;
                }
            } else {
                File targetDir = new File(new StringBuilder(copyToPath).append(File.separator).append(Constants.FILE_BACKUP_ROOT_NAME).toString());
                if (!targetDir.exists()) {
                    targetDir.mkdirs();
                }
                targetPath = targetDir.getAbsolutePath();
            }
            // 第一遍先检查文件数量
            if (fileTree.nodes.size() > 0) {
                isCheckCount = true;
                while (true) {
                    // 开始遍历文件树
                    for (FileTreeNode node : fileTree.nodes) {
                        mFileTotalCount = iterateFileTree(node, mRootDirFile, targetPath, isCheckCount);
                    }
                    if (!isCheckCount) {
                        break;
                    }
                    isCheckCount = false;
                }
            }
            Log.i(TAG, "doInBackground: " + mFileTotalCount);
        }
        if (mCopyProgress.failList.size() > 0) {
            StringBuilder failLog = new StringBuilder();
            for (FailLog log : mCopyProgress.failList) {
                failLog.append(log.path).append("\n").append(log.cause).append("\n").append("\n");
            }
            FileUtils.printDataToFile("log", "FailLog_" + System.currentTimeMillis(), failLog.toString());
        }
        return mCopyProgress;
    }

    /**
     * 遍历文件
     *
     * @param fileTreeNode  文件树节点对象
     * @param rootTargetDir 目标根目录
     * @param isStatistics  是否为统计数量
     * @return 复制文件数量
     */
    private long iterateFileTree(FileTreeNode fileTreeNode, DocumentFile documentFile, String rootTargetDir, boolean isStatistics) {
        long filesCount = 0;
        if (null != fileTreeNode.path) {
            File file = new File(fileTreeNode.path);
            //判断源文件、文件夹是否存在
            if (file.exists()) {
                //判断是否为文件夹
                if (file.isDirectory()) {
                    //是的话在目标目录创建文件夹
                    if (isSAFUseing) {
                        createDir(file.getAbsolutePath(), documentFile, isStatistics);
                    } else {
                        createDir(new StringBuilder().append(rootTargetDir).append(file.getAbsolutePath()).toString(), documentFile, isStatistics);
                    }
                    //判断该文件节点下面是否还有文件
                    if (fileTreeNode.nodes.size() > 0) {//已添加节点
                        for (FileTreeNode treeNode : fileTreeNode.nodes) {
                            filesCount += iterateFileTree(treeNode, documentFile, rootTargetDir, isStatistics);
                        }
                    } else if (fileTreeNode.isSelectedDir) {//选中整个文件夹
                        File dir = new File(fileTreeNode.path);
                        String[] fileNames = dir.list();
                        if (null != fileNames && fileNames.length > 0) {
                            for (String name : fileNames) {
                                File temp = new File(new StringBuilder(fileTreeNode.path).append(File.separator).append(name).toString());
                                if (temp.exists()) {
                                    if (temp.isDirectory()) {
                                        FileTreeNode childNode = new FileTreeNode();
                                        childNode.path = temp.getAbsolutePath();
                                        childNode.name = temp.getName();
                                        childNode.isSelectedDir = true;
                                        childNode.parent = fileTreeNode;
                                        fileTreeNode.nodes.add(childNode);
                                        filesCount += iterateFileTree(childNode, documentFile, rootTargetDir, isStatistics);
                                    } else {
                                        if (isStatistics) {
                                            FileTreeNode childNode = new FileTreeNode();
                                            childNode.path = temp.getAbsolutePath();
                                            childNode.name = temp.getName();
                                            childNode.isSelectedDir = true;
                                            childNode.parent = fileTreeNode;
                                            fileTreeNode.nodes.add(childNode);
                                        }
                                        if (copyFile(temp.getAbsolutePath(), rootTargetDir, documentFile, isStatistics)) {
                                            filesCount++;
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    if (copyFile(fileTreeNode.path, rootTargetDir, documentFile, isStatistics)) {
                        filesCount++;
                    }
                }
            }
        }
//        Log.i(TAG, "iterate return: " + filesCount);
        return filesCount;
    }

    /**
     * 创建文件夹
     *
     * @param path         路径
     * @param isStatistics 是否统计，true:不创建；false:创建
     */
    private void createDir(String path, DocumentFile documentFile, boolean isStatistics) {
        if (isStatistics || null == documentFile) {
            return;
        }
        if (isSAFUseing) {
            String[] names = path.split(File.separator);
            DocumentFile targetDir = documentFile;
            for (String name : names) {
                if (!TextUtils.isEmpty(name)) {
                    DocumentFile temp = targetDir.findFile(name);
                    DocumentFile tempfirstUp = targetDir.findFile(name.substring(0, 1).toUpperCase() + name.substring(1));
                    DocumentFile tempAllUp = targetDir.findFile(name.toUpperCase());
                    DocumentFile tempAllLow = targetDir.findFile(name.toLowerCase());
                    boolean isExisted = (null != temp && temp.exists())
                            || (null != tempfirstUp && tempfirstUp.exists())
                            || (null != tempAllUp && tempAllUp.exists())
                            || (null != tempAllLow && tempAllLow.exists());
                    if (!isExisted) {
                        targetDir = targetDir.createDirectory(name);
                    } else {
                        if (null != temp) {
                            targetDir = temp;
                        } else if (null != tempfirstUp) {
                            targetDir = tempfirstUp;
                        } else if (null != tempAllUp) {
                            targetDir = tempAllUp;
                        } else if (null != tempAllLow) {
                            targetDir = tempAllLow;
                        }
                    }
                }
            }
        } else {
            File file = new File(path);
            if (!file.exists()) {
                file.mkdirs();
            }
        }
    }

    /**
     * 复制文件
     *
     * @param path         路径
     * @param targetPath   目标路径
     * @param documentFile 使用SAF框架时的目标文件夹
     * @param isStatistics 是否统计，true:不复制；false:复制
     * @return
     */
    private boolean copyFile(String path, String targetPath, DocumentFile documentFile, boolean isStatistics) {
        mCopyProgress.isStatistics = isStatistics;
        mCopyProgress.currentFilePath = targetPath;
        mCopyProgress.completedCount = mCompletedCount;
        publishProgress(mCopyProgress);
        if (isStatistics) {
            mTotalCount++;
            mCopyProgress.totalFileCount = mTotalCount;
            mCopyProgress.fileListStaticisces.add(path);
            return true;
        }
        boolean result = true;
        InputStream inputStream = null;
        OutputStream outputStream = null;
        long stratTime = System.currentTimeMillis();
        try {
            inputStream = new FileInputStream(path);
            if (isSAFUseing) {
                File file = new File(path);
                String[] paths = file.getParentFile().getAbsolutePath().split(File.separator);
                DocumentFile targetDir = documentFile;
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
                Log.i(TAG, "measure targetDir cost: " + (System.currentTimeMillis() - stratTime));
                stratTime = System.currentTimeMillis();
                DocumentFile targetFile = targetDir.findFile(file.getName());
                if (null != targetFile && targetFile.exists()) {
                    targetFile.delete();
                }
                targetFile = targetDir.createFile(com.yifan.sdcardbackuper.utils.FileUtils.getMimeType(new File(path)), file.getName());
                outputStream = ApplicationContext.getInstance().getContentResolver().openOutputStream(targetFile.getUri());
                Log.i(TAG, "create targetDir and getOpt cost: " + (System.currentTimeMillis() - stratTime));
            } else {
                File file = new File(new StringBuilder().append(targetPath).append(File.separator).append(path).toString());
                if (file.exists()) {
                    file.delete();
                }
                file.createNewFile();
                outputStream = new FileOutputStream(file);
            }
            stratTime = System.currentTimeMillis();
            byte bt[] = new byte[1024];
            int c;
            while ((c = inputStream.read(bt)) > 0) {
                outputStream.write(bt, 0, c);
            }
            Log.i(TAG, "copy to targetDir cost: " + (System.currentTimeMillis() - stratTime));
            mCopyProgress.fileListCopy.add(path);
            mCompletedCount++;
            mCopyProgress.completedCount = mCompletedCount;
            publishProgress(mCopyProgress);
        } catch (Exception e) {
            e.printStackTrace();
            mCopyProgress.failList.add(new FailLog(path, e.getMessage()));
            result = false;
        } finally {
            try {
                if (null != inputStream) {
                    inputStream.close();
                    inputStream = null;
                }
                if (null != outputStream) {
                    outputStream.close();
                    outputStream = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    @Override
    protected void onProgressUpdate(CopyProgress... values) {
        super.onProgressUpdate(values);
        if (null != getOnAsyncListener() && getOnAsyncListener() instanceof MainActivity.OnCopyListener) {
            ((MainActivity.OnCopyListener) getOnAsyncListener()).onUpdateProgress(values);
        }
    }
}
