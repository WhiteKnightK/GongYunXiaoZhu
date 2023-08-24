package vip.chuansvip.gongyunxiaozhu.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import vip.chuansvip.gongyunxiaozhu.R

class LoadPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load_page)

        //延迟两秒后跳转LoginActivity
        val handler = android.os.Handler()
        handler.postDelayed({
            //要执行的操作
            startActivity(android.content.Intent(this,LoginActivity::class.java))
            finish()
        }, 1500)//3秒后执行Runnable中的run方法
    }
}