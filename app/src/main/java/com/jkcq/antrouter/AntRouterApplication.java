package com.jkcq.antrouter;

import android.app.Application;

import com.jkcq.antrouter.bean.ClubInfo;
import com.tencent.bugly.crashreport.CrashReport;

import timber.log.Timber;

/*
 *
 * @author mhj
 * Create at 2019/2/18 11:09
 */
public class AntRouterApplication extends Application {

    public static AntRouterApplication instance;

    // 初始化Instance；
    public synchronized void setInstance() {
        if (instance == null) {
            instance = this;
        }
    }

    private void initApp() {
        setInstance();
    }

    public static AntRouterApplication getApp() {
        return instance;
    }


    private StringBuilder stringBuilder = new StringBuilder();
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initApp();
        CrashReport.initCrashReport(this, "5987a17450", false);
        //CrashHandler crashHandler = CrashHandler.getInstance();
        Timber.plant(new DebugLoggerTree());
    }


    public void setStringBuilder(String str){
        stringBuilder.append(str);
    }

    public String getStringBuilder(){
        return stringBuilder.toString();
    }

    public void clearStringBuilder(){
        stringBuilder.delete(0,stringBuilder.length());
    }

}
