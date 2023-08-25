package com.jkcq.antrouter.http

import com.jkcq.antrouter.bean.AloneClubInfoBean
import com.jkcq.antrouter.bean.BaseResponse
import com.jkcq.antrouter.bean.TokenBean
import com.jkcq.antrouter.bean.VersionInfo

/**
 *  Created by BeyondWorlds
 *  on 2020/7/7
 */
class NetRepository : BaseRepository() {
    suspend fun getClubInfoByMac(map: Map<String, String>): BaseResponse<AloneClubInfoBean> {
        return RetrofitHelper.noAuthservice.refreshLoginInfo(map)
    }

    suspend fun registerDevice(map: Map<String, String>): BaseResponse<TokenBean> {
        return RetrofitHelper.noAuthservice.deviceRegister(map)
    }

    /**
     *获得心率墙用户登录信息
     */

    suspend fun unRegisterDevice(map: Map<String, String>): BaseResponse<Boolean> {
        return RetrofitHelper.mService.devcieLogout(map)
    }

    suspend fun checkUpdate(deviceId: String): BaseResponse<VersionInfo> {
        return RetrofitHelper.noAuthservice.getVersionInfo(deviceId)
    }


}