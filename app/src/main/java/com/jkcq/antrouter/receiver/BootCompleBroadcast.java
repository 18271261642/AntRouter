package com.jkcq.antrouter.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.jkcq.antrouter.activity.SplashActivity;

/*
 *
 * @author mhj
 * Create at 2019/2/22 16:18
 */public class BootCompleBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //监听到开机
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
          Intent intent1 = new Intent(context, SplashActivity.class);
            intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent1);
        }
    }
}
