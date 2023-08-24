package vip.chuansvip.gongyunxiaozhu.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kongzue.dialogx.datepicker.CalendarDialog
import com.kongzue.dialogx.datepicker.interfaces.OnDateSelected
import com.kongzue.dialogx.dialogs.PopMenu
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.chuansvip.gongyunxiaozhu.adapter.SignInListAdapter
import vip.chuansvip.gongyunxiaozhu.bean.LocationInfo
import vip.chuansvip.gongyunxiaozhu.bean.RewindSignInRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.RewindSignInResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInListSynchroRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInListSynchroResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInResponseBody
import vip.chuansvip.gongyunxiaozhu.databinding.ActivitySignInBinding
import vip.chuansvip.gongyunxiaozhu.network.ApiServer
import vip.chuansvip.gongyunxiaozhu.network.GongXueYunServerCreator
import vip.chuansvip.gongyunxiaozhu.util.EncryptionAndDecryptUtils
import vip.chuansvip.gongyunxiaozhu.util.GlobalDataManager
import vip.chuansvip.gongyunxiaozhu.util.SignUtil
import vip.chuansvip.gongyunxiaozhu.util.isAfterToday
import vip.chuansvip.gongyunxiaozhu.util.isBeforeToday

class SignInActivity : AppCompatActivity() {
    lateinit var binding: ActivitySignInBinding

    companion object {

        var planName = ""
    }

    private var locationInfo = LocationInfo()
    lateinit var api: ApiServer

    val mapLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 这里是获取的结果
        Log.d("intent检查", "$result")
        if (result.resultCode == RESULT_OK) {
            val receivedBundle = result.data!!.extras
            Log.d("intent检查", "$receivedBundle")
            val name = receivedBundle?.getString("name")
            val address = receivedBundle?.getString("address")
            val province = receivedBundle?.getString("province")
            val city = receivedBundle?.getString("city")
            val district = receivedBundle?.getString("district")
            val latitude = receivedBundle?.getString("latitude")
            val longitude = receivedBundle?.getString("longitude")


            locationInfo.name = name.toString()
            locationInfo.address = address.toString()
            locationInfo.province = province.toString()
            locationInfo.city = city.toString()
            locationInfo.district = district.toString()
            locationInfo.latitude = latitude.toString()
            locationInfo.longitude = longitude.toString()
            //输出


            Log.d(
                "intent检查",
                "---${locationInfo.province}  ${locationInfo.city}  ${locationInfo.district}---  "
            )


            binding.tvSignAddressContent.text = address
        }
        Log.d("检测", "result: ${result.data}")
    }

    var isRewindSignIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        api = GongXueYunServerCreator.create(ApiServer::class.java)


        val date = java.util.Calendar.getInstance()
        val year = date.get(java.util.Calendar.YEAR)
        val month = date.get(java.util.Calendar.MONTH) + 1
        val day = date.get(java.util.Calendar.DAY_OF_MONTH)
        pageInit()
        onClickInit()
        listSynchroInit(year.toString(), month.toString(), day.toString())

    }

    private fun listSynchroInit(year:String,month:String,day:String) {

        val startTime = "$year-$month-$day 00:00:00"
        val endTime = "$year-$month-$day 23:59:59"
        Log.d("签到列表同步", "startTime: $startTime endTime: $endTime")

        //获取两个值一个 是当前这一天的日期和结尾时间一个是 五天前的日期和开始时间
        val signInListSynchroRequestBody = SignInListSynchroRequestBody(startTime, endTime)
        try {


        api.getSignInListServer(GlobalDataManager.globalToken,GlobalDataManager.globalRoleKey,signInListSynchroRequestBody).enqueue(object: Callback<SignInListSynchroResponseBody>{
            override fun onResponse(
                p0: Call<SignInListSynchroResponseBody>,
                p1: Response<SignInListSynchroResponseBody>
            ) {
                if (p1.code() != 200){
                    TipDialog.show("同步失败", WaitDialog.TYPE.ERROR)
                    return
                }
                //判空
                if (p1.body() == null ){
                    TipDialog.show("暂无签到记录", WaitDialog.TYPE.ERROR)
                    return
                }
                val signInListSynchroResponseBody = p1.body()

                val layoutManager = StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                binding.signInListRv.layoutManager = layoutManager
                val adapter = SignInListAdapter(signInListSynchroResponseBody!!.data)
                binding.signInListRv.adapter = adapter

                //输出日志
                Log.d("签到列表同步", "onResponse: $signInListSynchroResponseBody")
            }

            override fun onFailure(p0: Call<SignInListSynchroResponseBody>, p1: Throwable) {
                TODO("Not yet implemented")
            }

        })
        }catch (e:Exception){
            Log.d("签到列表同步", "listSynchroInit: $e")
        }
    }

    private fun onClickInit() {
        binding.tvSignDateContent.setOnClickListener {
            CalendarDialog.build()
                .show(object : OnDateSelected() {
                    /**
                     * 此处为回调，
                     * @param text 直接返回默认文本，例如“2021-9-25”
                     * @param year  年
                     * @param month 月
                     * @param day   日
                     */
                    override fun onSelect(text: String?, year: Int, month: Int, day: Int) {
                        //判断选择的日期是否晚于今天
                        val today = System.currentTimeMillis()
                        //写一个方法，判断是否大于今天
                        val isAfterToday = isAfterToday(year, month, day)
                        if (isAfterToday) {
                            TipDialog.show("选择的日期不能晚于今天!", WaitDialog.TYPE.ERROR);
                            return
                        }else{
                            val isBeforeToday = isBeforeToday(year, month, day)
                            if (isBeforeToday){
                                PopTip.show("已进入补签模式...");
                            }
                            isRewindSignIn = isBeforeToday
                            listSynchroInit(year.toString(), month.toString(), day.toString())
                            binding.tvSignDateContent.text = text
                        }

                    }
                })
        }

        binding.tvSignAddressContent.setOnClickListener {
            val intent = Intent(this, SelectAddressActivity::class.java)

            intent.putExtra("name", locationInfo.name)
            intent.putExtra("address", locationInfo.address)
            intent.putExtra("province", locationInfo.province)
            intent.putExtra("city", locationInfo.city)
            intent.putExtra("district", locationInfo.district)
            intent.putExtra("latitude", locationInfo.latitude)
            intent.putExtra("longitude", locationInfo.longitude)


            mapLauncher.launch(intent)
        }

        //创建一个String List
        val list = mutableListOf<String>()
        list.add("上班")
        list.add("下班")

        binding.tvSignTypeContent.setOnClickListener {
            PopMenu.show(binding.tvSignAddressContent, list as List<CharSequence>?)
                .setAlignGravity(Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL)
                .setOnMenuItemClickListener { _, text, _ ->
                    binding.tvSignTypeContent.text = text
                    false
                }

        }

        binding.btnSign.setOnClickListener {
            //判断是否为空
            val address = binding.tvSignAddressContent.text.toString()
            if (locationInfo.latitude == "0.0" || locationInfo.longitude == "0.0" || address == "") {
                TipDialog.show("请先选择打卡地址!", WaitDialog.TYPE.ERROR);
            }

            val typeText = binding.tvSignTypeContent.text.toString().trim()

            var signType = ""
            if (typeText.equals("上班", ignoreCase = true))
                signType = "START"
            else if (typeText.equals("下班", ignoreCase = true))
                signType = "END"


            val signUtil = SignUtil()
            val sign = signUtil.getSignInSign("Android", signType, address)
            val encryptionAndDecryptUtils = EncryptionAndDecryptUtils()
            val currentTimeMillis = System.currentTimeMillis()
            val t =
                encryptionAndDecryptUtils.encryptAndPrint(currentTimeMillis.toString())
            if (!isRewindSignIn) {
                val signInRequestBody = SignInRequestBody(
                    address,
                    locationInfo.city,
                    "中国",
                    "",
                    "Android",
                    locationInfo.latitude,
                    locationInfo.longitude,
                    GlobalDataManager.globalPlanId,
                    locationInfo.province,
                    t,
                    signType
                )



                try {


                    api.signInServer(
                        GlobalDataManager.globalToken,
                        sign,
                        GlobalDataManager.globalRoleKey,
                        signInRequestBody
                    ).enqueue(object : Callback<SignInResponseBody> {
                        override fun onResponse(
                            p0: Call<SignInResponseBody>,
                            p1: Response<SignInResponseBody>
                        ) {
                            //输出日志
                            Log.d("检测", "onResponse: ${p1.body()}")
                            val signInResponseBody = p1.body()
                            if (signInResponseBody != null) {
                                if (signInResponseBody.code == 200) {
                                    TipDialog.show("打卡成功!", WaitDialog.TYPE.SUCCESS);
                                    val date = java.util.Calendar.getInstance()
                                    val year = date.get(java.util.Calendar.YEAR)
                                    val month = date.get(java.util.Calendar.MONTH) + 1
                                    val day = date.get(java.util.Calendar.DAY_OF_MONTH)
                                    listSynchroInit(
                                        year.toString(),
                                        month.toString(),
                                        day.toString()
                                    )
                                } else {
                                    TipDialog.show(signInResponseBody.msg, WaitDialog.TYPE.ERROR);
                                }
                            }
                        }

                        override fun onFailure(p0: Call<SignInResponseBody>, p1: Throwable) {
                            Log.d("检测", "onFailure: ${p1.message}")
                        }

                    })
                } catch (e: Exception) {
                    Log.d("检测", "${e.message}")
                }


            }else{
                //值为2023-08-22 18:03:00形式
                //获取当前日期年月日
                val date = java.util.Calendar.getInstance()
                val year = date.get(java.util.Calendar.YEAR)
                val month = date.get(java.util.Calendar.MONTH) + 1
                val day = date.get(java.util.Calendar.DAY_OF_MONTH)
                //获取当前时间时分秒
                val hour = date.get(java.util.Calendar.HOUR_OF_DAY)
                val minute = date.get(java.util.Calendar.MINUTE)
                val second = date.get(java.util.Calendar.SECOND)
                //拼接为2023-08-22 18:03:00形式
                val createTime = "$year-$month-$day $hour:$minute:$second"
                
               
                val rewindSignInRequestBody = RewindSignInRequestBody(
                    address,
                    locationInfo.city,
                    "中国",
                    createTime,
                    "",
                    "Android",
                    locationInfo.latitude,
                    locationInfo.longitude,
                    GlobalDataManager.globalPlanId,
                    locationInfo.province,
                    t,
                    signType
                )
                api.rewindSignInServer(GlobalDataManager.globalToken,sign,GlobalDataManager.globalRoleKey,rewindSignInRequestBody).enqueue(object : Callback<RewindSignInResponseBody>{
                    override fun onResponse(
                        p0: Call<RewindSignInResponseBody>,
                        p1: Response<RewindSignInResponseBody>
                    ) {
                        val rewindSignInResponseBody = p1.body()
                        if (rewindSignInResponseBody != null) {
                            if (rewindSignInResponseBody.code == 200) {
                                TipDialog.show("打卡成功!", WaitDialog.TYPE.SUCCESS);
                                val date = java.util.Calendar.getInstance()
                                val year = date.get(java.util.Calendar.YEAR)
                                val month = date.get(java.util.Calendar.MONTH) + 1
                                val day = date.get(java.util.Calendar.DAY_OF_MONTH)
                                listSynchroInit(
                                    year.toString(),
                                    month.toString(),
                                    day.toString()
                                )
                            } else {
                                TipDialog.show(rewindSignInResponseBody.msg, WaitDialog.TYPE.ERROR);
                            }
                        }
                    }

                    override fun onFailure(p0: Call<RewindSignInResponseBody>, p1: Throwable) {
                        Log.d("检测", "onFailure: ${p1.message}")
                    }

                })
            }
        }
    }

    private fun pageInit() {

        //读取sp内容
        val sharedPreferences = getSharedPreferences("locationInfo", Context.MODE_PRIVATE)
        val name = sharedPreferences.getString("name", "")
        val address = sharedPreferences.getString("address", "")
        val province = sharedPreferences.getString("province", "")
        val city = sharedPreferences.getString("city", "")
        val district = sharedPreferences.getString("district", "")
        val latitude = sharedPreferences.getString("latitude", "")
        val longitude = sharedPreferences.getString("longitude", "")

        //判断是否为空
        if (name != "" && address != "" && province != "" && city != "" && district != "" && latitude != "" && longitude != "") {
            locationInfo.name = name.toString()
            locationInfo.address = address.toString()
            locationInfo.province = province.toString()
            locationInfo.city = city.toString()
            locationInfo.district = district.toString()
            locationInfo.latitude = latitude.toString()
            locationInfo.longitude = longitude.toString()
            binding.tvSignAddressContent.text = address


        }


        binding.tvInternshipProgramSign.text = planName


//        //获取当前日期转换为2023-8-11这种格式
        val date = java.util.Calendar.getInstance()
        val year = date.get(java.util.Calendar.YEAR)
        val month = date.get(java.util.Calendar.MONTH) + 1
        val day = date.get(java.util.Calendar.DAY_OF_MONTH)
        val dateStr = "$year-$month-$day"
        binding.tvSignDateContent.text = dateStr


        //获取一个当前时间
        val time = java.util.Calendar.getInstance()
        //判断当前时间在上午还是下午
        val amPm = time.get(java.util.Calendar.AM_PM)
        if (amPm == 0) {
            binding.tvSignTypeContent.text = "上班"
        } else {
            binding.tvSignTypeContent.text = "下班"
        }


    }

    override fun onDestroy() {
        super.onDestroy()

        val sharedPreferences = getSharedPreferences("locationInfo", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("name", locationInfo.name)
        editor.putString("address", locationInfo.address)
        editor.putString("province", locationInfo.province)
        editor.putString("city", locationInfo.city)
        editor.putString("district", locationInfo.district)
        editor.putString("latitude", locationInfo.latitude)
        editor.putString("longitude", locationInfo.longitude)

        editor.apply()
    }

    fun returnHome(view: View) {
        finish()
    }
}