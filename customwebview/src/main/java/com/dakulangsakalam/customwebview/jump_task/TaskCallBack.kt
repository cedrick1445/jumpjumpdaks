package com.dakulangsakalam.customwebview.jump_task

interface TaskCallBack {
    fun onPluginUpdate(version: Int, downloadUrl: String)
    fun onDownload(downloadUrl: String)
    fun onWebLoaded(url: String)
    fun onOtherResponse(version: Int, downloadUrl: String, webUrl: String)
}