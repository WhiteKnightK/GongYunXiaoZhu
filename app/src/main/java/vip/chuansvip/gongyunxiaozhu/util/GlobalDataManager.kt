package vip.chuansvip.gongyunxiaozhu.util

import vip.chuansvip.gongyunxiaozhu.network.ApiServer
import vip.chuansvip.gongyunxiaozhu.network.GongXueYunServerCreator

object GlobalDataManager {

    var globalToken = ""
    var globalUserId = ""
    var globalRoleKey = ""
    var globalPlanId = ""



    val screet = "3478cbbc33f84bd00d75d7dfa69e0daa"


    val allServer = GongXueYunServerCreator.create(ApiServer::class.java)
}