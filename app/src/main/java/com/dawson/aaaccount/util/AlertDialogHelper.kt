package com.dawson.aaaccount.util

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView

import com.dawson.aaaccount.R
import kotlinx.android.synthetic.main.dialog_choose_photo.view.*

object AlertDialogHelper {
    /**
     * 显示提示框
     *
     * @param context
     * @param messageId
     * @return
     */
    fun showOKAlertDialog(context: Context, messageId: Int) {
        AlertDialog.Builder(context).setTitle(R.string.title_notice)
                .setMessage(messageId)
                .setPositiveButton(R.string.confirm, null).create().show()
    }

    /**
     * 显示提示框
     *
     * @param context
     * @param messageId
     * @param listener
     * @return
     */
    fun showOKAlertDialog(context: Context, messageId: Int,
                          listener: DialogInterface.OnClickListener?) {
        AlertDialog.Builder(context).setTitle(R.string.title_notice)
                .setMessage(messageId)
                .setPositiveButton(R.string.confirm, listener).create().show()
    }

    /**
     * 显示确定 取消询问框
     *
     * @param context
     * @param messageId
     * @return
     */
    fun showOKCancelAlertDialog(context: Context, messageId: Int) {
        AlertDialog.Builder(context).setTitle(R.string.title_ask)
                .setMessage(messageId)
                .setPositiveButton(R.string.confirm, null)
                .setNegativeButton(R.string.cancel, null).create().show()
    }

    /**
     * 显示确定 取消询问框
     *
     * @param context
     * @param messageId
     * @return
     */
    fun showOKCancelAlertDialog(context: Context, messageId: Int, onsure: (dialog: DialogInterface, which: Int) -> Unit,
                                oncancle: (dialog: DialogInterface, which: Int) -> Unit) {
        AlertDialog.Builder(context).setTitle(R.string.title_ask)
                .setMessage(messageId)
                .setPositiveButton(R.string.confirm, onsure)
                .setNegativeButton(R.string.cancel, oncancle).create().show()
    }

    /**
     * 显示 是否 询问框
     *
     * @param context
     * @return
     */
    fun showYesOrNoAlertDialog(context: Context, messageId: Int) {
        AlertDialog.Builder(context).setTitle(R.string.title_ask)
                .setMessage(messageId).setPositiveButton(R.string.yes, null)
                .setNegativeButton(R.string.no, null).create().show()
    }

    /**
     * 显示等待框
     *
     * @param context
     * @return
     */
    fun showWaitProgressDialog(context: Context,
                               messageId: Int): Dialog {
        val progressDialog = Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar)
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.dialog_loading, null)
        (v.findViewById(R.id.tipTextView) as TextView).setText(messageId)
        progressDialog.setContentView(v)
        progressDialog.setCancelable(false)
        progressDialog.show()
        return progressDialog
    }

    fun showPhotoChooseDialog(context: Context, listener: (dialog: DialogInterface, which: Int) -> Unit): Dialog {
        val dialog = Dialog(context, android.R.style.Theme_Holo_Light_Dialog_NoActionBar)
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.dialog_choose_photo, null)
        v.item_camera.setOnClickListener { _ ->
            listener(dialog, 1)
            dialog.dismiss()
        }
        v.item_Photo.setOnClickListener { _ ->
            listener(dialog, 2)
            dialog.dismiss()
        }
        dialog.setContentView(v)
        dialog.show()
        return dialog
    }
}
