package com.jkcq.appupdate

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.content.FileProvider
import com.liulishuo.okdownload.DownloadListener
import com.liulishuo.okdownload.DownloadTask
import com.liulishuo.okdownload.OkDownload
import com.liulishuo.okdownload.core.breakpoint.BreakpointInfo
import com.liulishuo.okdownload.core.cause.EndCause
import com.liulishuo.okdownload.core.cause.ResumeFailedCause
import java.io.File

/**
 * created by wq on 2019/6/25
 */
class ApkDownLoadManager(private val mActivity: Activity) {
    var task: DownloadTask? = null
    private var fileTotalLength: Long = 1
    private var increaseLength: Long = 0
    private val mFileName = "AntRouter.apk"
    private val mHandler = Handler(Looper.getMainLooper())
    var updateDialog: UpdateDialog? = null
    fun startDownLoad(apkDownloadUrl: String) {
        updateDialog =
            UpdateDialog(mActivity, 1, mActivity.resources.getString(R.string.downloading))
        updateDialog!!.setOnDialogClickListener(updateDialogListener)
        updateDialog!!.show()
        downloadAPK(apkDownloadUrl, mFileName)
    }

    private val updateDialogListener = OnDialogClickListener {
        updateDialog!!.dismiss()
        task!!.cancel()
        increaseLength = 0
        fileTotalLength = 1
        if (AppUpdateUtils.isUpdateMandatory) {
            mActivity.finish()
        }
    }

    /**
     * 下载apk
     *
     * @param url
     * @param fileName
     */
    fun downloadAPK(url: String, fileName: String?) {
        task = DownloadTask.Builder(
            url,
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        )
            .setFilename(fileName)
            .setReadBufferSize(8192)
            .setFlushBufferSize(30768) // the minimal interval millisecond for callback progress
            .setMinIntervalMillisCallbackProcess(100) // do re-download even if the task has already been completed in the past.
            .setPassIfAlreadyCompleted(false) //                .setAutoCallbackToUIThread(true)
            .build()
        task?.enqueue(mDownloadListener)
    }

    /**
     * 通过隐式意图调用系统安装程序安装APK
     */
    fun install(context: Context, file: File?) {
        AppUpdateUtils.isDownLoad = true
        updateDialog!!.dismiss()
        val intent = Intent(Intent.ACTION_VIEW)
        // 由于没有在Activity环境下启动Activity,设置下面的标签
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        if (Build.VERSION.SDK_INT >= 24) { //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致   参数3  共享的文件
//            Uri apkUri = FileProvider.getUriForFile(context, "com.jkcq.gym.phone.fileProvider", file);
            val apkUri = FileProvider.getUriForFile(
                context,
                "com.jkcq.useralone.antrouter.fileProvider",
                file!!
            )
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
        } else {
            intent.setDataAndType(
                Uri.fromFile(file),
                "application/vnd.android.package-archive"
            )
        }
        context.startActivity(intent)
    }

    var mDownloadListener: DownloadListener = object : DownloadListener {
        override fun taskStart(task: DownloadTask) {
            Log.e(TAG, "-------taskStart----")
        }

        override fun connectTrialStart(
            task: DownloadTask,
            requestHeaderFields: Map<String, List<String>>
        ) {
            Log.e(TAG, "-------connectTrialStart----")
        }

        override fun connectTrialEnd(
            task: DownloadTask,
            responseCode: Int,
            responseHeaderFields: Map<String, List<String>>
        ) {
            Log.e(TAG, "-------connectTrialEnd----")
        }

        override fun downloadFromBeginning(
            task: DownloadTask,
            info: BreakpointInfo,
            cause: ResumeFailedCause
        ) {
            Log.e(TAG, "-------downloadFromBeginning----")
            fileTotalLength = info.totalLength
            updateDialog!!.setTvPackgeSize(
                AppUpdateUtils.div(
                    fileTotalLength.toDouble(),
                    1024 * 1024.toDouble(),
                    2
                )
            )
        }

        override fun downloadFromBreakpoint(task: DownloadTask, info: BreakpointInfo) {
            Log.e(TAG, "-------downloadFromBreakpoint----" + " offset=" + info.totalOffset)
            increaseLength = OkDownload.with().breakpointStore()[task.id]!!.totalOffset
            fileTotalLength = info.totalLength
            updateDialog!!.setTvPackgeSize(
                AppUpdateUtils.div(
                    fileTotalLength.toDouble(),
                    1024 * 1024.toDouble(),
                    2
                )
            )
        }

        override fun connectStart(
            task: DownloadTask,
            blockIndex: Int,
            requestHeaderFields: Map<String, List<String>>
        ) {
            Log.e(TAG, "-------connectStart----")
        }

        override fun connectEnd(
            task: DownloadTask,
            blockIndex: Int,
            responseCode: Int,
            responseHeaderFields: Map<String, List<String>>
        ) {
            Log.e(TAG, "-------connectEnd----")
        }

        override fun fetchStart(task: DownloadTask, blockIndex: Int, contentLength: Long) {
            Log.e(TAG, "-------fetchStart----")
        }

        override fun fetchProgress(task: DownloadTask, blockIndex: Int, increaseBytes: Long) {
            Log.e(
                TAG,
                "-------fetchProgress----$increaseBytes increaseLength=$increaseLength fileTotalLength=$fileTotalLength"
            )
            increaseLength = increaseLength + increaseBytes
            updateDialog!!.updateProgress(increaseLength * 100 / fileTotalLength)
            //                mHandler.postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//
//                    }
//                },500);
//                updateDialog.updateProgress((float) Arith.div(increaseLength,fileTotalLength,0));
        }

        override fun fetchEnd(task: DownloadTask, blockIndex: Int, contentLength: Long) {
            Log.e(TAG, "-------fetchEnd----")

            val file = File(
                Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                mFileName
            )
            Log.e("ApkDownLoadManager", file.absolutePath)
            install(mActivity, file)
        }

        override fun taskEnd(task: DownloadTask, cause: EndCause, realCause: Exception?) {
            Log.e(
                TAG,
                "-------taskEnd----" + cause.name + " realCause=" + realCause?.message + " task.getId()=" + task.id
            )
            //下载完成
//                OkDownload.with().breakpointStore().remove(task.getId());
            if (cause.name == EndCause.ERROR.name) {
                task.enqueue(this)
                return
            }
            if (cause.name == EndCause.COMPLETED.name) {

            }
            //                if (increaseLength == fileTotalLength) {
//                    increaseLength = 0;
//                    fileTotalLength = 0;
//                    updateDialog.dismiss();
//                    // 下载完成后，开启系统安装apk功能！
//                    File file = new File(
//                            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
//                            , mFileName);
//                    Log.e("ApkDownLoadManager", file.getAbsolutePath());
//                    install(mActivity, file);
////                    Intent intent = new Intent();
////                    intent.setAction("android.intent.action.VIEW");
////                    intent.addCategory("android.intent.category.DEFAULT");
////                    intent.setDataAndType(
////                            Uri.parse("file:" + new File(MyFileUtil.getVideoDir() + "/" + fileName).getAbsolutePath()),
////                            "application/vnd.android.package-archive");
////                    startActivityForResult(intent, 1);
//                }
        }
    }

    companion object {
        private val TAG = ApkDownLoadManager::class.java.simpleName
    }

}