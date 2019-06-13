package com.like.app.manager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

/**
 * 作者：like on 2019-06-13 14:44
 * <p>
 * 邮箱：like@tydic.com
 * <p>
 * 描述：应用安装和卸载监听
 */
public class AppManagerBroadcastReceiver extends BroadcastReceiver {

    private ReactApplicationContext reactContext;

    public AppManagerBroadcastReceiver(ReactApplicationContext reactContext){
        this.reactContext = reactContext;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        /**
         * 获取（安装/替换/卸载）应用的 信息
         */
        String packages = intent.getDataString();
        packages = packages.split(":")[1];
        String action = intent.getAction();
        if (Intent.ACTION_PACKAGE_ADDED.equals(action)) {
            System.out.println("应用安装1"+packages);
            appManagerResult(Constant.INSTALL,"应用已安装",packages);
        } else if (Intent.ACTION_PACKAGE_REMOVED.equals(action)) {
            System.out.println("应用安装2"+packages);
            appManagerResult(Constant.UNINSTALL,"应用已卸载",packages);
        } else if (Intent.ACTION_PACKAGE_REPLACED.equals(action)) {
            System.out.println("应用安装3"+packages);
            appManagerResult(Constant.UPDATE,"应用已覆盖",packages);
        }
    }

    /**
     * Emitter返回集合封装
     * @param code
     * @param message
     * @param packageName
     */
    private void appManagerResult( int code , String message , String packageName){
        WritableMap writableMap = Arguments.createMap();
        writableMap.putInt("code",code);
        writableMap.putString("message",message);
        writableMap.putString("type","APK_CHANGE");
        writableMap.putString("packageName",packageName);
        reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("RNAppManagerEmitter", writableMap);
    }
}
