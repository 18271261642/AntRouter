package com.jkcq.antrouter.http

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo

/**
 *  Created by BeyondWorlds
 *  on 2020/7/28
 */
object NetUtil {

    /**
     * check NetworkConnected
     *
     * @param context
     * @return
     */
    fun isNetworkConnected(context: Context): Boolean {
        val manager =
            context.applicationContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val info = manager.activeNetworkInfo
        return !(null == info || !info.isConnected)
    }

    fun hasNetwork(context: Context): Boolean {
        val con = context
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val workinfo = con.activeNetworkInfo
        return if (workinfo == null || !workinfo.isAvailable) {
            false
        } else true
    }

    /**
     * 判断是否是wifi连接
     */
    fun checkWifiState(context: Context): Boolean {
        var isWifiConnect = true
        val cm =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfos = cm.allNetworkInfo
        for (i in networkInfos.indices) {
            if (networkInfos[i].state == NetworkInfo.State.CONNECTED) {
                if (networkInfos[i].type == ConnectivityManager.TYPE_MOBILE) {
                    isWifiConnect = false
                }
                if (networkInfos[i].type == ConnectivityManager.TYPE_WIFI) {
                    isWifiConnect = true
                }
            }
        }
        return isWifiConnect
    }
}