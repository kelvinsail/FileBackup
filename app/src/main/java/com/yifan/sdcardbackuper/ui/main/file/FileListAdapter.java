package com.yifan.sdcardbackuper.ui.main.file;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.model.FileItem;
import com.yifan.sdcardbackuper.utils.image.ImageLoader;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.utils.base.widget.BaseRecyclerHolder;

import java.io.File;
import java.util.List;

/**
 * Created by yifan on 2016/11/17.
 */
public class FileListAdapter extends BaseRecyclerAdapter<FileListAdapter.FileHolder>
        implements CompoundButton.OnCheckedChangeListener {

    /**
     * 布局适配器
     */
    private LayoutInflater mLayoutInflater;

    /**
     * 文件列表数据
     */
    private List<FileItem> mFileList;

    /**
     * 条目选中事件监听
     */
    public OnItemCheckedListener mItemCheckedListener;

    public FileListAdapter(List<FileItem> list) {
        this.mFileList = list;
    }

    @Override
    public FileHolder onCreate(ViewGroup parent, int viewType) {
        if (null == mLayoutInflater) {
            this.mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        return new FileHolder(mLayoutInflater.inflate(R.layout.item_file_list, parent, false));
    }

    @Override
    public void onBind(FileHolder viewHolder, int realPosition) {
        viewHolder.setData(realPosition, mFileList.get(realPosition));
    }

    @Override
    public int getRealItemCount() {
        return null != mFileList ? mFileList.size() : 0;
    }

    @Override
    public FileHolder getFakeHolder(View view) {
        return new FileHolder(view);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (null != buttonView) {
            int position = BaseRecyclerHolder.getPositionFromView(buttonView);
            if (position >= 0 && position < mFileList.size()) {
                if (null != mItemCheckedListener) {
                    mItemCheckedListener.onItemChecked(buttonView, buttonView.isChecked(), position);
                }
            }
        }
    }

    /**
     * 条目复用Holder
     */
    public class FileHolder extends BaseRecyclerHolder {

        CheckBox checkedView;
        TextView fileNameView;
        ImageView previewView;

        public FileHolder(View itemView) {
            super(itemView);
            checkedView = (CheckBox) itemView.findViewById(R.id.cb_item_file_list);
            fileNameView = (TextView) itemView.findViewById(R.id.tv_item_file_list);
            previewView = (ImageView) itemView.findViewById(R.id.iv_item_file_list_preview);
        }

        /**
         * 设置数据
         *
         * @param item
         */
        public void setData(int position, FileItem item) {
            setPosition(checkedView, position);
            fileNameView.setText(item.getName());
            checkedView.setOnCheckedChangeListener(null);
            checkedView.setChecked(item.isChecked());
            checkedView.setOnCheckedChangeListener(FileListAdapter.this);
            File file = new File(item.getPath());
            if (file.isFile()) {
                if (file.getName().toLowerCase().endsWith(".gif")
                        || file.getName().toLowerCase().endsWith(".bmp")
                        || file.getName().toLowerCase().endsWith(".jpg")
                        || file.getName().toLowerCase().endsWith(".jpeg")
                        || file.getName().toLowerCase().endsWith(".png")) {
                    ImageLoader.getInstance().loadImage(previewView.getContext(), file.getAbsolutePath(), previewView);
                } else if (file.getName().toLowerCase().endsWith(".mp3")
                        || file.getName().toLowerCase().endsWith(".wav")
                        || file.getName().toLowerCase().endsWith(".ogg")
                        || file.getName().toLowerCase().endsWith(".wma")) {
                    previewView.setImageResource(R.drawable.icon_music);
                } else if (file.getName().toLowerCase().endsWith(".apk")) {
                    previewView.setImageResource(R.drawable.icon_apk);
                } else {
                    previewView.setImageResource(R.drawable.icon_file);
                }
            } else {//文件夹
                previewView.setImageResource(R.drawable.icon_folder);
            }
        }
    }

    /**
     * 设置条目选中事件监听器
     *
     * @param onItemCheckedListener
     */
    public void setOnItemCheckedListener(OnItemCheckedListener onItemCheckedListener) {
        this.mItemCheckedListener = onItemCheckedListener;
    }
}
