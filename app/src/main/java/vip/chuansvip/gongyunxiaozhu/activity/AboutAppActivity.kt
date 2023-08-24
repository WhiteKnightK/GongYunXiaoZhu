package vip.chuansvip.gongyunxiaozhu.activity

import android.content.ClipData
import android.content.ClipboardManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.kongzue.dialogx.dialogs.PopTip
import vip.chuansvip.gongyunxiaozhu.bean.BaseActivity
import vip.chuansvip.gongyunxiaozhu.databinding.ActivityAboutAppBinding
import vip.chuansvip.gongyunxiaozhu.util.UpdateUtil
import vip.chuansvip.gongyunxiaozhu.util.joinQQGroup

class AboutAppActivity : BaseActivity(), UpdateUtil.UpdateCallback {
    lateinit var binding: ActivityAboutAppBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutAppBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkUpdate()

        binding.tvIsNewestVersion.setOnClickListener {
            checkUpdate()
        }


        binding.btnAboutAppBack.setOnClickListener {
            finish()
        }
        binding.tvAppVersion.text = getVersionName()

        binding.tvQQGroupNumber.setOnClickListener {
            //将727286533写入剪切板
//            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
//            val clipData = ClipData.newPlainText("text", "727286533")
//            clipboard.setPrimaryClip(clipData)
//            //提示
//            PopTip.show("复制成功");
            joinQQGroup("kEfI5W8unKVYRSpvMWBvhJICiBPYZPfC",this)
        }
        binding.tvEmailAboutApp.setOnClickListener {
            //将727286533写入剪切板
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText("text", "2601213628@qq.com")
            clipboard.setPrimaryClip(clipData)
            PopTip.show("复制成功");


        }


    }

    private fun checkUpdate() {
        val handler = Handler(Looper.getMainLooper())
        val updateUtil = UpdateUtil()
        updateUtil.checkVersion(this, handler,this)
    }

    //写一个获取当前软件版本的方法 获取版本号和版本名
    private fun getVersionName(): String {
        //获取包管理器
        val packageManager = packageManager
        //获取包信息
        val packageInfo = packageManager.getPackageInfo(packageName, 0)
        //获取版本号
        val versionCode = packageInfo.versionCode
        //获取版本名
        val versionName = packageInfo.versionName
        return "版本：$versionCode 版本名：$versionName - 正式版"
    }

    override fun onVersionCheckComplete(hasUpdate: Boolean) {
        if (hasUpdate) {
            binding.tvIsNewestVersion.text = "有新版本 点此检查更新"
        } else {
            binding.tvIsNewestVersion.text = "已是最新版本"
        }
    }
}