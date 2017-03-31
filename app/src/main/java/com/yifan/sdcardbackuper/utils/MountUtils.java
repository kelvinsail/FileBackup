package com.yifan.sdcardbackuper.utils;

import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;
import android.text.format.Formatter;
import android.util.Log;

import com.yifan.sdcardbackuper.ApplicationContext;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 挂载点判断工具
 *
 * Created by yifan on 2016/11/15.
 */
public class MountUtils {

    private final static String TAG = "MountUtils";

    /**
     * VolumeInfo.TYPE_PUBLIC 标识可移除设备
     */
    public static final int TYPE_PUBLIC = 0;

    public static final int STATE_UNMOUNTED = 0;
    public static final int STATE_CHECKING = 1;
    public static final int STATE_MOUNTED = 2;

    /**
     * 构造方法
     */
    private MountUtils() {
    }

    private static class Instances {

        public static MountUtils mInstance = new MountUtils();
    }

    /**
     * 获取实例，不兼容系统版本时抛出异常 {@link UnSupportException}
     */
    public static MountUtils getInstance() throws UnSupportException {
        if (Build.VERSION_CODES.ICE_CREAM_SANDWICH <= Build.VERSION.SDK_INT) {
            return Instances.mInstance;
        }
        //不兼容，抛出异常
        throw new UnSupportException();

    }

    /**
     * 获取所有挂载点信息
     *
     * @param isAllMounted            是否只获取已挂载的分区
     * @param isIgnoreExternalStorage 是否忽略内部储存器
     * @return
     */
    public List<MountPoint> getAllMountPoints(boolean isAllMounted, boolean isIgnoreExternalStorage) { //所有挂载点数据
        List<MountPoint> result = new ArrayList<>();
        try {
            //获取StorageManager管理器实例
            StorageManager sm = (StorageManager) ApplicationContext.getInstance().getSystemService(Context.STORAGE_SERVICE);
            //反射StorageManager类
            Class class_StorageManager = StorageManager.class;
            //判断api是否大于等于23
            //是的话getVolumes
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Method method_getVolumes = class_StorageManager.getMethod("getVolumes");
                Class class_VolumeInfo = Class.forName("android.os.storage.VolumeInfo");
                Method method_getType = class_VolumeInfo.getMethod("getType");
                Method method_getDisk = class_VolumeInfo.getMethod("getDisk");
                Method method_getPath = class_VolumeInfo.getMethod("getPath");
//                Method method_getInternalPath = class_VolumeInfo.getMethod("getInternalPath");
                Method method_getState = class_VolumeInfo.getMethod("getState");
                Class class_DiskInfo = Class.forName("android.os.storage.DiskInfo");
                Method method_isUsb = class_DiskInfo.getMethod("isUsb");

                //获取所有挂载点的对象
                List objArray = (List) method_getVolumes.invoke(sm);
                for (Object vol : objArray) {
                    int type = (Integer) method_getType.invoke(vol);
                    int state = (Integer) method_getState.invoke(vol);
                    if (type == TYPE_PUBLIC) {//6.0中外置sd和usb标示为公共的
                        Object disk = method_getDisk.invoke(vol);
                        if (null != disk) {
                            boolean isUsb = (boolean) method_isUsb.invoke(disk);
                            Field[] fields = disk.getClass().getDeclaredFields();
                            String label = null;
                            for (Field field : fields) {
                                if ("label".equals(field.getName())) {
                                    label = (String) field.get(disk);
                                }
                            }
                            Log.i(TAG, "getAllMountPoints: " + label);
                            boolean isMounted = state == STATE_MOUNTED;
                            File file = (File) method_getPath.invoke(vol);
                            Log.i(TAG, "getAllMountPoints: " + file.getAbsolutePath());
                            //如果是获取所有已挂载点，未挂载的则不加入数组
                            if (isAllMounted ? isMounted : true) {
                                MountPoint point = new MountPoint(file, true, isMounted);
                                point.setLabel(label);
                                point.setTotalSize(getTotalSize(point.getFile().getAbsolutePath()));//获取挂载点容量总大小
                                point.setAvailableSize(getAvailableSize(point.getFile().getAbsolutePath()));//获取挂载点可用大小
                                result.add(point);
                            }
                        }
                    }
                }

            } else {//否的话继续调用getVolumeList
                //getVolumeList方法,获取挂载点列表
                Method method_getVolumeList = class_StorageManager.getMethod("getVolumeList");
                //getVolumeState，获取挂载点状态
                Method method_getVolumeState = class_StorageManager
                        .getMethod("getVolumeState", String.class);
                //挂载点信息类，部分函数隐藏，得通过反射来调用
                Class class_StorageVolume = Class.forName("android.os.storage.StorageVolume");
                Method method_isRemovable = class_StorageVolume.getMethod("isRemovable");
                Method method_getPath = class_StorageVolume.getMethod("getPath");
                Method method_getPathFile = null;
                // 自API16 StorageVolume方法中没有getPathFile
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                    method_getPathFile = class_StorageVolume.getMethod("getPathFile");
                }
                //获取所有挂载点的对象
                Object[] objArray = (Object[]) method_getVolumeList.invoke(sm);
                for (Object value : objArray) {
                    String path = (String) method_getPath.invoke(value);
                    File file;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                        file = (File) method_getPathFile.invoke(value);
                    } else {
                        file = new File(path);
                    }
                    //是否可移除，可移除即为sdcard或外置U盘
                    boolean isRemovable = (boolean) method_isRemovable.invoke(value);
                    //是否已挂载
                    boolean isMounted;
                    String getVolumeState = (String) method_getVolumeState.invoke(sm, path);//获取挂载状态。
                    if (getVolumeState.equals(Environment.MEDIA_MOUNTED)) {
                        isMounted = true;
                    } else {
                        isMounted = false;
                    }
                    //如果是获取所有已挂载点，未挂载的则不加入数组
                    if ((isAllMounted ? isMounted : true) && (isIgnoreExternalStorage ? isRemovable : true)) {
                        MountPoint point = new MountPoint(file, isRemovable, isMounted);
                        point.setTotalSize(getTotalSize(point.getFile().getPath()));//获取挂载点容量总大小
                        point.setAvailableSize(getAvailableSize(point.getFile().getPath()));//获取挂载点可用大小
                        result.add(point);
                    }
                }
            }
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取已挂在的分区信息
     *
     * @return
     */
    public List<MountPoint> getMountedPoints() {
        return getAllMountPoints(true, false);
    }

    /**
     * 获得容量总大小
     *
     * @return
     */
    private String getTotalSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize;
        long totalBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            totalBlocks = stat.getBlockCountLong();
        } else {
            blockSize = stat.getBlockSize();
            totalBlocks = stat.getBlockCount();
        }
        return Formatter.formatFileSize(ApplicationContext.getInstance(), blockSize * totalBlocks);
    }

    /**
     * 获得剩余容量，即可用大小
     *
     * @return
     */
    private String getAvailableSize(String path) {
        StatFs stat = new StatFs(path);
        long blockSize;
        long availableBlocks;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            blockSize = stat.getBlockSizeLong();
            availableBlocks = stat.getAvailableBlocksLong();
        } else {
            blockSize = stat.getBlockSize();
            availableBlocks = stat.getAvailableBlocks();
        }
        return Formatter.formatFileSize(ApplicationContext.getInstance(), blockSize * availableBlocks);
    }

    /**
     * 挂载点数据类
     */
    public class MountPoint {

        /**
         * 设备标签
         */
        private String label;
        /**
         * 文件对象
         */
        private File file;
        /**
         * 用于判断是否为内置存储卡，如果为true就是代表本挂载点可以移除，就是外置存储卡，否则反之
         */
        private boolean isRemovable;
        /**
         * 用于标示，这段代码执行的时候这个出处卡是否处于挂载状态，如果是为true，否则反之
         */
        private boolean isMounted;

        private String totalSize;
        private String availableSize;

        public MountPoint(File file, boolean isRemovable, boolean isMounted) {
            this.file = file;
            this.isMounted = isMounted;
            this.isRemovable = isRemovable;
        }

        public File getFile() {
            return file;
        }

        public boolean isRemovable() {
            return isRemovable;
        }

        public boolean isMounted() {
            return isMounted;
        }


        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public void setFile(File file) {
            this.file = file;
        }

        public void setRemovable(boolean removable) {
            isRemovable = removable;
        }

        public void setMounted(boolean mounted) {
            isMounted = mounted;
        }

        public String getTotalSize() {
            return totalSize;
        }

        public void setTotalSize(String totalSize) {
            this.totalSize = totalSize;
        }

        public String getAvailableSize() {
            return availableSize;
        }

        public void setAvailableSize(String availableSize) {
            this.availableSize = availableSize;
        }
    }
}
