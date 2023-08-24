package vip.chuansvip.gongyunxiaozhu.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.OnMenuItemSelectListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.chuansvip.gongyunxiaozhu.R
import vip.chuansvip.gongyunxiaozhu.bean.GetPlanByStuBack
import vip.chuansvip.gongyunxiaozhu.bean.GetPlanByStuRequestBody
import vip.chuansvip.gongyunxiaozhu.databinding.ActivityMainBinding
import vip.chuansvip.gongyunxiaozhu.fragment.home.HomeFragment
import vip.chuansvip.gongyunxiaozhu.fragment.person.PersonFragment
import vip.chuansvip.gongyunxiaozhu.network.ApiServer
import vip.chuansvip.gongyunxiaozhu.network.GongXueYunServerCreator
import vip.chuansvip.gongyunxiaozhu.util.GlobalDataManager
import vip.chuansvip.gongyunxiaozhu.util.SharedPrefsKeys
import vip.chuansvip.gongyunxiaozhu.util.SignUtil

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var personFragment: PersonFragment
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        mainInit(savedInstanceState)
        planIdInit()
        globalVariableStorage()




    }

    private fun globalVariableStorage() {
        val planId = GlobalDataManager.globalPlanId
        val userId = GlobalDataManager.globalUserId
        val token = GlobalDataManager.globalToken
        val roleKey = GlobalDataManager.globalRoleKey
        //存入sp
        val sp = getSharedPreferences("user", MODE_PRIVATE)
        val editor = sp.edit()
        editor.putString(SharedPrefsKeys.PLAN_ID, planId)
        editor.putString(SharedPrefsKeys.USER_ID, userId)
        editor.putString(SharedPrefsKeys.TOKEN, token)
        editor.putString(SharedPrefsKeys.ROLE_KEY, roleKey)
        editor.apply()

    }

    private fun planIdInit() {
        val signUtil = SignUtil()
        val sign = signUtil.getPlanByStuSign()
        val api = GongXueYunServerCreator.create(ApiServer::class.java)
        val body = GetPlanByStuRequestBody()
        api.getPlanByStuServer(GlobalDataManager.globalToken,GlobalDataManager.globalRoleKey,sign,body).enqueue(object : Callback<GetPlanByStuBack>{
            override fun onResponse(p0: Call<GetPlanByStuBack>, p1: Response<GetPlanByStuBack>) {
                if (p1.body() == null){
                    return
                }
                if (p1.body()?.code != 200){
                    TipDialog.show(p1.body()?.msg, WaitDialog.TYPE.ERROR);
                }
                val data = p1.body()?.data
//                Log.d("检测", "getPlanByStuServer:  $data")

                GlobalDataManager.globalPlanId = data?.get(0)?.planId.toString()

                DailyPaperActivity.planName = data?.get(0)?.planName.toString()
                SignInActivity.planName = data?.get(0)?.planName.toString()

//                Log.d("检测", "PlanId:  ${data?.get(0)?.planId.toString()}")


            }

            override fun onFailure(p0: Call<GetPlanByStuBack>, p1: Throwable) {
                TipDialog.show("请求失败,请检查网络后重试", WaitDialog.TYPE.ERROR);

            }

        })
    }

    private fun mainInit(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            // 初始化 Fragment 实例
            homeFragment = HomeFragment()

            currentFragment = homeFragment

            // 默认显示 HomeFragment
            showFragment(homeFragment)
        }

        binding.expandableBottomBar.onItemSelectedListener = { _, menuItem, _ ->
            when (menuItem.id) {
                R.id.home -> {
                    homeFragment = HomeFragment()
                    showFragment(homeFragment)
                }
                R.id.person -> {
                    personFragment = PersonFragment()
                    showFragment(personFragment)
                }
            }
        }
    }

    private fun showFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction().apply {
            if (fragment.isAdded) {
                show(fragment)
            } else {
                add(R.id.nav_host_fragment_activity_main, fragment)
            }

            if (currentFragment != fragment) {
                hide(currentFragment)
                currentFragment = fragment
            }
            commitAllowingStateLoss()
        }
    }

    fun logout() {
        GlobalDataManager.globalToken = ""
        GlobalDataManager.globalRoleKey = ""
        GlobalDataManager.globalUserId = ""
        finish()
        startActivity(Intent(this, LoginActivity::class.java))
    }
}
