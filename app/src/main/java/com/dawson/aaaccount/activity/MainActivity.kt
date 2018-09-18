package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.support.v4.view.GravityCompat
import android.view.Window
import com.dawson.aaaccount.R
import com.dawson.aaaccount.fragment.DayBookFragment
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.model.IUserModel
import com.dawson.aaaccount.model.myaliyun.UserInstance
import com.dawson.aaaccount.model.myaliyun.UserModel
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.util.ErrorCode
import com.dawson.aaaccount.util.ImageLoadUtil
import com.dawson.aaaccount.util.OperateCode
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.layout_main_nav.view.*
import java.util.concurrent.TimeUnit

class MainActivity : FragmentActivity() {

    private val userModel: IUserModel = BaseModelFactory.factory.createUserModel()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContentView(R.layout.activity_main)
        ivOperate.setOnClickListener { _ -> layout_main.openDrawer(GravityCompat.START) }
        layoutTitle.setOnClickListener { _ -> (fg_daybook as DayBookFragment).gotoSelectFamily() }
        main_floatbtn.setOnClickListener { _ -> (fg_daybook as DayBookFragment).gotoAdd() }
        initDrawerLayout()
//        SettleModel().syncData(applicationContext, true).subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete { DLog.i("syncData", "syncData complete") }
//                .subscribe({ DLog.i("syncData", "syncData subscribe") },
//                        { DLog.i("syncData", it.message!!) })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus) initMeInfo()
    }

    override fun setTitle(title: CharSequence?) {
        super.setTitle(title)
        tv_title.text = title
    }

    /**
     * inflateHeaderView 进来的布局要宽一些
     */
    private fun initDrawerLayout() {
        nav_view.inflateHeaderView(R.layout.layout_main_nav)
        nav_view.getHeaderView(0).iv_head.setOnClickListener { _ ->
            goto({
                startActivityForResult(Intent(this, EditUserActivity::class.java), OperateCode.MODIFIED_BASIC)
            })
        }
        nav_view.getHeaderView(0).ll_nav_family.setOnClickListener { _ ->
            goto({
                startActivityForResult(Intent(this, FamilyActivity::class.java), OperateCode.MODIFIED_BASIC)
            })
        }
        nav_view.getHeaderView(0).ll_nav_statistic.setOnClickListener { _ ->
            goto({
                startActivity(Intent(this, StatisticsActivity::class.java))
            })
        }
//        nav_view.getHeaderView(0).ll_nav_settle.setOnClickListener { _ ->
//            goto({
//                startActivity(Intent(this, SettleListActivity::class.java))
//            })
//        }

        nav_view.getHeaderView(0).ll_nav_fb.setOnClickListener { _ ->
            goto({
                startActivity(Intent(this, FeedBackActivity::class.java))
            })
        }
        nav_view.getHeaderView(0).ll_nav_about.setOnClickListener { _ ->
            goto({
                startActivity(Intent(this, AboutActivity::class.java))
            })
        }
        nav_view.getHeaderView(0).ll_nav_logout.setOnClickListener { _ ->
            userModel.logout(this)
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe { result ->
                        if (result.result == ErrorCode.SUCCESS) {
                            goto({
                                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                            })
                            finish()
                        } else {
                            goto({
                                Common.showErrorInfo(this@MainActivity, result.errorCode,
                                        R.string.operate_fail, 0)
                            })
                        }
                    }
        }
    }

    private fun goto(body: () -> Unit) {
        layout_main.closeDrawer(GravityCompat.START)
        layout_main.postDelayed({
            body()
        }, 100)
    }

    private fun initMeInfo() {
        Observable.just(1)
                .delay(1000, TimeUnit.MILLISECONDS, Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    ImageLoadUtil.loadCircleImage(userModel.currentUser?.headUrl, nav_view.getHeaderView(0).iv_head)
                    nav_view.getHeaderView(0).tv_name.text = userModel.currentUser?.name
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OperateCode.MODIFIED_BASIC && resultCode == Activity.RESULT_OK) {
            initMeInfo()
            (fg_daybook as DayBookFragment).refreshBasicInfo()
        }
    }
}
