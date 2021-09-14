package com.jkcq.antrouter.utils

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

/**
 *  Created by BeyondWorlds
 *  on 2020/7/8
 */
object PermissionUtil {

    val TAG = PermissionUtil.javaClass.simpleName
    val REQUEST_DEFAULT = 11

    var mPermissonCallback: OnPermissonCallback? = null

    /**
     * 检查权限
     */
    fun checkPermission(activity: Activity, permissons: Array<String>, code: Int = REQUEST_DEFAULT, permissonCallback: OnPermissonCallback) {
        mPermissonCallback = permissonCallback
        val denyPermisson = findDeniedPermissions(activity, permissons)
        if (denyPermisson.size > 0) {
            ActivityCompat.requestPermissions(activity, denyPermisson.toTypedArray(), code)
        } else {
            mPermissonCallback?.isGrant(true)
        }

    }

    /**
     * 查找未同意的权限
     */
    fun findDeniedPermissions(activity: Activity, permissons: Array<String>): List<String> {
        val denyPermissions = ArrayList<String>()
        for (permisson in permissons) {
            if (ContextCompat.checkSelfPermission(activity, permisson) != PackageManager.PERMISSION_GRANTED) {
                denyPermissions.add(permisson)
            }
        }
        return denyPermissions
    }

    /**
     * 权限请求结果处理
     */
    fun onRequestPermissionsResult(requestCode: Int = REQUEST_DEFAULT, permissions: Array<out String>, grantResults: IntArray) {
        if(permissions.size>0&&grantResults.size>0){
            LogUtil.e(TAG, "permissions=" + permissions[0] + "grants=" + grantResults[0])
        }
        var isGrant = true
        when (requestCode) {
            REQUEST_DEFAULT -> {
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        isGrant = false
                    }
                }
                mPermissonCallback?.isGrant(isGrant)
            }

        }
    }

    interface OnPermissonCallback {
        fun isGrant(grant: Boolean)
    }
}