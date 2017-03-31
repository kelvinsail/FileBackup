package com.yifan.sdcardbackuper.ui.main.photo;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.model.photo.PhotoGroupItem;
import com.yifan.sdcardbackuper.ui.main.info.InfoFragment;
import com.yifan.sdcardbackuper.utils.image.ImageLoader;
import com.yifan.utils.base.TitleBarFragment;
import com.yifan.utils.utils.Constant;
import com.yifan.utils.utils.ResourcesUtils;

import java.io.File;

import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * 图片预览界面
 *
 * Created by yifan on 2016/12/16.
 */
public class PhotoPreviewFragment extends TitleBarFragment {

    public static final String TAG = "PhotoPreviewFragment";

    /**
     * 图片数据
     */
    private PhotoGroupItem mItem;

    /**
     * 图片控件
     */
    private PhotoView mPhotoView;

    /**
     * gif图片显示控件,glide不支持第三方控件
     */
    private ImageView mImageView;

    /**
     * 缩放控制器
     */
    private PhotoViewAttacher mAttacher;

    @Override
    public String getTAG() {
        return TAG;
    }

    public PhotoPreviewFragment() {
    }

    public static PhotoPreviewFragment newInstance(PhotoGroupItem item) {
        Bundle args = new Bundle();
        args.putParcelable(Constant.DATA, item);
        PhotoPreviewFragment fragment = new PhotoPreviewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mItem = getArguments().getParcelable(Constant.DATA);
        setToolBarTranslaction(true);

        if (mItem.path.endsWith(".gif")) {
            mImageView = new ImageView(getActivity());
            mImageView.setBackgroundResource(R.color.background_black);
            setContentView(mImageView, R.style.NoElecationTheme, false);
        } else {
            mPhotoView = new PhotoView(getActivity());
            mPhotoView.setBackgroundResource(R.color.background_black);
            setContentView(mPhotoView, R.style.NoElecationTheme, false);
        }

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        if (!mItem.path.endsWith(".gif")) {
            inflater.inflate(R.menu.menu_photo_preview, menu);
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @SuppressWarnings("WrongConstant")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_set_wallpaper:
                try {
                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                        Uri uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, Integer.toString(mItem.id));
                        Intent intent = WallpaperManager.getInstance(getActivity()).getCropAndSetWallpaperIntent(uri);
                        if (getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.GET_ACTIVITIES).size() > 0) {
                            startActivity(Intent.createChooser(intent, ResourcesUtils.getString(R.string.photo_set_wallpaper)));
                            return true;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_ATTACH_DATA);
                intent.setDataAndType(Uri.fromFile(new File(mItem.path)), "image/*");
                intent.putExtra("mimeType", "image/*");
                intent.addFlags(1);
                startActivity(Intent.createChooser(intent, ResourcesUtils.getString(R.string.photo_set_wallpaper)));
                return true;
            case R.id.action_get_info:
                File file = new File(mItem.path);
                if (file.exists()) {
                    InfoFragment.newInstance(InfoFragment.INFO_TYPE_PHOTO, file.getAbsolutePath()).show(getFragmentManager(), InfoFragment.TAG);
                    return true;
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initView() {
        super.initView();
        if (null != mItem) {
            if (mItem.path.endsWith(".gif")) {
                ImageLoader.getInstance().loadImage(getActivity(), mItem.path, mImageView);
            } else {
                ImageLoader.getInstance().loadImage(getActivity(), mItem.path, mPhotoView);
                mAttacher = new PhotoViewAttacher(mPhotoView);
            }
        }
        getSupportTitleBar().setTitle("");
    }

}
