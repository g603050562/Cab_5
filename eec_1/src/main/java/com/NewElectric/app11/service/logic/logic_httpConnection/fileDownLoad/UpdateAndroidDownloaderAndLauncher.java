package com.NewElectric.app11.service.logic.logic_httpConnection.fileDownLoad;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class UpdateAndroidDownloaderAndLauncher {

    public interface UpdataAndroidDownloaderAndLauncherListener{
        void showDialog(String message , int time);
        void writeLog(String message);
    }

    private Context context;
    private String downloaderVersion;
    private String launcherVersion;
    private String downloaderUrl;
    private String launcherUrl;

    private UpdataAndroidDownloaderAndLauncherListener updataAndroidDownloaderAndLauncherListener;

    public UpdateAndroidDownloaderAndLauncher(Context context, String downloaderVersion, String launcherVersion, String downloaderUrl, String launcherUrl , UpdataAndroidDownloaderAndLauncherListener updataAndroidDownloaderAndLauncherListener) {
        this.context = context;
        this.downloaderVersion = downloaderVersion;
        this.launcherVersion = launcherVersion;
        this.downloaderUrl = downloaderUrl;
        this.launcherUrl = launcherUrl;
        this.updataAndroidDownloaderAndLauncherListener = updataAndroidDownloaderAndLauncherListener;
    }

    public void onStart() throws PackageManager.NameNotFoundException {
        //本地版本
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageInfo("com.NewElectric.app4", 0);
        int nowDownloaderVersion = packageInfo.versionCode;
        PackageInfo packageInfoLauncher = packageManager.getPackageInfo("com.NewElectric.app5", 0);
        int nowLauncherVersion = packageInfoLauncher.versionCode;

        if (Integer.parseInt(downloaderVersion) > nowDownloaderVersion) {
            UpdateHardWare updateHardWare = new UpdateHardWare(context, "app4.apk", downloaderUrl, new UpdateHardWare.DownloadUpdateFileListener() {
                @Override
                public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                    content.installApk(dataPath,false);
                }
            });
            updateHardWare.downloadAPK();
            updataAndroidDownloaderAndLauncherListener.writeLog("下载 - 正在下载安装下载器");
            updataAndroidDownloaderAndLauncherListener.showDialog("正在下载下载器！", 3);
        }
        if (Integer.parseInt(launcherVersion) > nowLauncherVersion) {
            UpdateHardWare updateHardWare = new UpdateHardWare(context, "app5.apk", launcherUrl, new UpdateHardWare.DownloadUpdateFileListener() {
                @Override
                public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                    content.installApk(dataPath,true);
                }
            });
            updateHardWare.downloadAPK();
            updataAndroidDownloaderAndLauncherListener.writeLog("下载 - 正在下载安装启动器");
            updataAndroidDownloaderAndLauncherListener.showDialog("正在下载启动器！", 3);
        }
    }

}
