package com.like.app.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * 作者：like on 2019-06-11 21:15
 * <p>
 * 邮箱：like@tydic.com
 * <p>
 * 描述：应用管理类
 */
public class AppManager {

    /**
     * 获取所有已经安装应用信息
     * @param context
     * @return
     */
    public static List<PackageInfo> getPackageInfoList(Context context){
        PackageManager packageManager = context.getPackageManager();
        return packageManager.getInstalledPackages(0);
    }

    /**
     * 检测当前应用是否安装
     * @param context
     * @param packageName
     * @return
     */
    public static boolean isAvilible(Context context ,String packageName){
        List<PackageInfo> packageInfoList = getPackageInfoList(context);
        for (PackageInfo info :packageInfoList) {
            if (info.packageName.equalsIgnoreCase(packageName)){
                return true;
            }
        }
        return false;
    }

    /**
     * 检测当前应用版本
     * @param context
     * @param packageName
     * @return -1 表示未安装 其他表示版本号
     */
    public static String checkVersion(Context context , String packageName){
        List<PackageInfo> packageInfoList = getPackageInfoList(context);
        for (PackageInfo info :packageInfoList) {
            if (info.packageName.equalsIgnoreCase(packageName)){
                return info.versionName;
            }
        }
        return "-1";
    }

}
