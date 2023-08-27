package vip.chuansvip.gongyunxiaozhu.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Handler
import androidx.core.content.pm.PackageInfoCompat
import com.google.gson.Gson
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.util.TextInfo
import com.tencent.bugly.crashreport.CrashReport
import vip.chuansvip.gongyunxiaozhu.bean.AppVersionInfo
import vip.chuansvip.gongyunxiaozhu.network.MyApiServer
import vip.chuansvip.gongyunxiaozhu.network.MyServerCreator


class UpdateUtil {

    interface UpdateCallback {
        fun onVersionCheckComplete(hasUpdate: Boolean)
    }

    fun checkVersion(context: Context, handler: Handler, callback: UpdateCallback) {
        val server = MyServerCreator.create(MyApiServer::class.java)

        server.getVersionServer().enqueue(object : retrofit2.Callback<AppVersionInfo> {
            override fun onResponse(
                p0: retrofit2.Call<AppVersionInfo>,
                p1: retrofit2.Response<AppVersionInfo>
            ) {
                if (p1.body() == null) {
                    callback.onVersionCheckComplete(false) // 回调版本检查完成，没有版本更新
                    return
                }
                val versionInfo = p1.body()

                // 获取包管理器
                val packageManager = context.packageManager

                try {
                    // 获取包信息
                    val pInfo = context.getPackageInfo()

                    val longVersionCode = PackageInfoCompat.getLongVersionCode(pInfo)
                    val versionCode = longVersionCode.toInt()

                    if (versionInfo!!.VersionCode > versionCode) {
                        handler.post {
                            showUpdateDialog(
                                context,
                                versionInfo.ModifyContent,
                                versionInfo.DownloadUrl
                            )
                            callback.onVersionCheckComplete(true) // 回调版本检查完成，有版本更新
                        }
                    } else {
                        callback.onVersionCheckComplete(false) // 回调版本检查完成，没有版本更新
                    }
                } catch (e: PackageManager.NameNotFoundException) {
                    e.printStackTrace()
                    callback.onVersionCheckComplete(false) // 回调版本检查完成，出现异常，没有版本更新
                }
            }

            override fun onFailure(p0: retrofit2.Call<AppVersionInfo>, p1: Throwable) {
                callback.onVersionCheckComplete(false) // 回调版本检查完成，请求失败，没有版本更新
                CrashReport.postCatchedException(p1)
            }
        })
    }

    @Suppress("DEPRECATION")
    fun Context.getPackageInfo(): PackageInfo {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            packageManager.getPackageInfo(packageName, PackageManager.PackageInfoFlags.of(0))
        } else {
            packageManager.getPackageInfo(packageName, 0)
        }
    }

    private fun showUpdateDialog(context: Context, content: String, downloadUrl: String) {
        MessageDialog.show("更新提示", content, "前往更新", "取消")
            .setCancelTextInfo(TextInfo().setFontColor(Color.RED))
            .setOkButton { dialog, v ->


                // 创建一个 Intent，设置 Action 为 ACTION_VIEW，指定网站的 Uri
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(downloadUrl))
                context.startActivity(intent)
                false
            }
    }

}