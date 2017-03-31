package com.yifan.sdcardbackuper.utils.copy;

import android.text.TextUtils;

import com.yifan.sdcardbackuper.model.FileCopyItem;
import com.yifan.sdcardbackuper.model.FileTree;
import com.yifan.sdcardbackuper.model.FileTreeNode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件复制管理工基类
 *
 * Created by yifan on 2016/12/12.
 */
public class BaseCopyManager {


    /**
     * 是否正在拷贝
     */
    public boolean isCopying;

    /**
     * 待拷贝文件数据
     */
    private FileTree mFileTree;

    /**
     * 复制失败列表
     */
    private List<FileCopyItem> mFailCopies;

    public BaseCopyManager() {
        this.mFileTree = new FileTree();
        this.mFailCopies = new ArrayList<>();
    }

    /**
     * 获取文件树
     *
     * @return
     */
    public FileTree getFileTree() {
        return mFileTree;
    }

    /**
     * 添加一个待复制的文件
     *
     * @param path
     * @return
     */
    public int addFile(String path) {
        int result = FileTreeNode.ADD_FAIL_UNKNOWN;
        if (!TextUtils.isEmpty(path)) {
            //判断源文件是否存在
            File file = new File(path);
            if (file.exists()) {//文件存在，继续执行
                //添加文件
                result = mFileTree.addNode(path, file.isDirectory());
                if (result != FileTreeNode.ADD_SUCCESS) {
                    mFailCopies.add(new FileCopyItem(path));
                }
            }
        }
        return result;
    }

    /**
     * 删除待复制的文件记录
     *
     * @param path
     * @return
     */
    public boolean deleteFile(String path) {
        return mFileTree.deleteNode(path);
    }

    /**
     * 根据路径判断文件树中相应节点知否存在
     *
     * @param path
     * @return
     */
    public boolean isFileExisted(String path) {
        return mFileTree.isNodeExisted(path);
    }

    /**
     * 通过路径查找节点
     *
     * @param path
     * @return
     */
    public FileTreeNode findNodeByPath(String path) {
        return mFileTree.findNodeByPath(path);
    }

    /**
     * 获取待复制的节点的数量
     *
     * @return
     */
    public long getAllNodeCount() {
        long count = 0;
        if (mFileTree.nodes.size() > 0) {
            for (FileTreeNode node : mFileTree.nodes) {
                count += node.getAllNodesCount();
            }
        }
        return count;
    }
}
