package com.yifan.sdcardbackuper.task.backup;

import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.yifan.sdcardbackuper.model.FileTree;
import com.yifan.sdcardbackuper.model.FileTreeNode;
import com.yifan.sdcardbackuper.utils.Constants;
import com.yifan.sdcardbackuper.utils.copy.FileCopyManager;
import com.yifan.sdcardbackuper.utils.copy.PhotoCopyManager;

import java.io.File;

/**
 * 备份文件 线程
 *
 * Created by wuyifan on 2017/4/21.
 */

public class BackupTask implements Runnable {

    private static final String TAG = "BackupTask";

    /**
     * 备份方式 - 文件
     */
    public static final int BACKUP_TYPE_FILE = 0x101;
    /**
     * 备份方式 - 图片
     */
    public static final int BACKUP_TYPE_PHOTO = 0x102;

    /**
     * 目标路径
     */
    private String mTargetPath;

    /**
     * 被封类型
     */
    private int mType;

    /**
     * SAF 根文件对象
     */
    private DocumentFile mRootDirFile;

    /**
     * 是否使用SAF框架
     */
    private boolean isSAFUseing;

    /**
     * 第一遍检查文件数量
     */
    private boolean isCheckCount;

    /**
     * 文件总数
     */
    private long mFileTotalCount;


    public BackupTask(String targetPath, int type, DocumentFile rootFile) {
        this.mTargetPath = targetPath;
        this.mType = type;
        this.mRootDirFile = rootFile;
        this.isCheckCount = true;
    }

    @Override
    public void run() {
        Log.i(TAG, "run: ");
        //根据类型取出相应数据
        FileTree fileTree;
        //判断复制类型，相册复制与文件复制厨房存放数组分开
        if (BACKUP_TYPE_PHOTO == mType) {
            fileTree = PhotoCopyManager.getInstance().getFileTree();
        } else {
            fileTree = FileCopyManager.getInstance().getFileTree();
        }
        // 确定路径备份目标文件夹是否已建立
        String targetPath = null;
        //判断是否使用SAF框架
        if (isSAFUseing) {
            DocumentFile targetFile = mRootDirFile.findFile(Constants.FILE_BACKUP_ROOT_NAME);
            if (null == targetFile || !targetFile.exists()) {
                mRootDirFile = mRootDirFile.createDirectory(Constants.FILE_BACKUP_ROOT_NAME);
            } else {
                mRootDirFile = targetFile;
            }
        } else {
            File targetDir = new File(new StringBuilder(mTargetPath).append(File.separator).append(Constants.FILE_BACKUP_ROOT_NAME).toString());
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

    /**
     * 遍历文件树
     *
     * @param fileTreeNode 节点对象
     * @param documentFile SAF获取到的目标文件根路径
     * @param rootTargetDir 普通复制方式，目标文件夹路径
     * @param isStatistics 是否为检查数量
     * @return
     */
    private long iterateFileTree(FileTreeNode fileTreeNode, DocumentFile documentFile, String rootTargetDir, boolean isStatistics) {
        long count = 0;
        if (null != fileTreeNode) {
            if (null != fileTreeNode.nodes &&fileTreeNode.nodes.size()>0) {//文件夹，可能含有子文件
                if (fileTreeNode.nodes.size() > 0) {
                    for (FileTreeNode subNode : fileTreeNode.nodes) {
                        count += iterateFileTree(subNode, documentFile, rootTargetDir, isCheckCount);
                    }
                }
            } else if (fileTreeNode.isSelectedDir){//选中整个文件夹
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
                                count += iterateFileTree(childNode, documentFile, rootTargetDir, isStatistics);
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
                                   count++;
                                }
                            }
                        }
                    }
                }
            }else{//文件
                count =  1;
            }
        }
        return count;
    }

    private boolean copyFile(String absolutePath, String rootTargetDir, DocumentFile documentFile, boolean isStatistics) {
        return true;
    }
}
