package com.NewElectric.app11.service.logic.logic_httpConnection.longLink;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.net.URI;
import java.net.URISyntaxException;

import com.NewElectric.app11.service.logic.logic_httpConnection.http.HttpUrlMap;


/**
 * Created by apple on 2017/7/27.
 */

public class OpenLongLink {


    private static OpenLongLink instance = new OpenLongLink();
    private OpenLongLink() { }
    public static OpenLongLink getInstance() {
        return instance;
    }

    private WebSocketClient mWebSocketClient;
    private String client_id;

    public interface IFHttpOpenLongLinkLinstener {
        void onHttpReTurnIDResult(String code);
        void onHttpReturnDataResult(String data);
        void onHttpReturnErrorResult(int data);
    }

    private IFHttpOpenLongLinkLinstener ifHttpOpenLongLinkLinstener;

    //长链接保护程序 有时候第一次长连接解析不了
    int longlink_save_code = 0;
    int longlink_save_count = 20;
    private Thread thread_longlink_save = new Thread() {
        @Override
        public void run() {
            super.run();

            while (longlink_save_code == 0) {
                if (longlink_save_count > 0) {
                    longlink_save_count = longlink_save_count - 1;
                } else {
                    try {
                        closeConnect();
                        initSocketClient();
                        mWebSocketClient.connect();
                        ifHttpOpenLongLinkLinstener.onHttpReturnErrorResult(0);
                        System.out.println("longlink :    调用connect()");
                    } catch (Exception e) {
                        System.out.println("longlink :    " + e.toString());
                    }
                    longlink_save_count = 20;
                }
                try {
                    sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    public void init(IFHttpOpenLongLinkLinstener ifHttpOpenLongLinkLinstener){
        this.ifHttpOpenLongLinkLinstener = ifHttpOpenLongLinkLinstener;

        if (!thread_longlink_save.isAlive()) {
            thread_longlink_save.start();
        }

        try {
            closeConnect();
            initSocketClient();
            mWebSocketClient.connect();
            System.out.println("longlink :    调用connect()");
        } catch (Exception e) {
            System.out.println("longlink :    " + e.toString());
        }
    }

    private void initSocketClient() throws URISyntaxException {

        System.out.println("longlink :    长连接请求连接");

        if (mWebSocketClient == null) {

            mWebSocketClient = new WebSocketClient(new URI(HttpUrlMap.longlink)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {
                    System.out.println("longlink :    onOpen first " + serverHandshake.toString());
                }
                @Override
                public void onMessage(final String s) {
                    //这个是收到服务器推送下来的消息
                    System.out.println("longlink :    " + s);
                    longlink_save_count = 20;
                    try {
                        JSONTokener jsonTokener = new JSONTokener(s);
                        JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
                        String type = jsonObject.getString("type");

                        if (type.equals("init")) {
                            client_id = jsonObject.getString("client_id");
                            ifHttpOpenLongLinkLinstener.onHttpReTurnIDResult(client_id);
                        } else if (
                                type.equals("bindSuccess") ||
                                        type.equals("openCabBackDoor") ||
                                        type.equals("restartAndrBoard") ||
                                        type.equals("cmdRemoteOpenAdmin") ||
                                        type.equals("cmdAlertMsg") ||
                                        type.equals("remoteSendCabStat") ||
                                        type.equals("asyncAnalyzeOpenDoor") ||
                                        type.equals("updateCabinetApp") ||
                                        type.equals("remoteOpenDoor") ||
                                        type.equals("remoteCloseDoor") ||
                                        type.equals("getBatteryInfo") ||
                                        type.equals("rentBtyList") ||
                                        type.equals("updateHard") ||
                                        type.equals("rentBattery") ||
                                        type.equals("bindSuccess") ||
                                        type.equals("disableDoorOut") ||
                                        type.equals("updateAmmeter") ||
                                        type.equals("setThreadsProtectionStatus") ||
                                        type.equals("updateOneBattery") ||
                                        type.equals("updateOneHardDoor") ||
                                        type.equals("upVideoFileList") ||
                                        type.equals("upVideoFile") ||
                                        type.equals("updatePdu") ||
                                        type.equals("writeBtyUid") ||
                                        type.equals("activateBattery") ||
                                        type.equals("upgradeDcdc") ||
                                        type.equals("upgradeAcdc") ||
                                        type.equals("upgradeDcdcAll") ||
                                        type.equals("upgradeAcdcAll") ||
                                        type.equals("envBoardUpgrade") ||
                                        type.equals("upgradeCabinetCore") ||
                                        type.equals("switchCabOnOffline")||
                                        type.equals("pushrodActSetTime")||
                                        type.equals("cabBottomHope")||
                                        type.equals("upLogFileList") ||
                                        type.equals("upLogFileToServ")||
                                        type.equals("setGlobalDomain")
                        ) {

                            ifHttpOpenLongLinkLinstener.onHttpReturnDataResult(jsonObject.toString());

                        } else if (type.equals("ping")) {
                            mWebSocketClient.send("{\"type\":\"pong\"}");
                            ifHttpOpenLongLinkLinstener.onHttpReturnErrorResult(1);
                            System.out.println("longlink :    " + "{\"type\":\"pong\"}");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                        System.out.println("longlink :    " + e.toString());
                    }
                }

                @Override
                public void onClose(int i, String s, boolean remote) {
                    //连接断开，remote判定是客户端断开还是服务端断开
                    System.out.println("longlink :    Connection closed by " + (remote ? "remote peer" : "us") + ", info=" + s);
                }
                @Override
                public void onError(Exception e) {
                    System.out.println("longlink :    onMessage" + "error:" + e);

                }
            };
        }
    }

    private void closeConnect() {
        try {
            if (mWebSocketClient == null) {
                return;
            } else {
                mWebSocketClient.closeConnection(0, "onDestory");
                mWebSocketClient.close();
                mWebSocketClient = null;
                System.out.println("longlink :    调用closeConnect() 重启长连接");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWebSocketClient = null;
        }
    }

    public void onDestory() {

        try {
            if (mWebSocketClient == null) {
                return;
            } else {
                mWebSocketClient.closeConnection(0, "onDestory");
                mWebSocketClient.close();
                mWebSocketClient = null;

                longlink_save_code = 1;

                System.out.println("longlink :    调用closeConnect() 长连接停止");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mWebSocketClient = null;
        }

    }


}
