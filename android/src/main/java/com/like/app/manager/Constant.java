package com.like.app.manager;

/**
 * 作者：like on 2019-06-13 15:12
 * <p>
 * 邮箱：like@tydic.com
 * <p>
 * 描述：长两驱
 */
public interface Constant {
    int WAITING = 0;//等待下载
    int STARTED = 1;//开始下载
    int LOADING =2;//正在下载
    int SUCCESS = 3;//下载成功
    int FINISHED = 4;//下载完成
    int CANCEL = 5;//下载取消
    int ERROR = 6;//下载出错

    int INSTALL = 7;//应用安装
    int UNINSTALL = 8;//应用卸载
    int UPDATE = 9;//应用更新
}
