package com.jkcq.antrouter;

import android.app.Application;

import com.jkcq.antrouter.bean.ClubInfo;
import com.tencent.bugly.crashreport.CrashReport;

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

    @Override
    public void onCreate() {
        super.onCreate();
        initApp();
        CrashReport.initCrashReport(getApplicationContext(), "decceb56a0", false);
        //CrashHandler crashHandler = CrashHandler.getInstance();
        //crashHandler.init(getApplicationContext());
    }

}
