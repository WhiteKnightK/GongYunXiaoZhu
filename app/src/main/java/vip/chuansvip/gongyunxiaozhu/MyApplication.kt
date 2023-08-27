package vip.chuansvip.gongyunxiaozhu

import android.app.Application
import android.content.Context
import android.os.Build
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.style.IOSStyle
import com.tencent.bugly.crashreport.CrashReport
import com.tencent.bugly.crashreport.CrashReport.UserStrategy

class MyApplication : Application() {
    companion object{
        lateinit var context:Context
    }
    override fun onCreate() {
        super.onCreate()
        //初始化
        context = applicationContext

        val deviceModel = Build.MODEL
        val strategy = UserStrategy(applicationContext)
        strategy.deviceModel = deviceModel;
        CrashReport.initCrashReport(context, "35530b96f4", true, strategy);
        DialogX.init(this);
        dialogXInit()

    }

    fun dialogXInit(){
        //开启调试模式，在部分情况下会使用 Log 输出日志信息
        DialogX.init(this)
        DialogX.DEBUGMODE = true;

//设置主题样式
        DialogX.globalStyle = IOSStyle.style()

//设置亮色/暗色（在启动下一个对话框时生效）
        DialogX.globalTheme = DialogX.THEME.LIGHT;

//设置 InputDialog 自动弹出键盘
        DialogX.autoShowInputKeyboard = true;

//限制 PopTip 一次只显示一个实例（关闭后可以同时弹出多个 PopTip）
        DialogX.onlyOnePopTip = true;

//是否自动在主线程执行
        DialogX.autoRunOnUIThread = true;

//使用振动反馈（影响 WaitDialog、TipDialog）
        DialogX.useHaptic = true;


    }
}