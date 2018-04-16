package com.yifan.sdcardbackuper.task.backup;

import android.preference.PreferenceManager;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.yifan.sdcardbackuper.ApplicationContext;
import com.yifan.sdcardbackuper.model.CopyProgress;
import com.yifan.sdcardbackuper.model.FailLog;
import com.yifan.sdcardbackuper.model.FileTree;
import com.yifan.sdcardbackuper.model.FileTreeNode;
import com.yifan.sdcardbackuper.utils.Constants;
import com.yifan.sdcardbackuper.utils.FileUtils;
import com.yifan.sdcardbackuper.utils.copy.FileCopyManager;
import com.yifan.sdcardbackuper.utils.copy.PhotoCopyManager;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

/**
 * 备份文件 线程
 *
 * Created by wuyifan on 2017/4/21.
 */

public class BackupTask extends Thread {

    private static final String TAG = "BackupTask";

    /**
     * 备份参数类
     */
    private Backup mBackup;


    /**
     * 第一遍检查文件数量
     */
    private boolean isStatistics;

    /**
     * 是否正在复制
     */
    public static boolean isCopying;

    /**
     * 是否取消
     */
    private boolean isCancel;

    /**
     * 复制进度数据类
     */
    public static CopyProgress PROGRESS;

    /**
     * 是否跳过已存在的文件
     */
    public boolean isSkipExistedFiles;

    public BackupTask(Backup backup) {
        this.mBackup = backup;
        this.isStatistics = true;
        this.PROGRESS = new CopyProgress();
        this.isSkipExistedFiles = PreferenceManager.getDefaultSharedPreferences(
                ApplicationContext.getInstance()).getBoolean(Constants.KEY_PREFERENCES_SKIP_EXISTED_FILES,
                Constants.VALUE_PREFERENCES_SKIP_EXISTED_FILES);
    }

    @Override
    public void run() {
        Log.i(TAG, "run: ");
        EventBus.getDefault().post(new TaskStatus(TaskStatus.Status.START, PROGRESS));
        //根据类型取出相应数据
        FileTree fileTree;
        //判断复制类型，相册复制与文件复制厨房存放数组分开
        if (Backup.BACKUP_TYPE_PHOTO == this.mBackup.type) {
            fileTree = PhotoCopyManager.getInstance().getFileTree();
        } else {
            fileTree = FileCopyManager.getInstance().getFileTree();
        }
        // 确定路径备份目标文件夹是否已建立
        String targetPath = null;
        //判断是否使用SAF框架
        if (this.mBackup.isSAFUseing()) {
            DocumentFile targetFile = this.mBackup.rootDirFile.findFile(Constants.FILE_BACKUP_ROOT_NAME);
            if (null == targetFile || !targetFile.exists()) {
                this.mBackup.rootDirFile = this.mBackup.rootDirFile.createDirectory(Constants.FILE_BACKUP_ROOT_NAME);
            } else {
                this.mBackup.rootDirFile = targetFile;
            }
        } else {
            File targetDir = new File(new StringBuilder(this.mBackup.targetPath).append(File.separator).append(Constants.FILE_BACKUP_ROOT_NAME).toString());
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            targetPath = targetDir.getAbsolutePath();
        }
        // 第一遍先检查文件数量
        if (fileTree.nodes.size() > 0) {
            isStatistics = true;
            while (true) {
                // 开始遍历文件树
                for (FileTreeNode node : fileTree.nodes) {
                    iterateFileTree(node, this.mBackup.rootDirFile, targetPath, isStatistics);
                }
                if (!isStatistics) {
                    break;
                }
                isStatistics = false;
            }
        }
        //判断是否有复制失败的文件
        if (PROGRESS.failList.size() > 0) {
            StringBuilder failLog = new StringBuilder();
            for (FailLog log : PROGRESS.failList) {
                failLog.append(log.path).append("\n").append(log.cause).append("\n").append("\n");
            }
            com.yifan.utils.utils.FileUtils.printDataToFile("log", "FailLog_" + System.currentTimeMillis(), failLog.toString());
        }
        EventBus.getDefault().post(new TaskStatus(TaskStatus.Status.COMPLETED, PROGRESS));
        Log.i(TAG, "doInBackground: " + PROGRESS.totalFileCount);
    }

    /**
     * 遍历文件树
     *
     * @param fileTreeNode  节点对象
     * @param documentFile  SAF获取到的目标文件根路径
     * @param rootTargetDir 普通复制方式，目标文件夹路径
     * @param isStatistics  是否为检查数量
     */
    private void iterateFileTree(FileTreeNode fileTreeNode, DocumentFile documentFile, String rootTargetDir, boolean isStatistics) {
        if (null != fileTreeNode) {
            File file = new File(fileTreeNode.path);
            //判断源文件、文件夹是否存在
            if (file.exists()) {
                if (file.isDirectory()) {//文件夹
                    if (isStatistics & fileTreeNode.isSelectedDir) {//选中整个文件夹，统计时自动生成子节点并添加，然后取消该节点全选状态
                            //先清空子节点，防止重复添加
                            if (null != fileTreeNode.nodes) {
                                fileTreeNode.nodes.clear();
                            }
                            //打开路径，获取路径文件夹下的所有子文件名
                            File dir = new File(fileTreeNode.path);
                            String[] fileNames = dir.list();
                            //判断是否含有子文件
                            if (null != fileNames && fileNames.length > 0) {
                                //遍历子文件
                                for (String name : fileNames) {
                                    //取出子文件
                                    File temp = new File(new StringBuilder(fileTreeNode.path).append(File.separator).append(name).toString());
                                    if (temp.exists()) {
                                        FileTreeNode childNode = new FileTreeNode(
                                                temp.getName(), temp.getAbsolutePath(),
                                                fileTreeNode, true);
                                        fileTreeNode.nodes.add(childNode);
                                        if (temp.isDirectory()) {
                                            fileTreeNode.nodes.add(childNode);
                                            iterateFileTree(childNode, documentFile, rootTargetDir, isStatistics);
                                        } else {
                                            PROGRESS.totalFileCount++;
                                            PROGRESS.fileListStaticisces.add(temp.getAbsolutePath());
                                            EventBus.getDefault().post(PROGRESS);
                                        }
                                    }
                                }
                            }
                        fileTreeNode.isSelectedDir = false;
                    } else if (null != fileTreeNode.nodes && fileTreeNode.nodes.size() > 0) {//文件夹，遍历子文件
                        //正式复制时，自动创建路径文件夹
                        if (this.mBackup.isSAFUseing()) {
                            FileUtils.createDir(file.getAbsolutePath(), this.mBackup.isSAFUseing() ? documentFile : null, isStatistics);
                        } else {
                            FileUtils.createDir(new StringBuilder().append(rootTargetDir).append(file.getAbsolutePath()).toString(),
                                    this.mBackup.isSAFUseing() ? documentFile : null, isStatistics);
                        }
                        //遍历子文件
                        if (fileTreeNode.nodes.size() > 0) {
                            for (FileTreeNode subNode : fileTreeNode.nodes) {
                                iterateFileTree(subNode, documentFile, rootTargetDir, isStatistics);
                            }
                        }
                    }
                } else {//文件
                    if (isStatistics) {
                        PROGRESS.totalFileCount++;
                        PROGRESS.fileListStaticisces.add(fileTreeNode.path);
                        EventBus.getDefault().post(PROGRESS);
                    } else {
                        copyFile(fileTreeNode.path, rootTargetDir, documentFile);
                    }
                }

            }
        }
    }

    /**
     * 启用新线程复制文件
     *
     * @param path
     * @param targetPath
     * @param documentFile
     */
    private void copyFile(String path, String targetPath, DocumentFile documentFile) {
        BackupTask.PROGRESS.currentFilePath = path;
        EventBus.getDefault().post(PROGRESS);

        CopyTask task = new CopyTask(path, targetPath, documentFile);
        task.isSkipExistedFiles = isSkipExistedFiles;
        ThreadManager.getDefault().excuteAsync(task);
        isCopying = true;
        while (isCopying) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //每当文件复制完，休眠结束，继续下一个
        EventBus.getDefault().post(PROGRESS);
    }

    /**
     * 标记任务取消
     */
    public void cancel() {
        this.isCancel = true;
    }
}
