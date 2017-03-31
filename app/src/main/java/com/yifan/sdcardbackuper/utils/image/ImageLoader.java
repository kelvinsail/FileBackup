package com.yifan.sdcardbackuper.utils.image;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.yifan.sdcardbackuper.R;

/**
 * Created by yifan on 2016/12/13.
 */

public class ImageLoader {

    public static ImageLoader getInstance() {
        return LoaderInstance.mInstance;
    }

    private static class LoaderInstance {
        public static ImageLoader mInstance = new ImageLoader();
    }

    private ImageLoader() {
    }

    /**
     * 通过url或本地路径加载图片
     *
     * @param context
     * @param path
     * @param imageView
     */
    public void loadImage(Context context, String path, ImageView imageView) {
        loadImage(Glide.with(context), path, imageView);
    }

    /**
     * 通过url或本地路径加载图片
     *
     * @param activity
     * @param path
     * @param imageView
     */
    public void loadImage(Activity activity, String path, ImageView imageView) {
        loadImage(Glide.with(activity), path, imageView);
    }

    /**
     * 通过url或本地路径加载图片
     *
     * @param fragment
     * @param path
     * @param imageView
     */
    public void loadImage(Fragment fragment, String path, ImageView imageView) {
        loadImage(Glide.with(fragment), path, imageView);
    }

    private void loadImage(RequestManager manager, String path, ImageView imageView) {
        DrawableTypeRequest request = manager.load(path);
        request.placeholder(R.drawable.icon_polaroid);
        if (imageView.getScaleType() == ImageView.ScaleType.FIT_CENTER) {
            request.fitCenter();
        } else if (imageView.getScaleType() == ImageView.ScaleType.CENTER_CROP) {
            request.centerCrop();
        }
        request.into(imageView);
    }
}
