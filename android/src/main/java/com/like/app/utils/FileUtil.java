package com.like.app.utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 作者：like on 2019-06-13 08:59
 * <p>
 * 邮箱：like@tydic.com
 * <p>
 * 描述：文件管理器
 */
public class FileUtil {

    public static String getInnerSDCardPath(){
        return Environment.getExternalStorageDirectory().getPath();
    }

    /**
     * 删除文件夹
     *
     * @param file
     */
    public static void delete(File file) {
        if (file.isFile()) {
            file.delete();
            return;
        }
        if (file.isDirectory()) {
            File[] childFiles = file.listFiles();
            if (childFiles == null || childFiles.length == 0) {
                file.delete();
                return;
            }
            for (int i = 0; i < childFiles.length; i++) {
                delete(childFiles[i]);
            }
            Log.d("delete", "正在删除....");
            file.delete();
        }
    }


    /**
     * 文件夹管理
     *
     * @param path
     *            文件路径
     */
    public static void forceMkdir(String path) {
        File dir = new File(path);
        // 判断文件夹是否存在 如果不存在创建文件夹
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * 遍历指定后缀名文件
     *
     * @param path
     * @param extension
     */
    public static List<String> getFiles(String path, String extension,
                                        Boolean isIterative) {
        List<String> filePathList = new ArrayList<String>();
        File[] files = new File(path).listFiles();
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                File f = files[i];
                if (!f.isDirectory()) {
                    String pextension = f.getPath().substring(
                            f.getPath().length() - extension.length());
                    if (pextension.equals(extension)) {
                        filePathList.add(f.getPath());
                        if (!isIterative)
                            break;
                    }
                } else if (f.isDirectory() && f.getPath().indexOf("/.") == -1) {
                    getFiles(f.getPath(), extension, isIterative);
                }
            }
        }
        return filePathList;
    }

}
