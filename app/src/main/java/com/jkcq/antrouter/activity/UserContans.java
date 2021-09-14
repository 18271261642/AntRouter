package com.jkcq.antrouter.activity;

import java.util.concurrent.ConcurrentHashMap;

public class UserContans {


    //收到的SN对应的hr值
    volatile public static ConcurrentHashMap<String, String> mSnHrMap = new ConcurrentHashMap<>();
    volatile public static ConcurrentHashMap<String, String> mSnSendHrMap = new ConcurrentHashMap<>();
    //收到的SN对应的时间
    volatile public static ConcurrentHashMap<String, Long> mSnHrTime = new ConcurrentHashMap<>();
    //收到SN对应的电池
    volatile public static int REFRESH_RATE = 2;


}
