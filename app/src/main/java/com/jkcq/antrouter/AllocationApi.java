package com.jkcq.antrouter;

import com.jkcq.antrouter.utils.DeviceUtil;

public class AllocationApi {
    //
    //https://gateway.fitalent.com.cn/
    // public static String BaseUrl = "https://gateway.fitalent.com.cn";
    //public static String BaseUrl = "https://test.gateway.fitalent.com.cn";
   // public static final String BaseUrl = "https://test.hrw.fitalent.com.cn";//新的心率墙地址

//    public static final String BaseUrl = "https://test.hrw.mini-banana.com";
    public static final String BaseUrl = "https://hrw.mini-banana.com";

    //获取俱乐部列表
    //https://gateway.fitalent.com.cn/gymDevice/device/antDevice/getClubInfoByMac
    public static String getClubListUrl() {
        return BaseUrl + "/gymManager/manager/clubInfo/listSelectItem";
    }

    //注册ANt设备  https://gateway.fitalent.com.cn/gymDevice/device/antDevice/addDevice
    public static String getRegister() {
        return BaseUrl + "/gymDevice/device/antDevice/addDevice";
    }

    //更新状态
    public static String getUpLoadStatus() {
        return BaseUrl + "/gymDevice/device/antDevice/upDateStatus";
    }

    public static String queryDevices() {
        return BaseUrl + "/gymDevice/device/antDevice/getClubInfoByMac";
    }


    // 判断是否有网络
    public static boolean isNetwork = true;
    public static boolean isShowHint = false;


    public static String getMD5MAC() {
        return DeviceUtil.getMac(AntRouterApplication.getApp());
    }

}
