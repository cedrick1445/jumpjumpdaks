package com.dakulangsakalam.customwebview.jump_code.presentation

import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.dakulangsakalam.customwebview.R
import com.dakulangsakalam.customwebview.databinding.DownloadProcessBinding
import com.dakulangsakalam.customwebview.jump_code.presentation.DownloadTool.Companion.isFinishDownLoad
import org.wlf.filedownloader.DownloadFileInfo
import org.wlf.filedownloader.FileDownloadConfiguration
import org.wlf.filedownloader.FileDownloader
import org.wlf.filedownloader.listener.OnDeleteDownloadFileListener
import org.wlf.filedownloader.listener.OnDeleteDownloadFileListener.DeleteDownloadFileFailReason
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener
import org.wlf.filedownloader.listener.OnFileDownloadStatusListener.FileDownloadStatusFailReason
import org.wlf.filedownloader.listener.simple.OnSimpleFileDownloadStatusListener
import java.io.File

abstract class DownloadTool: AppCompatActivity() {
    companion object{
        var isFinishDownLoad = true
    }
}

fun DownloadTool.download(url: String){
    val dir: String = externalCacheDir?.absolutePath + File.separator + "FileDownloader"
    val dirApk = dir + url.substring(url.lastIndexOf("/"))
    val apkFile = File(dirApk)

    if (apkFile.exists()) {
        install(apkFile)
    } else {
        downloadApk(url, dir)
    }
}

fun DownloadTool.installApk(filePath: String){
    val apkFile = File(filePath)
    if (!apkFile.exists()) return
    install(apkFile)
}

fun DownloadTool.install(file: File){
    val apkUri: Uri = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        FileProvider.getUriForFile(this, "$packageName.fileProvider", file)
    } else {
        Uri.fromFile(file)
    }
    val intent = Intent(Intent.ACTION_VIEW)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.addCategory(Intent.CATEGORY_DEFAULT)
    intent.setDataAndType(apkUri, "application/vnd.android.package-archive")
    startActivity(intent)
}

fun DownloadTool.goToBrowser(url: String, canDismissDialog: Boolean) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    this.startActivity(intent)
    if (!canDismissDialog) {
        //强制更新前不能使用APP
    }
}


fun DownloadTool.downloadApk(url: String, dir: String){

    if (isFinishDownLoad) isFinishDownLoad = false
    else return

    val builder = FileDownloadConfiguration.Builder(this)
    with(builder){
        configFileDownloadDir(dir)
        configDownloadTaskSize(3)
        configRetryDownloadTimes(5)
        configDebugMode(false)
        configConnectTimeout(25000)
    }

    val configuration = builder.build()
    FileDownloader.init(configuration)

    val downDialog = Dialog(this, R.style.Theme_AppCompat_DayNight_DialogWhenLarge)
    val view = View.inflate(this, R.layout.download_process, null)
    val binding = DownloadProcessBinding.bind(view)
    val params = ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.MATCH_PARENT,
        ViewGroup.LayoutParams.MATCH_PARENT
    )
    with(downDialog){
        setCancelable(false)
        setCanceledOnTouchOutside(false)
        addContentView(binding.root, params)
    }.apply {
        downDialog.show()
    }
    val pb = binding.progressBar
    val tvProgress = binding.tvProgress

    val win = downDialog.window
    win!!.decorView.setPadding(0, 0, 0, 0)
    with(win.attributes){
        width = WindowManager.LayoutParams.MATCH_PARENT
        height = WindowManager.LayoutParams.MATCH_PARENT
    }

    val mOnFileDownloadStatusListener: OnFileDownloadStatusListener =
        object : OnSimpleFileDownloadStatusListener() {
            override fun onFileDownloadStatusRetrying(
                downloadFileInfo: DownloadFileInfo,
                retryTimes: Int
            ) {  }

            override fun onFileDownloadStatusWaiting(downloadFileInfo: DownloadFileInfo) {  }

            override fun onFileDownloadStatusPreparing(downloadFileInfo: DownloadFileInfo) { }

            override fun onFileDownloadStatusPrepared(downloadFileInfo: DownloadFileInfo) {  }

            override fun onFileDownloadStatusDownloading(
                downloadFileInfo: DownloadFileInfo,
                downloadSpeed: Float,
                remainingTime: Long
            ) {

                val totalSize = downloadFileInfo.fileSizeLong.toInt()
                val downloaded = downloadFileInfo.downloadedSizeLong.toInt()
                val temp = downloaded.toFloat() / totalSize.toFloat()
                val progress = (temp * 100).toInt()
                pb.progress = progress
                tvProgress.text = String.format("Updating, please wait... %s%%", progress)
            }

            override fun onFileDownloadStatusPaused(downloadFileInfo: DownloadFileInfo) { }

            override fun onFileDownloadStatusCompleted(downloadFileInfo: DownloadFileInfo) {
                isFinishDownLoad = true
                if (downDialog.isShowing) {
                    downDialog.dismiss()
                    installApk(downloadFileInfo.filePath)
                }
            }

            override fun onFileDownloadStatusFailed(
                url: String,
                downloadFileInfo: DownloadFileInfo,
                failReason: FileDownloadStatusFailReason
            ) {
                val failType = failReason.type

                when {
                    FileDownloadStatusFailReason.TYPE_URL_ILLEGAL == failType -> { }
                    FileDownloadStatusFailReason.TYPE_STORAGE_SPACE_IS_FULL == failType -> { }
                    FileDownloadStatusFailReason.TYPE_NETWORK_DENIED == failType -> { }
                    FileDownloadStatusFailReason.TYPE_NETWORK_TIMEOUT == failType -> { }
                    else -> { }
                }

                val failMsg = failReason.message // 或：failReason.getOriginalCause().getMessage()
                if (failMsg!!.contains("Trust anchor for certification path not found")) FileDownloader.start(url)
                else {
                    if (downDialog.isShowing) downDialog.dismiss()
                    goToBrowser(url, true)
                }
            }
        }
    FileDownloader.registerDownloadStatusListener(mOnFileDownloadStatusListener)

    FileDownloader.delete(url, true, object : OnDeleteDownloadFileListener {
        override fun onDeleteDownloadFilePrepared(downloadFileInfo: DownloadFileInfo) {}
        override fun onDeleteDownloadFileSuccess(downloadFileInfo: DownloadFileInfo) =  FileDownloader.start(url)
        override fun onDeleteDownloadFileFailed(
            downloadFileInfo: DownloadFileInfo,
            deleteDownloadFileFailReason: DeleteDownloadFileFailReason
        ) = FileDownloader.start(url)
    })
}