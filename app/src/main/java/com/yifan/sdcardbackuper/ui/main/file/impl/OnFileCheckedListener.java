package com.yifan.sdcardbackuper.ui.main.file.impl;

import android.view.View;
import android.widget.Toast;

import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.model.FileItem;
import com.yifan.sdcardbackuper.model.FileTreeNode;
import com.yifan.sdcardbackuper.utils.copy.FileCopyManager;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;

import java.lang.ref.WeakReference;

/**
 * 文件列表页，文件选择监听器
 *
 * Created by yifan on 2016/11/18.
 */
public class OnFileCheckedListener implements BaseRecyclerAdapter.OnItemCheckedListener {

    private static final String TAG = "OnFileCheckedListener";
    /**
     * 界面引用，必须继承{@link FileCheckedImpl} 接口
     */
    private WeakReference<FileCheckedImpl> mContent;

    public OnFileCheckedListener(WeakReference<FileCheckedImpl> content) {
        this.mContent = content;
    }

    @Override
    public void onItemChecked(View view, boolean isChecked, int position) {
        if (null != mContent.get()) {
            FileItem item = mContent.get().getList().get(position);
            item.setChecked(isChecked);
            if (isChecked) {//选中
                int result = FileCopyManager.getInstance().addFile(item.getPath());
                switch (result) {
                    case FileTreeNode.ADD_SUCCESS:
//                        Toast.makeText(mContent.get().getContext(), "success", Toast.LENGTH_SHORT).show();
                        break;
                    case FileTreeNode.ADD_FAIL_HAS_EXISTED:
                        Toast.makeText(mContent.get().getContext(), R.string.add_fail_has_existed, Toast.LENGTH_SHORT).show();
                        break;
                    case FileTreeNode.ADD_FAIL_UNKNOWN:
                        Toast.makeText(mContent.get().getContext(), R.string.add_fail_unknown, Toast.LENGTH_SHORT).show();
                    default:
                        break;
                }
            } else {//取消选中
                if (FileCopyManager.getInstance().deleteFile(item.getPath())) {
//                    Toast.makeText(mContent.get().getContext(), "delete success", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContent.get().getContext(), R.string.delete_fail, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
