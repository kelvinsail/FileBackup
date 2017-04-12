package com.yifan.sdcardbackuper.model;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 文件树结构 - 节点
 *
 * Created by yifan on 2016/11/17.
 */
public class FileTreeNode {

    /**
     * 添加成功
     */
    public static final int ADD_SUCCESS = 0x001;
    /**
     * 添加失败 - 原因未知
     */
    public static final int ADD_FAIL_UNKNOWN = 0x002;
    /**
     * 添加失败 - 文件节点已存在
     */
    public static final int ADD_FAIL_HAS_EXISTED = 0x003;

    /**
     * 当前节点名字
     */
    public String name;

    /**
     * 节点路径
     */
    public String path;

    /**
     * 子节点数组
     */
    public List<FileTreeNode> nodes;

    /**
     * 父节点
     */
    public FileTreeNode parent;

    /**
     * 是否选中了一整个文件夹
     */
    public boolean isSelectedDir;

    public FileTreeNode() {
        this.nodes = new ArrayList<>();
    }

    /**
     * 添加一个节点
     *
     * @param path          路径
     * @param isSelectedDir 是否选中了一个文件夹
     * @return {@link FileTreeNode#ADD_FAIL_HAS_EXISTED}、{@link FileTreeNode#ADD_FAIL_UNKNOWN}、{@link FileTreeNode#ADD_SUCCESS}
     */
    public int addNode(String path, boolean isSelectedDir) {
        if (!TextUtils.isEmpty(path)) {
            if (path.startsWith(File.separator)) {
                path = path.substring(1, path.length());
            }
            //判断分隔符位置，以此来判断是否还有子节点
            int separatorIndex = path.indexOf(File.separator);
            String pathRoot;//当前节点目录名
            String nextNodePath;//下一节点路径
            if (separatorIndex > 0) {//还有下一个节点
                pathRoot = path.substring(0, separatorIndex);
                nextNodePath = path.substring(separatorIndex, path.length());
            } else {//没有下一个节点位置，pathRoot即为path，文件/夹名
                pathRoot = path;
                nextNodePath = null;
            }
            //查找节点是否存在
            if (null != nodes && nodes.size() > 0) {
                for (FileTreeNode node : nodes) {
                    if (node.name.equals(pathRoot)) {
                        if (null != nextNodePath) {//还有子节点
                            return node.addNode(nextNodePath, isSelectedDir);
                        } else {//没有子节点，即文件已存在
                            return ADD_FAIL_HAS_EXISTED;
                        }
                    }
                }
                //没有子节点、也不存在于当前树结构中
            }
            //添加新的节点
            FileTreeNode node = new FileTreeNode();
            node.name = pathRoot;
            node.parent = this;
            FileTreeNode parent = this;
            StringBuilder path_ = new StringBuilder(node.name);
            while (parent != null) {
                if (null != parent.name) {
                    path_.insert(0, File.separator).insert(0, parent.name);
                }
                parent = parent.parent;
            }
            node.path = path_.toString();
            nodes.add(node);
            if (null != nextNodePath) {//还有子节点
                return node.addNode(nextNodePath, isSelectedDir);
            } else {//没有子节点，文件节点添加成功
                node.isSelectedDir = isSelectedDir;//添加判断是否选中了一整个个文件夹
                return ADD_SUCCESS;
            }

        }
        return ADD_FAIL_UNKNOWN;
    }

    /**
     * 删除一个节点
     *
     * 先遍历查找到最底层的节点，从最底层开始删除，
     *
     * @param path
     * @return
     */
    public boolean deleteNode(String path) {
        if (!TextUtils.isEmpty(path) && null != nodes && nodes.size() > 0) {
            if (path.startsWith(File.separator)) {
                path = path.substring(1, path.length());
            }
            //判断分隔符位置，以此来判断是否还有子节点
            int separatorIndex = path.indexOf(File.separator);
            String pathRoot;//当前节点目录名
            String nextNodePath;//下一节点路径
            if (separatorIndex > 0) {//还有下一个节点
                pathRoot = path.substring(0, separatorIndex);
                nextNodePath = path.substring(separatorIndex, path.length());
            } else {//没有下一个节点位置，pathRoot即为path，文件/夹名
                pathRoot = path;
                nextNodePath = null;
            }
            //遍历查找是否有顶层节点
            for (FileTreeNode node : nodes) {
                if (node.name.equals(pathRoot)) {
                    if (TextUtils.isEmpty(nextNodePath)) {//没有子节点，该节点为最低处节点，删除
                        nodes.remove(node);
                        //如果子节点数量为0，且并非选中一整个文件夹，该节点可以删除
                        if (null != parent && nodes.size() == 0 && !isSelectedDir) {
                            return parent.deleteNode(this.name);
                        }
                        //删除一个子节点，将该节点的父节点设为非全选
                        node.parent.isSelectedDir = false;
                        return true;
                    } else {//还有子节点，往下传递
                        return node.deleteNode(nextNodePath);
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据路径判断节点是否存在
     *
     * @param path
     * @return
     */
    public boolean isNodeExisted(String path) {
        if (!TextUtils.isEmpty(path) && null != nodes && nodes.size() > 0) {
            if (path.startsWith(File.separator)) {
                path = path.substring(1, path.length());
            }
            //判断分隔符位置，以此来判断是否还有子节点
            int separatorIndex = path.indexOf(File.separator);
            String pathRoot;//当前节点目录名
            String nextNodePath;//下一节点路径
            if (separatorIndex > 0) {//还有下一个节点
                pathRoot = path.substring(0, separatorIndex);
                nextNodePath = path.substring(separatorIndex, path.length());
            } else {//没有下一个节点位置，pathRoot即为path，文件/夹名
                pathRoot = path;
                nextNodePath = null;
            }
            //遍历查找是否有子节点
            for (FileTreeNode node : nodes) {
                if (node.name.equals(pathRoot)) {
                    //找到顶层节点，继续查找子节点
                    if (TextUtils.isEmpty(nextNodePath)) {
                        return true;
                    } else {
                        return node.isNodeExisted(nextNodePath);
                    }
                }
            }
        }
        return false;
    }

    /**
     * 根据路径查找节点
     *
     * @param path
     * @return
     */
    public FileTreeNode findNodeByPath(String path) {
        if (!TextUtils.isEmpty(path) && null != nodes && nodes.size() > 0) {
            if (path.startsWith(File.separator)) {
                path = path.substring(1, path.length());
            }
            //判断分隔符位置，以此来判断是否还有子节点
            int separatorIndex = path.indexOf(File.separator);
            String pathRoot;//当前节点目录名
            String nextNodePath;//下一节点路径
            if (separatorIndex > 0) {//还有下一个节点
                pathRoot = path.substring(0, separatorIndex);
                nextNodePath = path.substring(separatorIndex, path.length());
            } else {//没有下一个节点位置，pathRoot即为path，文件/夹名
                pathRoot = path;
                nextNodePath = null;
            }
            //遍历查找是否有顶层节点
            for (FileTreeNode node : nodes) {
                if (node.name.equals(pathRoot)) {
                    //找到顶层节点，继续查找子节点
                    if (TextUtils.isEmpty(nextNodePath)) {
                        return node;
                    } else {
                        return node.findNodeByPath(nextNodePath);
                    }
                }
            }
        }
        return null;
    }


    /**
     * 获取所有节点的数量
     *
     * @return
     */
    public long getAllNodesCount() {
        long count = 0;
        if (nodes.size() > 0) {
            for (FileTreeNode node : nodes) {
                if (node.nodes.size() > 0) {
                    count += node.getAllNodesCount();
                } else {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * 清空数据
     */
    public void clearAll() {
        nodes.clear();
    }

}
