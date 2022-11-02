package com.NewElectric.app11.service.logic.logic_httpConnection.fileDownLoad;

import android.content.Context;

import com.NewElectric.app11.units.RootCommand;

public class UpdateAndroidCore {

    private Context context;

    private String apkUrl = "";
    private String apkName = "RKUpdateService.apk";
    private String apkSavePath = "/system/app/RKUpdateService/RKUpdateService.apk";

    private String zipUrl = "";
    private String zipName = "update.zip";

    //内核升级参数
    private int androidCoreState = 0;

    public UpdateAndroidCore(Context context , String apkUrl , String zipUrl){
        this.context = context;
        this.apkUrl = apkUrl;
        this.zipUrl = zipUrl;
    }

    public void onStart(){

        new RootCommand().RootCommandStart("root");
        new RootCommand().RootCommandStart("remount");

        new UpdateHardWare(context, apkName, apkUrl, "",apkSavePath, new UpdateHardWare.DownloadUpdateFileListener() {
            @Override
            public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                androidCoreState = androidCoreState + 1;
                System.out.println("download：   apk - 下载成功");
                if (androidCoreState == 2) {
                    new RootCommand().RootCommandStart("reboot");
                    androidCoreState = 0;
                }
            }
        });
        new UpdateHardWare(context, zipName, zipUrl, "",new UpdateHardWare.DownloadUpdateFileListener() {
            @Override
            public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                androidCoreState = androidCoreState + 1;
                System.out.println("download：   zip - 下载成功");
                if (androidCoreState == 2) {
                    new RootCommand().RootCommandStart("reboot");
                    androidCoreState = 0;
                }
            }
        });
    }

}
