package com.NewElectric.app11.service.logic.logic_httpConnection.longLink;

import android.content.Context;
import android.content.pm.PackageManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.NewElectric.app11.MyApplication;
import com.NewElectric.app11.hardwarecomm.agreement.send.controlPlate.ControlPlateSend;
import com.NewElectric.app11.hardwarecomm.agreement.send.other.ElectricityMeterSend;
import com.NewElectric.app11.hardwarecomm.androidHard.DataFormat;
import com.NewElectric.app11.model.dao.sharedPreferences.CabInfoSp;
import com.NewElectric.app11.service.BaseLogic;
import com.NewElectric.app11.service.logic.logic_charging.pdu_1_to_3.BatteryActivation;
import com.NewElectric.app11.service.logic.logic_exchange.UidDictionart;
import com.NewElectric.app11.service.logic.logic_writeUid.WriteUid;
import com.NewElectric.app11.service.logic.logic_httpConnection.fileDownLoad.UpdateAndroidDownloaderAndLauncher;
import com.NewElectric.app11.service.logic.logic_httpConnection.fileDownLoad.UpdateAndroidCore;
import com.NewElectric.app11.service.logic.logic_httpConnection.fileUpload.logs.HttpUploadLogs;
import com.NewElectric.app11.service.logic.logic_httpConnection.fileUpload.logs.HttpUploadLogsPath;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttp;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.BaseHttpParameterFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.rentBattery.RentBatteryDataFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.http.uploadCabinet.UploadCabinetInfoDataFormat;
import com.NewElectric.app11.service.logic.logic_httpConnection.fileDownLoad.UpdateHardWare;
import com.NewElectric.app11.service.logic.logic_httpConnection.fileUpload.movies.HttpUploadMovies;
import com.NewElectric.app11.service.logic.logic_httpConnection.fileUpload.movies.HttpUploadMoviesPath;
import com.NewElectric.app11.units.FilesDirectoryUnits;
import com.NewElectric.app11.units.Md5;
import com.NewElectric.app11.units.RootCommand;


public class LongLinkConnect extends BaseLogic implements OpenLongLink.IFHttpOpenLongLinkLinstener {

    //单例
    private static LongLinkConnect instance = new LongLinkConnect();
    private LongLinkConnect() { }
    public static LongLinkConnect getInstance() {
        return instance;
    }

    private int threadHangUpCode = 0;
    private Context context;


    public void longLinkInit(Context context) {
        this.context = context;
        BaseLogicInit(context);
        OpenLongLink.getInstance().init(this);
    }

    public int getThreadHangUpCode() {
        return threadHangUpCode;
    }

    public void setThreadHangUpCode(int threadHangUpCode) {
        this.threadHangUpCode = threadHangUpCode;
    }

    @Override
    public void onHttpReTurnIDResult(String code) {
        final String fCode = code;
        List<BaseHttpParameterFormat> baseHttpParameterFormats =  new ArrayList<>();
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("number",cabInfoSp.getCabinetNumber_4600XXXX()));
        baseHttpParameterFormats.add(new BaseHttpParameterFormat("client_id",code));
        BaseHttp baseHttp = new BaseHttp(HttpUrlMap.BindLongLink, baseHttpParameterFormats, 6, new BaseHttp.BaseHttpListener() {
            @Override
            public void dataReturn(int code, String message, String data) {
                System.out.println("longlink :    code - " + code + " - message - " + message + " - data - " + data);
                if(code != 1){
                    new Thread(){
                        @Override
                        public void run() {
                            super.run();
                            try {
                                sleep(30 * 1000);
                                onHttpReTurnIDResult(fCode);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                }
            }
        });
        baseHttp.onStart();
    }

    /**
     * 内置参数：
     * onlineType
     * openAdmin
     * showDialog
     * updateBatteryUI
     * updateCabinetUI
     * updateHardWare
     */
    private void sendData(String onLineType) { //在线 离线
        setChanged();
        notifyObservers(new DataFormat<>("onlineType", onLineType));
    }
    private void sendData() {
        setChanged();
        notifyObservers(new DataFormat<>("updateCabinetUI", ""));
    }
    private void sendData(int openAdmin) { // 0 - 关闭  1 - 打开
        setChanged();
        notifyObservers(new DataFormat<>("openAdmin", openAdmin));
    }
    private void sendData(LongLinkConnectDialogFormat longLinkConnectDialogFormat){
        setChanged();
        notifyObservers(new DataFormat<>("showDialog", longLinkConnectDialogFormat));
    }
    private void sendData(LongLinkConnectUpdateHardWare longLinkConnectUpdateHardWare){
        setChanged();
        notifyObservers(new DataFormat<>("updateHardWare", longLinkConnectUpdateHardWare));
    }
    private void sendDataUpdateBatteryUI(int door) {
        setChanged();
        notifyObservers(new DataFormat<>("updateBatteryUI", door));
    }


    @Override
    public void onHttpReturnErrorResult(int data) {
        if (data == 1) {
            sendData("在线");
        } else {
            sendData("离线");
        }
    }


    @Override
    public void onHttpReturnDataResult(String orderString) {
        try {
            JSONTokener jsonTokener = new JSONTokener(orderString);
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            String type = jsonObject.getString("type");
            if (threadHangUpCode == 1) {
                return;
            }
            //长链接下发  重启android板子
            if (type.equals("restartAndrBoard")) {
                //获取root权限
                new RootCommand().RootCommandStart("reboot");
            }
            //长链接下发  打开关闭后台
            else if (type.equals("cmdRemoteOpenAdmin")) {
                String action = jsonObject.getString("action");
                if (action.equals("1")) {
                    if (MyApplication.getInstance().getActivity().size() == 1) {
                        sendData(1);
                    }
                }
                if (action.equals("0")) {
                    if (MyApplication.getInstance().getActivity().size() == 2) {
                        MyApplication.getInstance().getActivity().get(1).finish();
                        MyApplication.getInstance().getActivity().remove(1);
                    }
                }

                //todo::获取电池内部日志
//                GetBatteryInnerLog getBatteryInnerLog = new GetBatteryInnerLog(activity, 1, new GetBatteryInnerLog.GetBatteryInnerLogListener() {
//                    @Override
//                    public void onGetBatteryInnerLogStart() {
//                        MyApplication.getInstance().serialService.setThreadHandCode(1);
//                    }
//
//                    @Override
//                    public void onGetBatteryInnerLogInfo(String info) {
//
//                    }
//
//                    @Override
//                    public void onGetBatteryInnerLogFinish() {
//                        MyApplication.getInstance().serialService.setThreadHandCode(0);
//                    }
//
//                    @Override
//                    public void onGetBatteryInnerLogRetun(BatteryHistoryLogs batteryHistoryLogs) {
//
//                    }
//                });
//                getBatteryInnerLog.onStart();
            }
            //长链接下发  打开提示框
            else if (type.equals("cmdAlertMsg")) {
                String msg = jsonObject.getString("msg");
                String time = jsonObject.getString("time");
                sendData(new LongLinkConnectDialogFormat(msg,Integer.parseInt(time),1));
            }
            //长链接下发  显示舱门状态
            else if (type.equals("remoteSendCabStat")) {
                //舱门数据
                String data = jsonObject.getString("data");
                //设置电柜加热模式
                String isheat = jsonObject.getString("isheat");
                if(isheat.equals("2")){
                    //设置充电模式
                    Calendar cd = Calendar.getInstance();
                    int month = cd.get(Calendar.MONTH) + 1;
                    if (month == 10 || month == 11 || month == 12 || month == 1 || month == 2) {
                        new CabInfoSp(context).setChargeMode("1");
                    } else {
                        new CabInfoSp(context).setChargeMode("0");
                    }
                }else{
                    cabInfoSp.setChargeMode(isheat);
                }
                //更新显示信息
                cabInfoSp.setCabinetNumber_XXXXX(jsonObject.getString("cabid"));
                cabInfoSp.setAddress(jsonObject.getString("name"));
                cabInfoSp.setTelNumber(jsonObject.getJSONArray("phones").getJSONObject(0).getString("phone"));
                if (jsonObject.getString("number").equals(cabInfoSp.getCabinetNumber_4600XXXX())) {

                    JSONTokener jsonTokener_1 = new JSONTokener(data);
                    JSONArray jsonArray = (JSONArray) jsonTokener_1.nextValue();
                    for (int i = 0; i < jsonArray.length(); i++) {

                        JSONObject jsonObject_1 = jsonArray.getJSONObject(i);
                        String door = jsonObject_1.getString("door");
                        String outIn = jsonObject_1.getString("outIn");
                        int door_int = Integer.parseInt(door);
                        int outIn_int = Integer.parseInt(outIn);
                        if (forbiddenSp.getTargetForbidden(i) != -3) {
                            forbiddenSp.setTargetForbidden(door_int - 1, outIn_int);
                        } else {
                            if (outIn.equals("-1") || outIn.equals("-2")) {
                                forbiddenSp.setTargetForbidden(door_int - 1, outIn_int);
                            }
                        }
                    }
                }
                sendData();
            }
            //长链接下发  更新柜子android软件
            else if (type.equals("updateCabinetApp")) {
                String furl = jsonObject.getString("furl");
                sendData(new LongLinkConnectDialogFormat("准备更新主程序，请勿进行操作！",10,1));
                UpdateHardWare updateHardWare = new UpdateHardWare(context, "app11.apk", furl, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        content.installApk(dataPath,true);
                        /* TODO: 下载界面更新界面 */
                    }
                });
                updateHardWare.downloadAPK();
                writeLog("下载 - 正在下载安装换电程序");
            }
            //长链接下发  网络后台开门
            else if (type.equals("remoteOpenDoor")) {
                int door = Integer.parseInt(jsonObject.getString("door"));
                String reason = jsonObject.getString("reason");
                pushAndPull(door, "网络下发弹出电池：" + reason);

            }
            //长链接下发  给服务器上传电柜里面的电池参数
            else if (type.equals("getBatteryInfo")) {
                List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", cabInfoSp.getCabinetNumber_4600XXXX()));
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("jsons", new UploadCabinetInfoDataFormat().getJson(context, controlPlateInfo, pduInfo)));
                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.UploadCabinetInfo, baseHttpParameterFormats);
                baseHttp.onStart();
            }
            //长链接下发  给服务器上传电柜里面的电池参数
            else if (type.equals("rentBtyList")) {
                sendData(new LongLinkConnectDialogFormat("正在校验电池信息！",10,1));
                String uid = jsonObject.getString("uid");
                String order_num = jsonObject.getString("order_num");
                String did = jsonObject.getString("did");
                List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("uid", uid));
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("btyjson", new RentBatteryDataFormat().getJson(context, did, uid, order_num, controlPlateInfo).toString()));
                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.RentBattery, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                    @Override
                    public void dataReturn(int code, String message, String data) {
                        if (code == -1 || code == 0) {
                            sendData(new LongLinkConnectDialogFormat(message,10,1));
                        } else {
                            try {
                                JSONObject jsonObject = new JSONObject(data);
                                String uid32 = jsonObject.getString("uid32");
                                int door = Integer.parseInt(jsonObject.getString("door"));
                                int index = door - 1;

                                WriteUid.getInstance().write(door, uid32, "租电池写入UID", new WriteUid.WriteUidListener() {
                                    @Override
                                    public void showDialog(String message, int time, int type) {
                                        sendData(new LongLinkConnectDialogFormat(message,10,1));
                                    }

                                    @Override
                                    public void writeUidResult(boolean result) {
                                        List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                                        baseHttpParameterFormats.add(new BaseHttpParameterFormat("did",did));
                                        baseHttpParameterFormats.add(new BaseHttpParameterFormat("number",cabInfoSp.getCabinetNumber_4600XXXX()));
                                        baseHttpParameterFormats.add(new BaseHttpParameterFormat("door",door+""));
                                        if(result){
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("rstatus","1"));
                                        }else{
                                            baseHttpParameterFormats.add(new BaseHttpParameterFormat("rstatus","-1"));
                                        }
                                        baseHttpParameterFormats.add(new BaseHttpParameterFormat("battery",controlPlateInfo.getControlPlateBaseBeans()[index].getBID()));
                                        baseHttpParameterFormats.add(new BaseHttpParameterFormat("electric",controlPlateInfo.getControlPlateBaseBeans()[index].getBatteryRelativeSurplus()+""));
                                        BaseHttp baseHttp = new BaseHttp(HttpUrlMap.RentBatteryFinish, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                                            @Override
                                            public void dataReturn(int code, String message, String data) {
                                                String tel = UidDictionart.getI10EndPhoneNumber(uid32);
                                                pushAndPull( door,"租赁电池");
                                                sendData(new LongLinkConnectDialogFormat("租赁成功！！请手机尾号" + tel + "的用户拿走第" + door + "号舱门电池",10,1));
                                            }
                                        });
                                        baseHttp.onStart();
                                    }


                                });
                            } catch (Exception e) {
                                System.out.println("longlink - error - " + e.toString());
                                sendData(new LongLinkConnectDialogFormat(e.toString(),10,1));
                            }
                        }
                    }
                });
                baseHttp.onStart();
            }

            //长链接下发  更新android内核
            else if (type.equals("upgradeCabinetCore")) {
                String apkUrl = jsonObject.getString("apkurl");
                String zipUrl = jsonObject.getString("zipurl");
                UpdateAndroidCore updateAndroidCore = new UpdateAndroidCore(context, apkUrl, zipUrl);
                updateAndroidCore.onStart();
            }

            //长链接下发 绑定长链接成功
            else if (type.equals("bindSuccess")) {
                //获得网络之后检测上次的电柜信息 判断要不要吐出电池
                String lastCabInfo = cabInfoSp.getLastCabInfo();
                if (lastCabInfo.equals("")) {
                    System.out.println("网络：   保存空数据");
                    return;
                }
                JSONObject lastCabInfoJSONObject = new JSONObject(lastCabInfo);
                long diffExtimr = Long.parseLong(System.currentTimeMillis() + "") - Long.parseLong(lastCabInfoJSONObject.getString("extime"));
                int second = (int) diffExtimr / 1000;
                if (second > 3600 * 3) {
                    System.out.println("网络：   断网时间 - " + second + "秒   启动弹出");
                    List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("number", cabInfoSp.getCabinetNumber_4600XXXX()));
                    baseHttpParameterFormats.add(new BaseHttpParameterFormat("jsons", new UploadCabinetInfoDataFormat().getJson(context, controlPlateInfo, pduInfo)));
                    BaseHttp baseHttp = new BaseHttp(HttpUrlMap.BatteriesIsBinding, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                        @Override
                        public void dataReturn(int code, String message, String data) {
                            try {
                                if (code == -1) {
                                    sendData(new LongLinkConnectDialogFormat(message,10,1));
                                } else if (code == 1) {
                                    JSONArray jsonArray = new JSONArray(data);
                                    if (jsonArray.toString().equals("[]")) {
                                        System.out.println("网络：   没有绑定电池");
                                    } else {
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject_item = jsonArray.getJSONObject(i);
                                            pushAndPull(Integer.parseInt(jsonObject_item.getString("door")), "开机弹出绑定电池");
                                        }
                                    }
                                } else if (code == 0) {
                                    sendData(new LongLinkConnectDialogFormat(message,10,1));
                                }
                            }catch (Exception e){
                                System.out.println(e.toString());
                            }
                        }
                    });
                    baseHttp.onStart();
                } else {
                    System.out.println("网络：   断网时间 - " + second + "秒   不启动弹出");
                }

                List<BaseHttpParameterFormat> baseHttpParameterFormats = new ArrayList<>();
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("token", Md5.getMd5Token("1")));
                baseHttpParameterFormats.add(new BaseHttpParameterFormat("data", "1"));
                BaseHttp baseHttp = new BaseHttp(HttpUrlMap.GetDownloaderAndLauncher, baseHttpParameterFormats, new BaseHttp.BaseHttpListener() {
                    @Override
                    public void dataReturn(int code, String message, String data) {
                        try{
                            JSONObject jsonObjectVersion = new JSONObject(data);
                            String downloaderVersion = jsonObjectVersion.getString("DownloaderVersion");
                            String launcherVersion = jsonObjectVersion.getString("LauncherVersion");
                            String downloaderUrl = jsonObjectVersion.getString("DownloaderUrl");
                            String launcherUrl = jsonObjectVersion.getString("LauncherUrl");
                            UpdateAndroidDownloaderAndLauncher updateAndroidDownloaderAndLauncher = new UpdateAndroidDownloaderAndLauncher(context, downloaderVersion, launcherVersion, downloaderUrl, launcherUrl, new UpdateAndroidDownloaderAndLauncher.UpdataAndroidDownloaderAndLauncherListener() {
                                @Override
                                public void showDialog(String message, int time) {
                                    sendData(new LongLinkConnectDialogFormat(message,time,1));
                                }

                                @Override
                                public void writeLog(String message) {
                                    writeLog(message);
                                }
                            });
                            updateAndroidDownloaderAndLauncher.onStart();
                        }catch (Exception e){
                            System.out.println(e.toString());
                        }
                    }
                });
                baseHttp.onStart();
            }
            //长链接下发伸长推杆 和收回
            else if (type.equals("disableDoorOut")) {
                String door = jsonObject.getString("door");
                String outIn = jsonObject.getString("outIn");
                forbiddenSp.setTargetForbidden(Integer.parseInt(door) - 1, Integer.parseInt(outIn));
                //伸出禁用
                if (outIn.equals("-2")) {
                    push(Integer.parseInt(door));
                }
                //收回禁用
                else if (outIn.equals("-1")) {
                    pull(Integer.parseInt(door));
                }
                //解禁
                else if (outIn.equals("1")) {
                    pull(Integer.parseInt(door));
                }
                sendDataUpdateBatteryUI(Integer.parseInt(door));
            }
            //长链接下发 更新电表信息
            else if (type.equals("updateAmmeter")) {
                sendData(new LongLinkConnectDialogFormat("正在更新用电量，请稍候！",10,1));
                MyApplication.serialAndCanPortUtils.serSendOrder(ElectricityMeterSend.getMeterAddress());
            }
            //长链接下发 激活电池
            else if (type.equals("activateBattery")) {
                String door = jsonObject.getString("door");
                new BatteryActivation(Integer.parseInt(door)).onStart();
            }
            //长链接下发 更新电表信息
            else if (type.equals("setThreadsProtectionStatus")) {
                String threadType = jsonObject.getString("setStatus");
                if (threadType.equals("0")) {
                    cabInfoSp.setTPTNumber("0");
                    sendData(new LongLinkConnectDialogFormat("正在关闭线程保护！",10,1));
                } else {
                    sendData(new LongLinkConnectDialogFormat("正在打开线程保护！",10,1));
                    cabInfoSp.setTPTNumber("1");
                }
            }
            //长链接下发 更新电池程序
            else if (type.equals("updateOneBattery")) {
                final String url = jsonObject.getString("url");
                final String tarDoor = jsonObject.getString("door");
                final String fname = jsonObject.getString("fname");
                final String manu = jsonObject.getString("manu");
                new UpdateHardWare(context, fname, url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        LongLinkConnectUpdateHardWare longLinkConnectUpdateHardWare = new LongLinkConnectUpdateHardWare(Integer.parseInt(tarDoor), dataPath, manu, new CabInfoSp(context).getTelNumber(), cabInfoSp.getCabinetNumber_4600XXXX(), "Battery");
                        sendData(longLinkConnectUpdateHardWare);
                    }
                });
            }
            //长链接下发 更新pdu程序
            else if (type.equals("updatePdu")) {
                String url = jsonObject.getString("url");
                new UpdateHardWare(context, "pdu_update.bin", url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        LongLinkConnectUpdateHardWare longLinkConnectUpdateHardWare = new LongLinkConnectUpdateHardWare(0, dataPath, "0", new CabInfoSp(context).getTelNumber(), cabInfoSp.getCabinetNumber_4600XXXX(), "PDU");
                        sendData(longLinkConnectUpdateHardWare);
                    }
                });
            }
            //长链接下发  更新硬件程序
            else if (type.equals("updateHard")) {
                final String url = jsonObject.getString("url");
                new UpdateHardWare(context, "app.bin", url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        LongLinkConnectUpdateHardWare longLinkConnectUpdateHardWare = new LongLinkConnectUpdateHardWare(1, dataPath, "1", new CabInfoSp(context).getTelNumber(), cabInfoSp.getCabinetNumber_4600XXXX(), "ControlPlate");
                        sendData(longLinkConnectUpdateHardWare);
                    }
                });
            }
            //长链接下发  更新硬件程序
            else if (type.equals("updateOneHardDoor")) {
                final String url = jsonObject.getString("url");
                final String door = jsonObject.getString("door");
                new UpdateHardWare(context, "app.bin", url, new UpdateHardWare.DownloadUpdateFileListener() {
                    @Override
                    public void onDownloadUpdateFileReturn(UpdateHardWare content, String dataPath) {
                        LongLinkConnectUpdateHardWare longLinkConnectUpdateHardWare = new LongLinkConnectUpdateHardWare(Integer.parseInt(door), dataPath, "2", new CabInfoSp(context).getTelNumber(), cabInfoSp.getCabinetNumber_4600XXXX(), "ControlPlate");
                        sendData(longLinkConnectUpdateHardWare);
                    }
                });
            }
            //长链接下发 回传目录信息
            else if (type.equals("upVideoFileList")) {
                System.out.println("movies：" + jsonObject.toString());
                String cabid = jsonObject.getString("cabid");
                String admid = jsonObject.getString("admid");
                String upUrl = jsonObject.getString("upUrl");
                String logintk = jsonObject.getString("_logintk_");
                String remark = jsonObject.getString("remark");
                String date = jsonObject.getString("date");
                String hour = jsonObject.getString("hour");
                String level = jsonObject.getString("level");
                HttpUploadMoviesPath httpUploadMoviesPath = new HttpUploadMoviesPath(context, cabid, admid, upUrl, logintk, date, hour, level);
                httpUploadMoviesPath.start();
            }
            //长链接下发 回传视频信息
            else if (type.equals("upVideoFile")) {
                System.out.println("movies：" + jsonObject.toString());
                String cabid = jsonObject.getString("cabid");
                String vname = jsonObject.getString("vname");
                String admid = jsonObject.getString("admid");
                String upUrl = jsonObject.getString("upUrl");
                String upField = jsonObject.getString("upField");
                String token = jsonObject.getString("_token");
                String hour = jsonObject.getString("hour");
                String day = jsonObject.getString("day");
                HttpUploadMovies httpUploadMovies = new HttpUploadMovies();
                httpUploadMovies.httpPost(upUrl, FilesDirectoryUnits.EXTERNAL_MOVIES_DIR + File.separator + day + File.separator + hour + File.separator + vname, vname, cabid, admid, token, new HttpUploadMovies.HttpUploadMoviesListener() {
                    @Override
                    public void onHttpUploadMoviesReturn(long total, long now, long type, String title) {
                        /* TODO: 下载界面更新界面 */
                    }
                });
            }
            else if (type.equals("writeBtyUid")) {
                final String fUid32 = jsonObject.getString("uid32");
                final String fDoor = jsonObject.getString("door");
                final String fOutType = jsonObject.getString("outType");
                WriteUid.getInstance().write(Integer.parseInt(fDoor), fUid32, "后台写入UID", new WriteUid.WriteUidListener() {
                    @Override
                    public void showDialog(String message, int time, int type) {
                        sendData(new LongLinkConnectDialogFormat(message,10,1));
                    }

                    @Override
                    public void writeUidResult(boolean result) {
                        if(result){
                            if (fOutType.equals("2")) {
                                pushAndPull(Integer.parseInt(fDoor),"写入电池UID，并弹出电池");
                                String tel = UidDictionart.getI10EndPhoneNumber(fUid32);
                                sendData(new LongLinkConnectDialogFormat("电池写入成功！！请手机尾号" + tel + "的用户拿走第" + fDoor + "号舱门电池",10,1));
                            }
                        }else {
                            sendData(new LongLinkConnectDialogFormat("电池写入失败！！如有问题请联系电话客服！",10,1));
                        }
                    }
                });
            }
            //长链接 下发重启控制板
            else if (type.equals("remoteRestartDoor")) {
                String door = jsonObject.getString("door");
                MyApplication.serialAndCanPortUtils.canSendOrder(ControlPlateSend.canPlateReboot(Integer.parseInt(door)));
            }
            //长链接 下发重启控制板
            else if (type.equals("upLogFileList")) {
                String cabid = jsonObject.getString("cabid");
                String admid = jsonObject.getString("admid");
                String date = jsonObject.getString("date");
                String upUrl = jsonObject.getString("upUrl");
                String remark = jsonObject.getString("remark");
                HttpUploadLogsPath httpUploadLogsPath = new HttpUploadLogsPath(cabid, admid, upUrl,date);
                httpUploadLogsPath.start();
            }
            //长链接 下发重启控制板
            else if (type.equals("upLogFileToServ")) {
                String cabid = jsonObject.getString("cabid");
                String vname = jsonObject.getString("vname");
                String admid = jsonObject.getString("admid");
                String day = jsonObject.getString("day");
                String upField = jsonObject.getString("upField");
                String upUrl = jsonObject.getString("upUrl");
                String remark = jsonObject.getString("remark");
                HttpUploadLogs httpUploadLogs = new HttpUploadLogs();
                httpUploadLogs.httpPost(upUrl , day , vname , cabid , admid);
            }
            //长链接 切换服务器
            else if (type.equals("setGlobalDomain")) {
                String globalDomain = jsonObject.getString("mjson");
                JSONObject jsonObject1 = new JSONObject(globalDomain);
                cabInfoSp.setServer(jsonObject1.toString());
                new RootCommand().RootCommandStart("reboot");
            }

        } catch (JSONException | PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }
}
