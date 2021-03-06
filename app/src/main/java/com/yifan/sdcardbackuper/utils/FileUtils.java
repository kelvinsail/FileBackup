package com.yifan.sdcardbackuper.utils;

import android.media.MediaMetadataRetriever;
import android.support.v4.provider.DocumentFile;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.Locale;

/**
 * Created by yifan on 2016/11/17.
 */
public class FileUtils {

//    public static int[] copyFile(String filePath, String toPath) {
//        int[] result = new int[2];
//        File file = new File(filePath);
//        if (file.exists() && file.canRead()) {
//            FileOutputStream fos = null;
//            FileInputStream fis = null;
//            try {
//                fis = new FileInputStream(file);
//                fos = new FileOutputStream(toPath);
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } finally {
//                CloseUtils.close(fos);
//            }
//        }
//        return result;
//    }

    //    /**
//     * 获取文件的MIME类型
//     *
//     * @param filePath
//     * @return
//     */
//    public static String getMimeType(String filePath) {
//        MediaMetadataRetriever mmr = new MediaMetadataRetriever();
//        String mime = "text/plain";
//        if (filePath != null) {
//            try {
//                mmr.setDataSource(filePath);
//                mime = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_MIMETYPE);
//            } catch (IllegalStateException e) {
//                return mime;
//            } catch (IllegalArgumentException e) {
//                return mime;
//            } catch (RuntimeException e) {
//                return mime;
//            }
//        }
//        return mime;
//    }
//
    private static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    /**
     * 获取文件的MIME类型
     *
     * @param file
     * @return
     */
    public static String getMimeType(File file) {
        String suffix = getSuffix(file);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (null != type && !type.isEmpty()) {
            return type;
        }
        return "file/*";
    }

    /**
     * 获取文件大小
     *
     * @param file
     * @return
     */
    public static String getFileLenght(File file) {
        String lenghtFormat = formatFileLenght(0);
        if (null != file && file.exists()) {
            lenghtFormat = formatFileLenght(file.length());
        }
        return lenghtFormat;
    }

    /**
     * 格式化文件大小
     *
     * @param lenght
     * @return
     */
    public static String formatFileLenght(long lenght) {
        String lenghtFormat;
        if (lenght >= 1024 * 1024) {
            lenghtFormat = new StringBuilder(String.valueOf(new BigDecimal(lenght).divide(new BigDecimal(1024 * 1024), 2, BigDecimal.ROUND_HALF_UP))).append(" MB").toString();
        } else {
            lenghtFormat = new StringBuilder(String.valueOf(new BigDecimal(lenght).divide(new BigDecimal(1024), 0, BigDecimal.ROUND_HALF_UP))).append(" KB").toString();
        }
        return lenghtFormat;
    }

    /**
     * 根据长度对比两个同名文件是否一样
     *
     * @param originFile
     * @param targetFile
     * @return
     */
    public static boolean compareTwoFiles(File originFile, Object targetFile) {
        if (null != originFile && originFile.exists() && null != targetFile) {
            if (targetFile instanceof DocumentFile
                    && ((DocumentFile) targetFile).exists()) {
                return originFile.getName().equals(((DocumentFile) targetFile).getName())
                        && originFile.length() == ((DocumentFile) targetFile).length();
            } else if (targetFile instanceof File
                    && ((File) targetFile).exists()) {
                return originFile.getName().equals(((File) targetFile).getName())
                        && originFile.length() == ((File) targetFile).length();
            }
        }
        return false;
    }
}
