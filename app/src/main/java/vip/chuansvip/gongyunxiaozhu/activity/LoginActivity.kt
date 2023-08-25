package vip.chuansvip.gongyunxiaozhu.activity

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.chuansvip.gongyunxiaozhu.MyApplication
import vip.chuansvip.gongyunxiaozhu.bean.BaseActivity
import vip.chuansvip.gongyunxiaozhu.bean.GetMoGuDingUserInfoBack
import vip.chuansvip.gongyunxiaozhu.bean.GetPlanByStuBack
import vip.chuansvip.gongyunxiaozhu.bean.GetPlanByStuRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.LoginBack
import vip.chuansvip.gongyunxiaozhu.bean.LoginData
import vip.chuansvip.gongyunxiaozhu.databinding.ActivityLoginBinding
import vip.chuansvip.gongyunxiaozhu.network.ApiServer
import vip.chuansvip.gongyunxiaozhu.network.GongXueYunServerCreator
import vip.chuansvip.gongyunxiaozhu.util.EncryptionAndDecryptUtils
import vip.chuansvip.gongyunxiaozhu.util.GlobalDataManager
import vip.chuansvip.gongyunxiaozhu.util.SharedPrefsKeys
import vip.chuansvip.gongyunxiaozhu.util.SignUtil
import vip.chuansvip.gongyunxiaozhu.util.UpdateUtil
import vip.chuansvip.gongyunxiaozhu.util.makeDebugDialog
import vip.chuansvip.gongyunxiaozhu.util.makeDebugDialogThrowable


class LoginActivity : BaseActivity(), UpdateUtil.UpdateCallback {

    val binding: ActivityLoginBinding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        try {


            determineLoginStatus()
            checkUpdate()
            loginInit()

            binding.checkShowPassword.setOnCheckedChangeListener { buttonView, isChecked ->
                if (isChecked) {
                    binding.edPassword.inputType = 128
                } else {
                    binding.edPassword.inputType = 129
                }
            }
        } catch (e: Exception) {
            makeDebugDialog(this, e)
        }

    }

    private fun planIdInit() {
        val signUtil = SignUtil()
        val sign = signUtil.getPlanByStuSign()
        val api = GongXueYunServerCreator.create(ApiServer::class.java)
        val body = GetPlanByStuRequestBody()
        api.getPlanByStuServer(
            GlobalDataManager.globalToken,
            GlobalDataManager.globalRoleKey,
            sign,
            body
        ).enqueue(object : Callback<GetPlanByStuBack> {
            override fun onResponse(p0: Call<GetPlanByStuBack>, p1: Response<GetPlanByStuBack>) {
                if (p1.body() == null) {
                    return
                }
                Log.d("检测", "onResponse:  ${p1.body()}")
                if (p1.body()!!.msg == "token失效") {
                    val intent = Intent("com.example.broadcastbestpractice.FORCE_OFFLINE")
                    MyApplication.context!!.sendBroadcast(intent)
                } else {

                    val data = p1.body()?.data
//                Log.d("检测", "getPlanByStuServer:  $data")
                    TipDialog.show("登录成功", WaitDialog.TYPE.SUCCESS);

                    GlobalDataManager.globalPlanId = data?.get(0)?.planId.toString()
                    DailyPaperActivity.planName = data?.get(0)?.planName.toString()
                    SignInActivity.planName = data?.get(0)?.planName.toString()

//                Log.d("检测", "PlanId:  ${data?.get(0)?.planId.toString()}")
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }, 2000) // 延迟2秒跳转到首页
                }

            }

            override fun onFailure(call: Call<GetPlanByStuBack>, throwable: Throwable) {
                TipDialog.show("getPlanByStuServer请求异常", WaitDialog.TYPE.ERROR)
                makeDebugDialogThrowable(this@LoginActivity, throwable)
            }


        })
    }

    private fun determineLoginStatus() {
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        val token = sp.getString(SharedPrefsKeys.TOKEN, "")
        val userId = sp.getString(SharedPrefsKeys.USER_ID, "")
        val roleKey = sp.getString(SharedPrefsKeys.ROLE_KEY, "")
        val planId = sp.getString(SharedPrefsKeys.PLAN_ID, "")

        Log.d("检测", "sp:$token,$userId,$roleKey,$planId")


        if (token != "" && userId != "" && roleKey != "") {
            val emptyJson = "{}"
            val requestBody = RequestBody.create("application/json".toMediaType(), emptyJson)
            GlobalDataManager.allServer.getUserInfo(
                token.toString(),
                roleKey.toString(),
                requestBody
            ).enqueue(object : Callback<GetMoGuDingUserInfoBack> {
                override fun onResponse(
                    p0: Call<GetMoGuDingUserInfoBack>,
                    p1: Response<GetMoGuDingUserInfoBack>
                ) {

                    Log.d("检测", "determineLoginStatus: $p1")
                    Log.d("检测", "determineLoginStatus: ${p1.body()}")
                    Log.d("检测", "determineLoginStatus: ${p1.body()!!.code}")

                    if (p1.body()!!.code == 200) {
                        GlobalDataManager.globalPlanId = planId.toString()
                        GlobalDataManager.globalUserId = userId.toString()
                        GlobalDataManager.globalToken = token.toString()
                        GlobalDataManager.globalRoleKey = roleKey.toString()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }

                override fun onFailure(p0: Call<GetMoGuDingUserInfoBack>, p1: Throwable) {
                    Log.d("检测", "determineLoginStatus: $p1")

                }

            })
        }


    }

    fun toNoticeForUseActivity(view: View) {
        startActivity(Intent(this, NoticeForUseActivity::class.java))
    }


    private fun checkUpdate() {
        val handler = Handler(Looper.getMainLooper())
        val updateUtil = UpdateUtil()
        updateUtil.checkVersion(this, handler, this)
    }

    private fun loginInit() {


        //判断是否记住密码
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        val phone = sp.getString("phone", "")
        val password = sp.getString("password", "")
        if (phone != "" && password != "") {
            binding.edPhone.setText(phone)
            binding.edPassword.setText(password)
            binding.checkRememberPhoneAndPassword.isChecked = true
        }
        //登录按钮的点击事件
        binding.btnLogin.setOnClickListener {
            WaitDialog.show("登录中...");
            val phone = binding.edPhone.text.toString()
            val password = binding.edPassword.text.toString()
            //判空
            if (phone.isEmpty() || password.isEmpty()) {
                TipDialog.show("手机号或密码不能为空", WaitDialog.TYPE.ERROR);
                return@setOnClickListener
            }
            //判断手机号是否合法
            if (!phone.matches(Regex("1[3-9]\\d{9}"))) {
                TipDialog.show("手机号不合法", WaitDialog.TYPE.ERROR);
                return@setOnClickListener
            }

            login(phone, password)

        }

    }


    private fun login(phone: String, pwd: String) {
        val encryptionAndDecryptUtils = EncryptionAndDecryptUtils()
        val encryptedPhone = encryptionAndDecryptUtils.encryptAndPrint(phone)
        val encryptedPassword = encryptionAndDecryptUtils.encryptAndPrint(pwd)
        val timestamp = System.currentTimeMillis().toString()
        val encryptedTimestamp = encryptionAndDecryptUtils.encryptAndPrint(timestamp)


        val loginData = LoginData()
        loginData.phone = encryptedPhone
        loginData.password = encryptedPassword
        loginData.t = encryptedTimestamp

        GlobalDataManager.allServer.loginServer(loginData).enqueue(object : Callback<LoginBack> {
            override fun onResponse(call: Call<LoginBack>, response: Response<LoginBack>) {
                val loginBack = response.body()
                if (loginBack == null) {
                    println("请求失败")
                    return
                }
                if (loginBack.code != 200) {
                    TipDialog.show(loginBack.msg, WaitDialog.TYPE.ERROR);
                    return
                }
                if (binding.checkRememberPhoneAndPassword.isChecked) {
                    //保存账号密码
                    val sp = getSharedPreferences("user", MODE_PRIVATE)
                    val editor = sp.edit()
                    editor.putString("phone", phone)
                    editor.putString("password", pwd)
                    editor.apply()
                }
                GlobalDataManager.globalToken = loginBack.data.token
                GlobalDataManager.globalUserId = loginBack.data.userId
                GlobalDataManager.globalRoleKey = loginBack.data.roleKey


                planIdInit()







            }

            override fun onFailure(call: Call<LoginBack>, t: Throwable) {
                TipDialog.show("登录失败,请检查网络后重试", WaitDialog.TYPE.ERROR);
            }
        })
    }

    override fun onVersionCheckComplete(hasUpdate: Boolean) {

    }
}