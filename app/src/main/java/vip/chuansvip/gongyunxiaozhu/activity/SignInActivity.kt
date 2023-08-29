package vip.chuansvip.gongyunxiaozhu.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.kongzue.dialogx.datepicker.CalendarDialog
import com.kongzue.dialogx.datepicker.interfaces.OnDateSelected
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.dialogs.PopMenu
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import com.permissionx.guolindev.PermissionX
import com.tencent.bugly.crashreport.CrashReport
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.chuansvip.gongyunxiaozhu.MyApplication
import vip.chuansvip.gongyunxiaozhu.adapter.SignInListAdapter
import vip.chuansvip.gongyunxiaozhu.bean.AttachmentRewindSignInRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.AttachmentsSignInRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.BaseActivity
import vip.chuansvip.gongyunxiaozhu.bean.GetPlanByStuBack
import vip.chuansvip.gongyunxiaozhu.bean.GetPlanByStuRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.GetUploadTokenRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.GetUploadTokenResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.LocationInfo
import vip.chuansvip.gongyunxiaozhu.bean.RewindSignInRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.RewindSignInResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInListSynchroRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInListSynchroResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.SignInResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.UploadingImgResponseBody
import vip.chuansvip.gongyunxiaozhu.databinding.ActivitySignInBinding
import vip.chuansvip.gongyunxiaozhu.network.ApiServer
import vip.chuansvip.gongyunxiaozhu.network.GongXueYunServerCreator
import vip.chuansvip.gongyunxiaozhu.util.EncryptionAndDecryptUtils
import vip.chuansvip.gongyunxiaozhu.util.GlobalDataManager
import vip.chuansvip.gongyunxiaozhu.util.SignInImgUtil
import vip.chuansvip.gongyunxiaozhu.util.SignUtil
import vip.chuansvip.gongyunxiaozhu.util.isAfterToday
import vip.chuansvip.gongyunxiaozhu.util.isBeforeToday


class SignInActivity : BaseActivity(), SignInImgUtil.SignInImgCallback {
    lateinit var binding: ActivitySignInBinding

    companion object {

        var planName = ""
    }

    private var locationInfo = LocationInfo()
    lateinit var api: ApiServer

    var imgAttachmentBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)

    @SuppressLint("SetTextI18n")
    val mapLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        // 这里是获取的结果
        Log.d("intent检查", "$result")
        if (result.resultCode == RESULT_OK) {
            val receivedBundle = result.data!!.extras
            Log.d("intent检查", "$receivedBundle")
            val name = receivedBundle?.getString("name")
            var address = receivedBundle?.getString("address")
            var province = receivedBundle?.getString("province")
            var city = receivedBundle?.getString("city")
            var district = receivedBundle?.getString("district")
            val latitude = receivedBundle?.getString("latitude")
            val longitude = receivedBundle?.getString("longitude")



            if (province.toString() == city.toString() && city.toString() == district.toString()) {
                val regex =
                    Regex("(.*?省|.*?自治区|.*?市)(.*?市|.*?自治州|.*?地区)?(.*?区|.*?县|.*?市|.*?自治县|.*?自治区)?")
                val matchResult = regex.find(province.toString())

                province = matchResult!!.groupValues[1]
                city = matchResult.groupValues[2]
                district = matchResult.groupValues[3]
                //判断一下省有没有包含市
                if (city == ""){
                    city = province
                    province = city.replace("市","")
                }


                println("省: $province")
                println("市: $city")
                println("区/县: $district")

            }


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
            Toast.makeText(
                this,
                "---${locationInfo.province}  ${locationInfo.city}  ${locationInfo.district}---",
                Toast.LENGTH_SHORT
            ).show()


            address = address!!.replace(province.toString(), "").replace(city.toString(), "")
                .replace(district.toString(), "")

            binding.tvSignAddressContent.text =
                province.toString() + "·" + city.toString() + "·" + district.toString() + "·" + address
        }
        Log.d("检测", "result: ${result.data}")
    }

    val photoAlbumLaunch =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            val data: Intent? = it.data // 获取相册返回的 Intent 数据
            //输出data
//        Log.d("检测", "data: $data")
            if (data != null && data.data != null) {
                val imageUri = data.data
                //日志输出
                Log.d("检测", "imageUri: $imageUri")

                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                imgAttachmentBitmap = bitmap



                Glide.with(this).load(bitmap).into(binding.btnAddImg)
                // 使用得到的 bitmap 进行后续处理

            }
        }

    val cameraLaunch = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        val data: Intent? = it.data // 获取相册返回的 Intent 数据

        //处理相机返回的数据

        if (it.resultCode == Activity.RESULT_OK) {
            // 获取拍摄的照片
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                // 将照片显示在 ImageView 中
                Glide.with(this).load(imageBitmap).into(binding.btnAddImg)
                Log.d("检测", "imageData: ${data.extras?.get("data")}")
                imgAttachmentBitmap = imageBitmap
            }


        }


    }


    var isRewindSignIn = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)



        planIdInit()

        api = GongXueYunServerCreator.create(ApiServer::class.java)


        val date = java.util.Calendar.getInstance()
        val year = date.get(java.util.Calendar.YEAR)
        val month = date.get(java.util.Calendar.MONTH) + 1
        val day = date.get(java.util.Calendar.DAY_OF_MONTH)
        pageInit()
        onClickInit()
        listSynchroInit(year.toString(), month.toString(), day.toString())

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
                    //判断planId planName是否为空
                    if (data?.get(0)?.planId == null || data?.get(0)?.planName == null) {
                        if (data?.get(0)?.planId == null) {
                            TipDialog.show("planId为空", WaitDialog.TYPE.ERROR)
                        } else {
                            TipDialog.show("planName为空", WaitDialog.TYPE.ERROR)
                        }

                        //详细一点
                        return
                    }

                    GlobalDataManager.globalPlanId = data[0].planId.toString()

                    planName = data[0].planName.toString()
                    binding.tvInternshipProgramSign.text = planName


//                Log.d("检测", "PlanId:  ${data?.get(0)?.planId.toString()}")

                }

            }

            override fun onFailure(call: Call<GetPlanByStuBack>, throwable: Throwable) {
//                TipDialog.show("getPlanByStuServer请求异常", WaitDialog.TYPE.ERROR)
//                makeDebugDialogThrowable(this@LoginActivity, throwable)
                CrashReport.postCatchedException(throwable)
            }


        })
    }

    private fun listSynchroInit(year: String, month: String, day: String) {

        val startTime = "$year-$month-$day 00:00:00"
        val endTime = "$year-$month-$day 23:59:59"
        Log.d("签到列表同步", "startTime: $startTime endTime: $endTime")

        //获取两个值一个 是当前这一天的日期和结尾时间一个是 五天前的日期和开始时间
        val signInListSynchroRequestBody = SignInListSynchroRequestBody(startTime, endTime)



        api.getSignInListServer(
            GlobalDataManager.globalToken,
            GlobalDataManager.globalRoleKey,
            signInListSynchroRequestBody
        ).enqueue(object : Callback<SignInListSynchroResponseBody> {
            override fun onResponse(
                p0: Call<SignInListSynchroResponseBody>,
                p1: Response<SignInListSynchroResponseBody>
            ) {
//                if (p1.code() != 200){
//                    TipDialog.show("同步失败", WaitDialog.TYPE.ERROR)
//                    return
//                }
                if (p1.body()!!.msg == "token失效") {
                    val intent = Intent("com.example.broadcastbestpractice.FORCE_OFFLINE")
                    sendBroadcast(intent)
                }
                //判空
                if (p1.body() == null) {
//                    TipDialog.show("暂无签到记录", WaitDialog.TYPE.ERROR)
                    return
                }
                val signInListSynchroResponseBody = p1.body()
                if (signInListSynchroResponseBody == null) {

                    return
                }
                if (signInListSynchroResponseBody.data == null) {
//                    TipDialog.show("暂无签到记录", WaitDialog.TYPE.ERROR)
                    return
                }
                // 继续进行操作
                val layoutManager =
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                binding.signInListRv.layoutManager = layoutManager
                val adapter = SignInListAdapter(signInListSynchroResponseBody.data)
                binding.signInListRv.adapter = adapter

                //输出日志
                Log.d("签到列表同步", "onResponse: $signInListSynchroResponseBody")
            }

            override fun onFailure(p0: Call<SignInListSynchroResponseBody>, p1: Throwable) {
                CrashReport.postCatchedException(p1)
            }

        })
    }

    private fun initPermission() {
        PermissionX.init(this)
            .permissions(

                Manifest.permission.CAMERA
            )
            .onExplainRequestReason { scope, deniedList ->
                val message = "APP需要您同意以下权限才能正常使用"
                scope.showRequestReasonDialog(deniedList, message, "允许", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                // 判断用户是否手动开启了权限
                if (PermissionX.isGranted(
                        this@SignInActivity,
                        Manifest.permission.CAMERA

                    )
                ) {
                    Toast.makeText(
                        this@SignInActivity,
                        "您已经开启了权限，但应用可能需要重新启动",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    scope.showForwardToSettingsDialog(
                        deniedList,
                        "您需要去应用程序设置当中手动开启权限",
                        "去设置",
                        "退出"
                    )
                }
            }
            .request { allGranted, _, deniedList ->
                if (allGranted) {
                    haveCameraPermission = true
                    requestCameraPermission() // 在这里调用获取位置服务方法
                } else {
                    haveCameraPermission = false
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                    System.exit(0)
                }
            }
    }

    private var haveCameraPermission = false

    //获取相机权限的方法
    private fun requestCameraPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA),
            1
        )
    }


    private fun onClickInit() {

        binding.btnAddImg.setOnClickListener {
            BottomMenu.show(arrayOf("拍摄", "从相册选择"))
                .setMessage("这里是标题")
                .setCancelButton("取消")
                .setOnMenuItemClickListener(object : OnMenuItemClickListener<BottomMenu> {
                    override fun onClick(
                        dialog: BottomMenu?,
                        text: CharSequence?,
                        index: Int
                    ): Boolean {
                        if (index == 0) {
                            //调用相机

                            initPermission()
                            if (!haveCameraPermission) {
                                return false
                            }
                            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                            cameraLaunch.launch(intent)


                        }
                        if (index == 1) {
                            //调用相册
                            val intent = Intent(Intent.ACTION_PICK)
                            intent.type = "image/*"
                            photoAlbumLaunch.launch(intent)
                        }
                        return false
                    }

                })
        }

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
                            TipDialog.show("选择的日期不能晚于今天!", WaitDialog.TYPE.ERROR)
                            return
                        } else {
                            val isBeforeToday = isBeforeToday(year, month, day)
                            if (isBeforeToday) {
                                PopTip.show("已进入补签模式...")
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

//           //判断是否设置了图片附件     var imgAttachmentBitmap: Bitmap = Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888)
            if (imgAttachmentBitmap.width == 1) {
                Log.d("图片提交结果", "onClickInit: 未设置图片")

            } else {
                Log.d("图片提交结果", "onClickInit: 设置了图片")

                //获取当前时间戳
                val encryptionAndDecryptUtils = EncryptionAndDecryptUtils()
                val currentTimeMillis = System.currentTimeMillis()
                val t = encryptionAndDecryptUtils.encryptAndPrint(currentTimeMillis.toString())

                val getUploadTokenRequestBody = GetUploadTokenRequestBody(
                    t
                )
                api.getUploadTokenServer(
                    GlobalDataManager.globalToken,
                    GlobalDataManager.globalRoleKey,
                    getUploadTokenRequestBody
                ).enqueue(object : Callback<GetUploadTokenResponseBody> {
                    override fun onResponse(
                        p0: Call<GetUploadTokenResponseBody>,
                        p1: Response<GetUploadTokenResponseBody>
                    ) {
                        val body = p1.body() ?: return

                        val uploadToken = body.data

                        val signInImgUtil = SignInImgUtil()
                        signInImgUtil.uploadingImg(
                            uploadToken,
                            imgAttachmentBitmap,
                            this@SignInActivity
                        )


                    }

                    override fun onFailure(p0: Call<GetUploadTokenResponseBody>, p1: Throwable) {
                        CrashReport.postCatchedException(p1)
                    }

                })

                return@setOnClickListener


//               uploadingImg("3xpTTKkV0RPP6CdkurGtmx9hN_fHG0fQEDB2iHH9:ruQ9sLTBpeniCDrcsxmWarxmyac=:eyJzY29wZSI6Im1vZ3VhcHAiLCJtaW1lTGltaXQiOiJpbWFnZS9qcGVnO2ltYWdlL3BuZztpbWFnZS93ZWJwO2FwcGxpY2F0aW9uL21zd29yZDthcHBsaWNhdGlvbi92bmQub3BlbnhtbGZvcm1hdHMtb2ZmaWNlZG9jdW1lbnQud29yZHByb2Nlc3NpbmdtbC5kb2N1bWVudDthcHBsaWNhdGlvbi92bmQubXMtZXhjZWw7YXBwbGljYXRpb24vdm5kLm9wZW54bWxmb3JtYXRzLW9mZmljZWRvY3VtZW50LnNwcmVhZHNoZWV0bWwuc2hlZXQ7YXBwbGljYXRpb24vdm5kLm9wZW54bWxmb3JtYXRzLW9mZmljZWRvY3VtZW50LnByZXNlbnRhdGlvbm1sLnByZXNlbnRhdGlvbjthcHBsaWNhdGlvbi9wZGY7YXBwbGljYXRpb24vdm5kLm1zLXBvd2VycG9pbnQ7YXBwbGljYXRpb24vemlwO2FwcGxpY2F0aW9uL3gtcmFyO2FwcGxpY2F0aW9uL3gtcmFyLWNvbXByZXNzZWQ7YXBwbGljYXRpb24veC16aXAtY29tcHJlc3NlZDthcHBsaWNhdGlvbi9vY3RldC1zdHJlYW07YXBwbGljYXRpb24veC1vbGUtc3RvcmFnZSIsImRlYWRsaW5lIjoxNjkzMzA2MzUzfQ==",imgAttachmentBitmap)

            }


            //判断是否为空
            val address = binding.tvSignAddressContent.text.toString()
            if (locationInfo.latitude == "0.0" || locationInfo.longitude == "0.0" || address == "") {
                TipDialog.show("请先选择打卡地址!", WaitDialog.TYPE.ERROR)
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
                                TipDialog.show("打卡成功!", WaitDialog.TYPE.SUCCESS)
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
                                TipDialog.show(signInResponseBody.msg, WaitDialog.TYPE.ERROR)
                            }
                        }
                    }

                    override fun onFailure(p0: Call<SignInResponseBody>, p1: Throwable) {
                        CrashReport.postCatchedException(p1)
                    }

                })


            } else {
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
                api.rewindSignInServer(
                    GlobalDataManager.globalToken,
                    sign,
                    GlobalDataManager.globalRoleKey,
                    rewindSignInRequestBody
                ).enqueue(object : Callback<RewindSignInResponseBody> {
                    override fun onResponse(
                        p0: Call<RewindSignInResponseBody>,
                        p1: Response<RewindSignInResponseBody>
                    ) {
                        val rewindSignInResponseBody = p1.body()
                        if (rewindSignInResponseBody != null) {
                            if (rewindSignInResponseBody.code == 200) {
                                TipDialog.show("打卡成功!", WaitDialog.TYPE.SUCCESS)
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
                                TipDialog.show(rewindSignInResponseBody.msg, WaitDialog.TYPE.ERROR)
                            }
                        }
                    }

                    override fun onFailure(p0: Call<RewindSignInResponseBody>, p1: Throwable) {
                        CrashReport.postCatchedException(p1)
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

    override fun onSignInImgComplete(uploadingImgResponseBody: UploadingImgResponseBody) {
        //判断是否为空
        Log.d("地址检测", "onSignInImgComplete:${uploadingImgResponseBody.key}")

       val key = uploadingImgResponseBody.key.replace("upload/", "")


        val address = binding.tvSignAddressContent.text.toString()
        if (locationInfo.latitude == "0.0" || locationInfo.longitude == "0.0" || address == "") {
            TipDialog.show("请先选择打卡地址!", WaitDialog.TYPE.ERROR)
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
            val attachmentsSignInRequestBody = AttachmentsSignInRequestBody(
                address,
              key,
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
            api.signInServer(
                GlobalDataManager.globalToken,
                sign,
                GlobalDataManager.globalRoleKey,
                attachmentsSignInRequestBody
            ).enqueue(object : Callback<SignInResponseBody> {
                override fun onResponse(
                    p0: Call<SignInResponseBody>,
                    p1: Response<SignInResponseBody>
                ) {
                    val attachmentsSignInResponseBody = p1.body()
                    if (attachmentsSignInResponseBody != null) {
                        if (attachmentsSignInResponseBody.code == 200) {
                            TipDialog.show("打卡成功!", WaitDialog.TYPE.SUCCESS)
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
                            TipDialog.show(
                                attachmentsSignInResponseBody.msg,
                                WaitDialog.TYPE.ERROR
                            )
                        }
                    }
                }

                override fun onFailure(p0: Call<SignInResponseBody>, p1: Throwable) {
                    CrashReport.postCatchedException(p1)
                }

            })
        } else {
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


            val attachmentRewindSignInRequestBody = AttachmentRewindSignInRequestBody(
                address,
                key,
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
            api.rewindSignInServer(
                GlobalDataManager.globalToken,
                sign,
                GlobalDataManager.globalRoleKey,
                attachmentRewindSignInRequestBody
            ).enqueue(object : Callback<RewindSignInResponseBody> {
                override fun onResponse(
                    p0: Call<RewindSignInResponseBody>,
                    p1: Response<RewindSignInResponseBody>
                ) {
                    val rewindSignInResponseBody = p1.body()
                    if (rewindSignInResponseBody != null) {
                        if (rewindSignInResponseBody.code == 200) {
                            TipDialog.show("打卡成功!", WaitDialog.TYPE.SUCCESS)
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
                            TipDialog.show(rewindSignInResponseBody.msg, WaitDialog.TYPE.ERROR)
                        }
                    }
                }

                override fun onFailure(p0: Call<RewindSignInResponseBody>, p1: Throwable) {
                    CrashReport.postCatchedException(p1)
                }

            })
        }
    }
}