package com.yifan.sdcardbackuper.ui.main.photo;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.model.photo.PhotoGroup;
import com.yifan.sdcardbackuper.model.photo.PhotoGroupItem;
import com.yifan.sdcardbackuper.utils.image.ImageLoader;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.utils.base.widget.BaseRecyclerHolder;
import com.yifan.utils.utils.ResourcesUtils;

import java.util.List;

/**
 * 图片数据适配器
 *
 * Created by yifan on 2016/12/12.
 */
public class PhotoGroupAdapter extends BaseRecyclerAdapter<PhotoGroupAdapter.BasePhotoHolder> implements CompoundButton.OnCheckedChangeListener {

    /**
     * 图片集合
     */
    public static final int ITEM_TYPE_GROUP = 0x001;
    /**
     * 图片
     */
    public static final int ITEM_TYPE_PICTURE = 0x002;

    /**
     * 布局加载器
     */
    private LayoutInflater mLayoutInflater;

    /**
     * 数据源
     */
    private List mDatas;

    public PhotoGroupAdapter(List data) {
        this.mDatas = data;
    }

    @Override
    public BasePhotoHolder onCreate(ViewGroup parent, int viewType) {
        if (null == mLayoutInflater) {
            mLayoutInflater = LayoutInflater.from(parent.getContext());
        }
        View view = mLayoutInflater.inflate(R.layout.item_photo_group, parent, false);
        switch (viewType) {
            case ITEM_TYPE_PICTURE:
                return new PhotoItemHolder(view);
            default:
            case ITEM_TYPE_GROUP:
                return new PhotoGroupHolder(view);
        }
    }

    @Override
    public int getRealItemType(int position) {
        if (null != mDatas && position >= 0 && position <= mDatas.size()) {
            if (mDatas.get(position) instanceof PhotoGroup) {
                return ITEM_TYPE_GROUP;
            } else if (mDatas.get(position) instanceof PhotoGroupItem) {
                return ITEM_TYPE_PICTURE;
            }
        }
        return super.getRealItemType(position);
    }

    @Override
    public void onBind(BasePhotoHolder viewHolder, int realPosition) {
        viewHolder.setData(mDatas.get(realPosition), realPosition);
    }

    @Override
    public int getRealItemCount() {
        if (null == mDatas) {
            return 0;
        }
        return mDatas.size();
    }

    @Override
    public PhotoGroupHolder getFakeHolder(View view) {
        return null;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (null != getOnItemCheckedListener()) {
            int position = BaseRecyclerHolder.getPositionFromView(buttonView);
            if (position >= 0) {
                getOnItemCheckedListener().onItemChecked(buttonView, isChecked, position);
            }
        }
    }

    public class BasePhotoHolder extends BaseRecyclerHolder {
        public BasePhotoHolder(View itemView) {
            super(itemView);
        }

        public void setData(Object data, int position) {
        }
    }

    public class PhotoGroupHolder extends BasePhotoHolder {
        ImageView imageView;
        CheckBox checkBox;
        TextView textView;

        public PhotoGroupHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_photo_group_item);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_photo_group_item);
            textView = (TextView) itemView.findViewById(R.id.tv_photo_group_item);
        }

        public void setData(Object group, int position) {
            setPosition(checkBox, position);
            if (null != group && group instanceof PhotoGroup) {
                ImageLoader.getInstance().loadImage(imageView.getContext(), ((PhotoGroup) group).getItems().get(0).path, imageView);
                textView.setText(ResourcesUtils.getString(R.string.photo_group_size, String.valueOf(((PhotoGroup) group).getItems().size())));
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(((PhotoGroup) group).isChecked());
                checkBox.setOnCheckedChangeListener(PhotoGroupAdapter.this);
            }
        }
    }

    public class PhotoItemHolder extends BasePhotoHolder {
        ImageView imageView;
        CheckBox checkBox;

        public PhotoItemHolder(View itemView) {
            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.iv_photo_group_item);
            checkBox = (CheckBox) itemView.findViewById(R.id.cb_photo_group_item);
            itemView.findViewById(R.id.tv_photo_group_item).setVisibility(View.GONE);
        }

        public void setData(Object group, int position) {
            setPosition(checkBox, position);
            if (null != group && group instanceof PhotoGroupItem) {
                ImageLoader.getInstance().loadImage(imageView.getContext(), ((PhotoGroupItem) group).path, imageView);
                checkBox.setOnCheckedChangeListener(null);
                checkBox.setChecked(((PhotoGroupItem) group).isChecked());
                checkBox.setOnCheckedChangeListener(PhotoGroupAdapter.this);
            }
        }
    }

}
