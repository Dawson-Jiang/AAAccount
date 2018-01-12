package com.dawson.aaaccount.activity

import android.app.Dialog
import android.support.v4.app.Fragment
import android.view.View

open class BaseFragment : Fragment() {

    protected var mProgressDialog: Dialog? = null
    protected var rootView: View? = null

    protected fun cancelDialog() {
        if (mProgressDialog != null && mProgressDialog!!.isShowing) mProgressDialog!!.cancel()
    }
}
