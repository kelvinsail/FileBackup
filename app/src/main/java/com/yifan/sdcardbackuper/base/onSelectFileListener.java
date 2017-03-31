package com.yifan.sdcardbackuper.base;

/**
 * 文件选择接口
 *
 * Created by yifan on 2016/11/17.
 */
public interface onSelectFileListener {

    /**
     * 文件选中
     *
     * @param isSelected true：选中;false：取消选中
     * @param path       路径
     */
    void onFileChoose(boolean isSelected, String path);

}
