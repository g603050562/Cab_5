package com.NewElectric.app11.config;

public class SystemConfig {

    private static String HELLO = "halouhuandian";
    private static String MIXIANG = "mixiang";

    public enum serverEnum{
        hello,
        mixiang,
    }

    public static serverEnum getServer(String serverInfo){
        if(serverInfo.indexOf(HELLO)!=-1){
            return serverEnum.hello;
        }else if(serverInfo.indexOf(MIXIANG)!=-1){
            return serverEnum.mixiang;
        }else {
            return serverEnum.hello;
        }
    }

}
