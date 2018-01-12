package com.dawson.aaaccount.model.leancloud

import android.content.Context
import com.avos.avoscloud.AVFile
import com.dawson.aaaccount.bean.result.OperateResult
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.model.IFileModel
import com.dawson.aaaccount.util.BitmapHelper
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import java.util.*

/**
 * 文件操作
 * Created by Administrator on 2017/5/25.
 */

class FileModel : IFileModel {

    override fun uploadFile(context: Context, file: String, progressCallback: (progress: Int) -> Unit): Observable<OperateResult<Array<String>>> {
        return Observable.create<String> { e ->
            uploadFiles.clear()
            var tmpFileName = file
            if (file.endsWith(".jpg") || file.endsWith(".png"))
                tmpFileName = BitmapHelper.compressImageTwo(file)
            e.onNext(tmpFileName)
        }
                .subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.io())
                .map<OperateResult<Array<String>>> { tmpFileName ->
                    val fileName = file.substring(file.lastIndexOf("/") + 1)
                    val avFile = AVFile.withAbsoluteLocalPath(fileName, tmpFileName)
                    avFile.save()
                    uploadFiles.put(avFile.url, avFile)
                    OperateResult(arrayOf(avFile.url, avFile.getThumbnailUrl(true, Common.THUMB_SIZE, Common.THUMB_SIZE)))
                }
    }

    override fun uploadFile(context: Context, files: MutableList<String>, progressCallback: (file: String, progress: Int) -> Unit):
            Observable<OperateResult<Map<String, Array<String>>>> {
        val obss = mutableListOf<Observable<OperateResult<Array<String>>>>()
        files.forEach {
            obss.add(uploadFile(context, it) { progress -> progressCallback(it, progress) })
        }
        return Observable.zipIterable<OperateResult<Array<String>>, OperateResult<Map<String, Array<String>>>>(obss, {
            val resMap = mutableMapOf<String, Array<String>>()
//            val orgRes = it as Array<OperateResult<Array<String>>>
            it.forEachIndexed { i, or ->
                val r = or as OperateResult<Array<String>>
                resMap.put(files[i], r.content!!)
            }
            OperateResult(resMap.toMap())
        }, false, 1024).subscribeOn(Schedulers.io())
    }

    override fun downloadFile(url: String, progressCallback: (progress: Int) -> Unit): Observable<OperateResult<String>> {
        return Observable.just(OperateResult(""))
    }

    companion object {
        var uploadFiles: MutableMap<String, AVFile> = HashMap()
    }
}
