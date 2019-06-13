
package com.like.app.manager;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.like.app.utils.AppManager;
import com.like.app.utils.FileUtil;

import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class RNAppManagerModule extends ReactContextBaseJavaModule {

    private final ReactApplicationContext reactContext;



    public RNAppManagerModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        x.Ext.init((Application) reactContext.getApplicationContext());

        AppManagerBroadcastReceiver appManagerBroadcastReceiver = new AppManagerBroadcastReceiver(reactContext);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.PACKAGE_ADDED");
        intentFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        intentFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        intentFilter.addDataScheme("package");
        reactContext.registerReceiver(appManagerBroadcastReceiver,intentFilter);

    }



    @Nullable
    @Override
    public Map<String, Object> getConstants() {
        Map<String,Object> constants = new HashMap<>();
        constants.put("sdPath", FileUtil.getInnerSDCardPath());
        constants.put("WAITING",Constant.WAITING);
        constants.put("STARTED",Constant.STARTED);
        constants.put("LOADING",Constant.LOADING);
        constants.put("SUCCESS",Constant.SUCCESS);
        constants.put("FINISHED",Constant.FINISHED);
        constants.put("CANCEL",Constant.CANCEL);
        constants.put("ERROR",Constant.ERROR);
        constants.put("INSTALL",Constant.INSTALL);
        constants.put("UNINSTALL",Constant.UNINSTALL);
        constants.put("UPDATE",Constant.UPDATE);
        return constants;
    }

    @Override
    public String getName() {
        return "RNAppManager";
    }

    /**
     * 检测应用是否安装
     *
     * @param packageName
     * @param callback
     */
    @ReactMethod
    public void checkAppIsInstall(String packageName, Callback callback) {
        boolean isAvilible = AppManager.isAvilible(reactContext, packageName);
        callback.invoke(isAvilible);
    }

    /**
     * 获取已经安装应用所有信息
     *
     * @param callback
     */
    @ReactMethod
    public void packageInfoList(Callback callback) {
        List<PackageInfo> packageInfoList = AppManager.getPackageInfoList(reactContext);
        WritableArray writableArray = Arguments.createArray();
        for (PackageInfo info : packageInfoList) {
            WritableMap writableMap = Arguments.createMap();
            writableMap.putString("packageName", info.packageName);
            writableMap.putString("versionName", info.versionName);
            writableMap.putInt("versionCode", info.versionCode);
            writableMap.putString("firstInstallTime", info.firstInstallTime + "");
            writableMap.putString("lastUpdateTime", info.lastUpdateTime + "");
            writableArray.pushMap(writableMap);
        }
        callback.invoke(writableArray);
    }

    /**
     * 版本检测
     *
     * @param packageName
     * @param callback
     */
    @ReactMethod
    public void checkVersion(String packageName, Callback callback) {
        String version = AppManager.checkVersion(reactContext, packageName);
        callback.invoke(version);
    }

    /**
     * 应用下载
     *
     * @param url
     * @param downloadPath
     */
    @ReactMethod
    public void appDownload(String url, String downloadPath , final boolean autoInstall) {
        RequestParams requestParams = new RequestParams(url);
        requestParams.setSaveFilePath(downloadPath);
        requestParams.setAutoRename(true);
        x.http().get(requestParams, new org.xutils.common.Callback.ProgressCallback<File>() {

            @Override
            public void onSuccess(File result) {
                System.out.println("文件下载"+"下载成功");
                downloadResult(Constant.SUCCESS,"下载成功",null);
                if (autoInstall){
                    startInstallApk(result);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                System.out.println("文件下载"+"下载异常"+ex.toString());
                downloadResult(Constant.ERROR,ex.toString(),null);
            }

            @Override
            public void onCancelled(CancelledException cex) {
                System.out.println("文件下载"+"取消下载");
                downloadResult(Constant.CANCEL,"取消下载",null);
            }

            @Override
            public void onFinished() {
                System.out.println("文件下载"+"结束下载");
                downloadResult(Constant.FINISHED,"结束下载",null);
            }

            @Override
            public void onWaiting() {
                System.out.println("文件下载"+"等待下载");
                downloadResult(Constant.WAITING,"等待下载",null);
            }

            @Override
            public void onStarted() {
                System.out.println("文件下载"+"开始下载");
                downloadResult(Constant.STARTED,"开始下载",null);
            }

            @Override
            public void onLoading(long total, long current, boolean isDownloading) {
                WritableMap writableMap = Arguments.createMap();
                writableMap.putString("total",total+"");
                writableMap.putString("current",current+"");
                downloadResult(Constant.LOADING,"正在下载",writableMap);
                System.out.println("文件下载"+"正在下载=total="+total+"，current="+current);
            }
        });
    }

    @ReactMethod
    public void install(String apkPath){
        File file = new File(apkPath);
        startInstallApk(file);
    }

    /**
     * 安装apk
     * @param apkFile
     */
    public void startInstallApk( File apkFile){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT > 23){
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Uri apkUri = FileProvider.getUriForFile(reactContext,"",apkFile);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri,"application/vnd.android.package-archive");

        }else {
            intent.setDataAndType(Uri.fromFile(apkFile),"application/vnd.android.package-archive");
        }
        reactContext.startActivity(intent);
    }

    /**
     * Emitter返回集合封装
     * @param code
     * @param message
     * @param map
     */
    private void downloadResult(int code , String message , WritableMap map){
        WritableMap writableMap = Arguments.createMap();
        writableMap.putInt("code",code);
        writableMap.putString("message",message);
        writableMap.putString("type","APK_DOWNLOAD");
        writableMap.putMap("data",map);
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RNAppManagerEmitter", writableMap);
    }

}