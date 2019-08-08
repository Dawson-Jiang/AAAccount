package com.dawson.aaaccount.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Window
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.AnimationSet
import android.widget.Toast
import com.dawson.aaaccount.R
import com.dawson.aaaccount.model.BaseModelFactory
import com.dawson.aaaccount.model.IUserModel
import com.dawson.aaaccount.util.Common
import com.dawson.aaaccount.util.DLog
import com.dawson.aaaccount.util.ImageLoadUtil
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_welcome.*
import java.net.UnknownHostException

class WelcomeActivity : Activity() {
    private var animationSet: AnimationSet = AnimationSet(true)

    internal var handler: Handler = Handler()
    private var animationState = -1//-1 未开始 1开始 0结束
    private var hasInit = false

    private var userModel: IUserModel = BaseModelFactory.factory.createUserModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.activity_welcome)

        animationSet.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {
                animationState = 1
            }

            override fun onAnimationEnd(animation: Animation) {
                handler.postDelayed({
                    animationState = 0
                    goTo()
                }, 1000)
            }

            override fun onAnimationRepeat(animation: Animation) {

            }
        })
        hasInit = false
        userModel.initUser(applicationContext)
                .subscribe({ _ ->
                    if (!isFinishing) {
                        hasInit = true
                        goTo()
                    }
                }, {
                    it.printStackTrace()
                    if (it !is UnknownHostException) DLog.error("initUser", it)
                    Toast.makeText(this@WelcomeActivity, "启动失败", Toast.LENGTH_SHORT).show()
                    finish()
                })
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        if (hasFocus && animationState == -1) {
            startAnimation()
        }
    }

    private fun startAnimation() {
        val alphaAnimation = AlphaAnimation(0.0f, 1.0f)
        alphaAnimation.duration = (1000).toLong()
        animationSet.addAnimation(alphaAnimation)
        view_icon.startAnimation(animationSet)

    }

    @Synchronized
    private fun goTo() {
        if (animationState != 0 || !hasInit) return
        if (userModel.isLogin(this@WelcomeActivity.applicationContext)) {
            startActivity(Intent(this@WelcomeActivity, MainActivity::class.java))
        } else {
            startActivity(Intent(this@WelcomeActivity, LoginActivity::class.java))
        }
        finish()
    }
}
