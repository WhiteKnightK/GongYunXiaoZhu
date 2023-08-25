package vip.chuansvip.gongyunxiaozhu.network

import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query
import vip.chuansvip.gongyunxiaozhu.bean.AppBannerInfo
import vip.chuansvip.gongyunxiaozhu.bean.AppServer
import vip.chuansvip.gongyunxiaozhu.bean.AppVersionInfo
import vip.chuansvip.gongyunxiaozhu.bean.DailyPaperRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.FeedBackRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.FeedBackResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.GetMoGuDingUserInfoBack
import vip.chuansvip.gongyunxiaozhu.bean.GetPaperResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.GetPaperResponseBodyData
import vip.chuansvip.gongyunxiaozhu.bean.GetPlanByStuBack
import vip.chuansvip.gongyunxiaozhu.bean.GetPlanByStuRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.ListByStuBack
import vip.chuansvip.gongyunxiaozhu.bean.ListByStuResRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.LoginBack
import vip.chuansvip.gongyunxiaozhu.bean.LoginData
import vip.chuansvip.gongyunxiaozhu.bean.LogoutBack
import vip.chuansvip.gongyunxiaozhu.bean.RewindSignInRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.RewindSignInResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInListSynchroRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInListSynchroResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInListSynchroResponseBodyData
import vip.chuansvip.gongyunxiaozhu.bean.SignInRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInResponseBody

interface ApiServer {
    @Headers("Content-Type: application/json; charset=utf-8")
    @POST("/session/user/v3/login")
    fun loginServer(@Body loginData: LoginData) : Call<LoginBack>

    @POST("/usercenter/user/v2/info")
    fun getUserInfo(@Header("Authorization") token:String,@Header("Rolekey") type:String,@Body  requestBody: RequestBody) : Call<GetMoGuDingUserInfoBack>

    @POST("/usercenter/user/v1/logout")
    fun logoutServer(@Header("Authorization") token:String,@Header("Rolekey") type:String) : Call<LogoutBack>

    @POST("/practice/plan/v3/getPlanByStu")
    fun getPlanByStuServer(@Header("Authorization") token:String,@Header("Rolekey") type:String,@Header("Sign") sign:String,@Body body:GetPlanByStuRequestBody) : Call<GetPlanByStuBack>

    @POST("/practice/paper/v2/save")
    fun submitDayReports(@Header("Authorization") token:String,@Header("Sign") sign:String,@Header("Rolekey") rolekey:String,@Body dailyPaperRequestBody: Any) : Call<LoginBack>

    @POST("/practice/paper/v2/listByStu")
    fun listByStuServer(@Header("Authorization") token:String,@Header("Sign") sign:String,@Header("Rolekey") rolekey:String,@Body listByStuResRequestBody: ListByStuResRequestBody) : Call<ListByStuBack>

    @POST("/attendence/clock/v2/save")
    fun signInServer(@Header("Authorization") token:String,@Header("Sign") sign:String,@Header("Rolekey") rolekey:String,@Body SignInRequestBody: SignInRequestBody ) : Call<SignInResponseBody>

    @POST("attendence/clock/v1/listSynchro")
    fun getSignInListServer(@Header("Authorization") token:String,@Header("Rolekey") rolekey:String,@Body signInListSynchroRequestBody: SignInListSynchroRequestBody ) : Call<SignInListSynchroResponseBody>

    @POST("/attendence/attendanceReplace/v2/save")
    fun rewindSignInServer(@Header("Authorization") token:String,@Header("Sign") sign:String,@Header("Rolekey") rolekey:String,@Body rewindSignInRequestBody: RewindSignInRequestBody ) : Call<RewindSignInResponseBody>


}

interface MyApiServer {
    @GET("/api/app/version")
    fun getVersionServer() : Call<AppVersionInfo>

    @GET("/api/app/server/list")
    fun getServerListServer() : Call<AppServer>

    @GET("/api/app/banner/list")
    fun getBannerListServer() : Call<AppBannerInfo>

    @POST("api/app/feedback")
    fun feedbackServer(@Body feedbackRequestBody: FeedBackRequestBody) : Call<FeedBackResponseBody>

    @GET("/api/app/papercontent")
    fun getPaperContentServer(@Query("type") type:Int ) : Call<GetPaperResponseBody>

}

