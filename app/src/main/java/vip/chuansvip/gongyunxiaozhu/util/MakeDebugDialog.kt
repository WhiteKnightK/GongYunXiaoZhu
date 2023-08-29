package vip.chuansvip.gongyunxiaozhu.util

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip

public fun makeDebugDialog(context: Context?, ignored: Exception) {
    if (context == null) return
    ignored.printStackTrace()
    val stringBuffer = StringBuffer()
    stringBuffer.append(ignored.message).append("\n")
    for (throwable in ignored.stackTrace) {
        stringBuffer.append(throwable.toString()).append("\n")
    }

    MessageDialog.show("出现异常报错，点击确定将报错信息发送给管理员", stringBuffer.toString(), "确定")
        .setOkButtonClickListener { baseDialog, v ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", stringBuffer.toString())
            clipboard.setPrimaryClip(clipData)
            PopTip.show("报错信息复制成功")

               joinQQGroup("kEfI5W8unKVYRSpvMWBvhJICiBPYZPfC",context)
            false
        }
        .setCancelable(false)
}

public fun makeDebugDialogThrowable(context: Context?, throwable: Throwable) {
    if (context == null) return
    throwable.printStackTrace()
    val stringBuffer = StringBuffer()
    stringBuffer.append(throwable.message).append("\n")
    for (throwableElement in throwable.stackTrace) {
        stringBuffer.append(throwableElement.toString()).append("\n")
    }

    MessageDialog.show("出现异常报错，点击确定将报错信息发送给管理员", stringBuffer.toString(), "确定")
        .setOkButtonClickListener { baseDialog, v ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", stringBuffer.toString())
            clipboard.setPrimaryClip(clipData)
            PopTip.show("报错信息复制成功")

            joinQQGroup("kEfI5W8unKVYRSpvMWBvhJICiBPYZPfC",context)




            false
        }
        .setCancelable(false)
}

fun makeDebugDialogError(context: Context?, error: Error) {
    if (context == null) return

    error.printStackTrace()
    val stringBuffer = StringBuffer()
    stringBuffer.append("Error: ").append(error.message).append("\n")

    for (throwableElement in error.stackTrace) {
        stringBuffer.append(throwableElement.toString()).append("\n")
    }

    MessageDialog.show("出现异常报错，点击确定将报错信息发送给管理员", stringBuffer.toString(), "确定")
        .setOkButtonClickListener { _, _ ->
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", stringBuffer.toString())
            clipboard.setPrimaryClip(clipData)
            PopTip.show("报错信息复制成功")

            // joinQQGroup("kEfI5W8unKVYRSpvMWBvhJICiBPYZPfC", context) // 如果需要加入 QQ 群，取消注释这一行

            false
        }
        .setCancelable(false)
}

