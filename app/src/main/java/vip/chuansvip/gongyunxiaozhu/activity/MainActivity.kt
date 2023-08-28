package vip.chuansvip.gongyunxiaozhu.activity

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import vip.chuansvip.gongyunxiaozhu.R
import vip.chuansvip.gongyunxiaozhu.bean.BaseActivity
import vip.chuansvip.gongyunxiaozhu.databinding.ActivityMainBinding
import vip.chuansvip.gongyunxiaozhu.fragment.home.HomeFragment
import vip.chuansvip.gongyunxiaozhu.fragment.person.PersonFragment
import vip.chuansvip.gongyunxiaozhu.util.GlobalDataManager
import vip.chuansvip.gongyunxiaozhu.util.SharedPrefsKeys


class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var homeFragment: HomeFragment
    private lateinit var personFragment: PersonFragment
    private lateinit var currentFragment: Fragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)



            mainInit(savedInstanceState)

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




    private fun mainInit(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            homeFragment = HomeFragment()
            currentFragment = homeFragment
            showFragment(homeFragment)
        }

        binding.expandableBottomBar.onItemSelectedListener = { _, menuItem, _ ->
            when (menuItem.id) {
                R.id.home -> {
                    showFragment(homeFragment)
                }

                R.id.person -> {
                    if (!::personFragment.isInitialized) {
                        personFragment = PersonFragment()
                    }
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
