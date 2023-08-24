package vip.chuansvip.gongyunxiaozhu.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import vip.chuansvip.gongyunxiaozhu.R

class NoticeForUseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notice_for_use)
    }
    fun getBack(view:View){
        finish()
    }
}