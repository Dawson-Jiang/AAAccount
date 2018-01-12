package com.dawson.aaaccount.model

import android.content.Context
import com.dawson.aaaccount.bean.result.OperateResult
import io.reactivex.Observable

/**
 * 大文件操作 上传 下载等
 * Created by Administrator on 2017/5/25.
 */

interface IFileModel {

    fun uploadFile(context: Context, file: String, progressCallback: (progress: Int) -> Unit): Observable<OperateResult<Array<String>>>

    /**
     * 上传多文件
     *
     * @param files
     */
    fun uploadFile(context: Context, files: MutableList<String>, progressCallback: (file: String, progress: Int) -> Unit): Observable<OperateResult<Map<String, Array<String>>>>

    fun downloadFile(url: String, progressCallback: (progress: Int) -> Unit): Observable<OperateResult<String>>
}
