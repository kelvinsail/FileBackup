package com.yifan.sdcardbackuper.ui.main.info;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.yifan.sdcardbackuper.R;
import com.yifan.sdcardbackuper.task.FolderLenghtStatisticsTask;
import com.yifan.sdcardbackuper.utils.FileUtils;
import com.yifan.utils.base.BaseAsyncTask;
import com.yifan.utils.base.BaseDialogFragment;
import com.yifan.utils.base.widget.BaseRecyclerAdapter;
import com.yifan.utils.base.widget.BaseRecyclerHolder;
import com.yifan.utils.utils.Constant;
import com.yifan.utils.utils.ResourcesUtils;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 文件属性展示 Fragment
 *
 * Created by yifan on 2016/12/20.
 */
public class InfoFragment extends BaseDialogFragment {

    public static final String TAG = "InfoFragment";

    /**
     * 展示文件属性
     */
    public static final int INFO_TYPE_FILE = 0x001;

    /**
     * 展示图片属性
     */
    public static final int INFO_TYPE_PHOTO = 0x002;

    /**
     * 列表页控件
     */
    private RecyclerView mListView;

    /**
     * 数据适配器
     */
    private InfoAdapter mAdapter;

    /**
     * 数据
     */
    private List<FileInfo> mDatas;

    /**
     * 文件夹大小统计 异步任务
     */
    private FolderLenghtStatisticsTask mTask;

    /**
     * 异步任务监听
     */
    private OnCountLenghtListener mListener;

    @Override
    public String getTAG() {
        return TAG;
    }

    public InfoFragment() {
    }

    public static InfoFragment newInstance(int type, String path) {
        Bundle args = new Bundle();
        args.putInt(Constant.TYPE, type);
        args.putString(Constant.DATA, path);
        InfoFragment fragment = new InfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mDatas = new ArrayList<>();
        File file = new File(getArguments().getString(Constant.DATA, ""));
        if (file.exists()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd a HH:mm:ss");

            switch (getArguments().getInt(Constant.TYPE, INFO_TYPE_FILE)) {
                case INFO_TYPE_PHOTO:
                    mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_photo_modify_time), sdf.format(file.lastModified())));
                    try {
//                        ExifInterface.TAG_ORIENTATION //旋转角度，整形表示，在ExifInterface中有常量对应表示
//                        ExifInterface.TAG_DATETIME //拍摄时间，取决于设备设置的时间
//                        ExifInterface.TAG_MAKE //设备品牌
//                        ExifInterface.TAG_MODEL //设备型号，整形表示，在ExifInterface中有常量对应表示
//                        ExifInterface.TAG_FLASH //闪光灯
//                        ExifInterface.TAG_IMAGE_LENGTH //图片高度
//                        ExifInterface.TAG_IMAGE_WIDTH //图片宽度
//                        ExifInterface.TAG_GPS_LATITUDE //纬度
//                        ExifInterface.TAG_GPS_LONGITUDE //经度
//                        ExifInterface.TAG_GPS_LATITUDE_REF //纬度名（N or S）
//                        ExifInterface.TAG_GPS_LONGITUDE_REF //经度名（E or W）
//                        ExifInterface.TAG_EXPOSURE_TIME //曝光时间
//                        ExifInterface.TAG_APERTURE //光圈值
//                        ExifInterface.TAG_ISO //ISO感光度
//                        ExifInterface.TAG_DATETIME_DIGITIZED //数字化时间
//                        ExifInterface.TAG_SUBSEC_TIME //
//                        ExifInterface.TAG_SUBSEC_TIME_ORIG //
//                        ExifInterface.TAG_SUBSEC_TIME_DIG //
//                        ExifInterface.TAG_GPS_ALTITUDE //海拔高度
//                        ExifInterface.TAG_GPS_ALTITUDE_REF //海拔高度
//                        ExifInterface.TAG_GPS_TIMESTAMP //时间戳
//                        ExifInterface.TAG_GPS_DATESTAMP //日期戳
//                        ExifInterface.TAG_WHITE_BALANCE //白平衡
//                        ExifInterface.TAG_FOCAL_LENGTH //焦距
//                        ExifInterface.TAG_GPS_PROCESSING_METHOD //用于定位查找的全球定位系统处理方法。

                        ExifInterface exifInterface = new ExifInterface(file.getAbsolutePath());
                        String takeTime = exifInterface.getAttribute(ExifInterface.TAG_DATETIME);

                        if (!TextUtils.isEmpty(takeTime)) {
                            SimpleDateFormat sdfOrign = new SimpleDateFormat("yyyy:MM:dd HH:mm:ss");
                            try {
                                Date date = sdfOrign.parse(takeTime);
                                if (null != date) {
                                    takeTime = sdf.format(date.getTime());
                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_photo_take_time), takeTime));
                        }
                        //获取图片尺寸
                        BitmapFactory.Options opts = new BitmapFactory.Options();
                        opts.inJustDecodeBounds = true;
                        BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                        opts.inSampleSize = 1;
                        opts.inJustDecodeBounds = false;
                        Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        bitmap.recycle();
                        mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_photo_info),
                                new StringBuilder(file.getName()).append("\n").append(FileUtils.formatFileLenght(file.length()))
                                        .append(" ").append(width).append("x").append(height).append("px").toString()));
                        //拍摄参数
                        StringBuilder info = new StringBuilder();
                        String model = exifInterface.getAttribute(ExifInterface.TAG_MODEL);//型号
                        String make = exifInterface.getAttribute(ExifInterface.TAG_MAKE);//产商
                        String aperture = exifInterface.getAttribute(ExifInterface.TAG_F_NUMBER);//光圈
                        double exposureTime = exifInterface.getAttributeDouble(ExifInterface.TAG_EXPOSURE_TIME, 0d);//快门曝光时间
                        int iso = exifInterface.getAttributeInt(ExifInterface.TAG_ISO_SPEED_RATINGS, 0);//ISO
                        String focalLength = exifInterface.getAttribute(ExifInterface.TAG_FOCAL_LENGTH);//焦距
                        int flash = exifInterface.getAttributeInt(ExifInterface.TAG_FLASH, 0);//焦距
                        if (!TextUtils.isEmpty(model)) {
                            info.append(model).append(", ");
                        }
                        if (!TextUtils.isEmpty(make)) {
                            info.append(make);
                        }
                        if (info.length() > 0) {
                            info.append("\n");
                        }
                        if (!TextUtils.isEmpty(aperture)) {
                            info.append("f/").append(aperture).append(" ");
                        }
                        if (exposureTime > 0) {
                            info.append("1/").append((int) (1 / exposureTime)).append(" ");
                        }
                        if (iso > 0) {
                            info.append("ISO").append(iso).append("\n");
                        }
                        if (!TextUtils.isEmpty(focalLength) && focalLength.indexOf("/") > 0) {
                            String[] string = focalLength.split("/");
                            double value = 0;
                            if (null != string && string.length == 2) {
                                value = new BigDecimal(string[0]).divide(new BigDecimal(string[1]), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
                            }
                            if (value > 0) {
                                info.append(value).append("mm").append(" ");
                            } else {
                                info.append(focalLength).append(" ");
                            }
                        }
                        if (info.length() > 0) {
                            info.append(ResourcesUtils.getString(flash == 16 ? R.string.property_photo_has_no_flash : R.string.property_photo_has_flash));
                        }
                        if (!TextUtils.isEmpty(info.toString())) {
                            mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_photo_take_info), info.toString()));
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_photo_path), file.getAbsolutePath()));
                    break;
                case INFO_TYPE_FILE:
                    mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_path), file.getAbsolutePath()));
                    //获取文件、文件夹大小
                    FileInfo info = new FileInfo(ResourcesUtils.getString(R.string.property_lenght), String.valueOf(0));
                    mDatas.add(info);
                    if (null != mTask) {
                        mTask.cancel(true);
                        mTask = null;
                    }
                    if (null == mListener) {
                        mListener = new OnCountLenghtListener(new WeakReference<>(this));
                    }
                    mTask = new FolderLenghtStatisticsTask();
                    mTask.setOnAsyncListener(mListener);
                    mTask.asyncExecute(file.getAbsolutePath(), info);
                    mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_time), sdf.format(file.lastModified())));
                    mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_read_able), String.valueOf(file.canRead())));
                    mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_write_able), String.valueOf(file.canWrite())));
                    mDatas.add(new FileInfo(ResourcesUtils.getString(R.string.property_is_hidden), String.valueOf(file.isHidden())));
                    break;
            }
        } else {
            dismiss();
        }
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (null != mTask) {
            mTask.cancel(true);
            mTask = null;
        }
    }

    @Override
    public int getLayoutResID() {
        return R.layout.dialog_file_info;
    }

    @Override
    public void initView() {
        super.initView();
        mListView = (RecyclerView) getRootView().findViewById(R.id.rv_dialog_file_info);
        mListView.getLayoutParams().width = (int) (ResourcesUtils.getDisplayMetrics().widthPixels * 0.95);
        mListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mAdapter = new InfoAdapter();
        mListView.setAdapter(mAdapter);
    }

    /**
     * 数据适配器
     */
    private class InfoAdapter extends BaseRecyclerAdapter<InfoAdapter.InfoHolder> {

        LayoutInflater mLayoutInflater;

        public InfoAdapter() {
        }

        @Override
        public InfoHolder onCreate(ViewGroup parent, int viewType) {
            if (null == mLayoutInflater) {
                mLayoutInflater = LayoutInflater.from(parent.getContext());
            }
            return new InfoHolder(mLayoutInflater.inflate(R.layout.item_file_info, parent, false));
        }

        @Override
        public void onBind(InfoHolder viewHolder, int realPosition) {
            viewHolder.setData(mDatas.get(realPosition));
        }

        @Override
        public int getRealItemCount() {
            return mDatas.size();
        }

        @Override
        public InfoHolder getFakeHolder(View view) {
            return new InfoHolder(view);
        }

        class InfoHolder extends BaseRecyclerHolder {

            TextView name;
            TextView value;

            public InfoHolder(View itemView) {
                super(itemView);
                name = (TextView) itemView.findViewById(R.id.tv_item_file_info_name);
                value = (TextView) itemView.findViewById(R.id.tv_item_file_info_value);
            }

            public void setData(FileInfo info) {
                name.setText(info.name);
                value.setText(info.value);
            }
        }
    }

    /**
     * 长度统计异步任务监听
     */
    public static class OnCountLenghtListener implements BaseAsyncTask.OnAsyncListener {

        private WeakReference<InfoFragment> mFragment;

        public OnCountLenghtListener(WeakReference<InfoFragment> mFragment) {
            this.mFragment = mFragment;
        }

        @Override
        public void onAsyncSuccess(Object data) {

        }

        @Override
        public void onAsyncFail() {

        }

        @Override
        public void onAsyncCancelled() {

        }

        @Override
        public void onAsyncStart() {

        }

        @Override
        public void onAsyncCompleted() {
            if (null != mFragment.get()) {
                mFragment.get().mTask = null;
            }
        }

        public void onProgressUpdate(InfoFragment.FileInfo... info) {
            if (null != mFragment.get() && null != mFragment.get().getActivity() && null != info && info.length > 0) {
                mFragment.get().getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mFragment.get().mAdapter.notifyDataSetChanged();
                    }
                });
            }
        }
    }

    /**
     * 文件属性
     */
    public class FileInfo {

        /**
         * 属性名
         */
        public String name;

        /**
         * 属性值
         */
        public String value;

        public FileInfo(String name, String value) {
            this.name = name;
            this.value = value;
        }
    }
}
