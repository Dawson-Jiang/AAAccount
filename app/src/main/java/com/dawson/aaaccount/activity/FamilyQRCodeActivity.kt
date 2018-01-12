package com.dawson.aaaccount.activity

import android.graphics.Bitmap
import android.os.Bundle
import com.dawson.aaaccount.R
import com.dawson.aaaccount.bean.Family
import com.dawson.aaaccount.util.DLog
import com.dawson.aaaccount.util.QRCodeHelper
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_family_qrcode.*


class FamilyQRCodeActivity : BaseActivity() {
    internal var family: Family? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_family_qrcode)
        family = intent.getSerializableExtra("family") as Family
        if (family == null) {
            finish()
            return
        }
        initCommonTitle()
        // 生成二维码
        Observable.create<Bitmap> { e ->
            e.onNext(QRCodeHelper.getQRCode("familyId:${family?.id!!}", 400, 400)!!)
            e.onComplete()
        }.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ res -> ivQRCode.setImageBitmap(res) }, {
                    DLog.error("familyQR_get", it)
                })
    }

    override fun initCommonTitle() {
        super.initCommonTitle()
        title = family!!.name!!
    }
}
