package com.jkcq.appupdate

import android.app.Activity
import android.app.Dialog
import android.view.Gravity
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


/**
 * @author WuJianhua
 */
class UpdateDialog(private val mContext: Activity, type: Int, values: String) : Dialog(mContext, R.style.SimpleDialogStyle) {

    private var clickListener: OnDialogClickListener? = null

    private var tv_progress_value : TextView ?= null
    private var view_progress : ProgressView?= null
    private var tv_packge : TextView ?= null



    init {
        val view = LayoutInflater.from(mContext).inflate(R.layout.dialog_update_layout, null)
        setContentView(view)
        setCancelable(false)
        setCanceledOnTouchOutside(false)


        val cancel_upload : TextView = view.findViewById(R.id.cancel_upload)
        tv_progress_value = view.findViewById(R.id.tv_progress_value)
        view_progress = findViewById(R.id.view_progress)
        tv_packge = findViewById(R.id.tv_packge)

        //设置Dialog大小位置
        val dialogWindow = window
        val lp = dialogWindow!!.attributes
        dialogWindow.setGravity(Gravity.CENTER)
//        int desity = (int) ScreenUtils.getScreenDensity();
//        lp.width= LinearLayout.LayoutParams.MATCH_PARENT;
//        lp.height=LinearLayout.LayoutParams.WRAP_CONTENT;
        lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
        lp.width = (AppUpdateUtils.getScreenWidth(mContext) * 0.8).toInt()
        dialogWindow.attributes = lp
        if(AppUpdateUtils.isUpdateMandatory){
            cancel_upload.visibility= View.GONE
        }
      cancel_upload.setOnClickListener { clickListener?.dialogClickType(1) }
    }
    fun updateProgress(progress: Long) {
        tv_progress_value?.setText(progress.toString() + "%")
//        Log.e("updateProgress", ""+progress)
      view_progress?.setProgress(progress)
    }



    fun setTvPackgeSize(size: Double) {
        val tips = mContext.resources.getString(R.string.package_size,""+size)
//        tv_packge.setText(mContext.resources.getString(R.string.package_size,""+size))
        tv_packge?.setText("更新包大小:"+size+" M")
    }

    fun setOnDialogClickListener(clickListener: OnDialogClickListener) {
        this.clickListener = clickListener
    }

    fun onClick() {
    }
}
