package com.jkcq.antrouter.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

public class TimeControlUtils {

    public static void isTimeNow(Context context){
        SavePreferencesData savePreferencesData = new SavePreferencesData(context);
        int time = savePreferencesData.getIntegerData("startUseTime2");
        if(time<=0){
            savePreferencesData.putIntegerData("startUseTime2", (int)(System.currentTimeMillis()/1000));
            return;
        }
        int curren = (int)(System.currentTimeMillis()/1000);
        if((curren-time)>=(20*24*60*60)){   //  用了二十天了
            Dialog dialog = ProgressDialog.show(context, "", "授权码过期！");
            dialog.setCancelable(false);
        }
    }
}
