package vip.chuansvip.gongyunxiaozhu.util

import java.security.MessageDigest

class SignUtil {

    fun getPlanByStuSign() : String{
        val s = GlobalDataManager.globalUserId + GlobalDataManager.globalRoleKey + GlobalDataManager.screet
        val sign = getMd5(s)
        return sign
    }
    fun getDailyPaperSign(reportType:String,title:String) : String{
        val s = GlobalDataManager.globalUserId + reportType+GlobalDataManager.globalPlanId+title + GlobalDataManager.screet
        val sign = getMd5(s)
        return sign
    }


    fun getListByStuSign(reportType:String) : String{
        val s = GlobalDataManager.globalUserId + GlobalDataManager.globalRoleKey + reportType + GlobalDataManager.screet
        val sign = getMd5(s)
        return sign
    }

    fun getSignInSign(device:String,type:String,address:String) : String{
        val s = device+type + GlobalDataManager.globalPlanId+ GlobalDataManager.globalUserId + address  + GlobalDataManager.screet
        val sign = getMd5(s)
        return sign
    }

    fun getMd5(s: String): String {
        val md = MessageDigest.getInstance("MD5")
        val digest = md.digest(s.toByteArray(Charsets.UTF_8))
        val getMd5 = digest.joinToString("") { String.format("%02x", it) }
        return getMd5
    }

}








