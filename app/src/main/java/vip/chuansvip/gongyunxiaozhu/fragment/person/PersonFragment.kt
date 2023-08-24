package vip.chuansvip.gongyunxiaozhu.fragment.person

import TimeStampConverter
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContextCompat.getSystemService
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.kongzue.dialogx.DialogX
import com.kongzue.dialogx.dialogs.BottomDialog
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopTip
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.OnBindView
import com.kongzue.dialogx.style.IOSStyle
import com.kongzue.dialogx.style.MIUIStyle
import com.kongzue.dialogx.util.TextInfo
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.chuansvip.gongyunxiaozhu.R
import vip.chuansvip.gongyunxiaozhu.activity.AboutAppActivity
import vip.chuansvip.gongyunxiaozhu.activity.MainActivity
import vip.chuansvip.gongyunxiaozhu.activity.NoticeForUseActivity
import vip.chuansvip.gongyunxiaozhu.bean.FeedBackRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.FeedBackResponseBody
import vip.chuansvip.gongyunxiaozhu.bean.GetMoGuDingUserInfoBack
import vip.chuansvip.gongyunxiaozhu.bean.LoginBack
import vip.chuansvip.gongyunxiaozhu.bean.LogoutBack
import vip.chuansvip.gongyunxiaozhu.bean.MoGuDingInfo
import vip.chuansvip.gongyunxiaozhu.databinding.FragmentPersonBinding
import vip.chuansvip.gongyunxiaozhu.network.ApiServer
import vip.chuansvip.gongyunxiaozhu.network.GongXueYunServerCreator
import vip.chuansvip.gongyunxiaozhu.network.MyApiServer
import vip.chuansvip.gongyunxiaozhu.network.MyServerCreator
import vip.chuansvip.gongyunxiaozhu.util.EncryptionAndDecryptUtils
import vip.chuansvip.gongyunxiaozhu.util.GlobalDataManager


class PersonFragment : Fragment() {

    lateinit var binding: FragmentPersonBinding
    lateinit var api: ApiServer
    lateinit var myApi: MyApiServer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPersonBinding.inflate(layoutInflater)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        api = GongXueYunServerCreator.create(ApiServer::class.java)
        myApi = MyServerCreator.create(MyApiServer::class.java)

        try {
            userInfoInit()
            logoutInit()
            onClickInit()
        } catch (e: Exception) {
            Log.d("检测", "onViewCreated: ${e.message}")
        }

    }

    private fun onClickInit() {

        binding.btnAboutApp.setOnClickListener {
            val intent = Intent(context, AboutAppActivity::class.java)
            startActivity(intent)
        }
        binding.btnNoticeForUse.setOnClickListener {

            val intent = Intent(context, NoticeForUseActivity::class.java)
            startActivity(intent)
        }
        binding.btnFeedback.setOnClickListener {
            DialogX.globalStyle = MIUIStyle.style()
            BottomDialog.show(object : OnBindView<BottomDialog>(
                R.layout.layout_custom_reply
            ) {
                override fun onBind(dialog: BottomDialog, v: View) {
                    val btnReplyCommit = v.findViewById<TextView>(R.id.btn_reply_commit)
                    val editReplyCommit = v.findViewById<EditText>(R.id.edit_reply_commit)
                    val editMailbox = v.findViewById<EditText>(R.id.edit_mail_box)
                    val checkBoxIsAnonymity = v.findViewById<CheckBox>(R.id.check_box_is_anonymity)
                    val splitLine = v.findViewById<View>(R.id.custom_reply_splitLine)

                    checkBoxIsAnonymity.setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isChecked) {
                            editMailbox.visibility = View.GONE
                            splitLine.visibility = View.GONE
                        } else {
                            editMailbox.visibility = View.VISIBLE
                            splitLine.visibility = View.VISIBLE
                        }
                    }
                    btnReplyCommit.setOnClickListener {
                        dialog.dismiss()
                        val feedBackRequestBody = FeedBackRequestBody()
                        feedBackRequestBody.content = editReplyCommit.text.toString()
                        feedBackRequestBody.email = editMailbox.text.toString()
                        feedBackRequestBody.isAnonymity = checkBoxIsAnonymity.isChecked
                        //判空
                        if (!feedBackRequestBody.isAnonymity) {
                            if (feedBackRequestBody.email.isEmpty()) {
                                TipDialog.show("联系方式不能为空", WaitDialog.TYPE.WARNING)
                                return@setOnClickListener
                            }
                        }
                        if (feedBackRequestBody.content.isEmpty()) {
                            TipDialog.show("反馈内容不能为空", WaitDialog.TYPE.WARNING)
                            return@setOnClickListener
                        }



                        try {
                            Log.d(
                                "检测",
                                "onResponse: ${feedBackRequestBody}"
                            )

                            myApi.feedbackServer(feedBackRequestBody)
                                .enqueue(object : Callback<FeedBackResponseBody> {
                                    override fun onResponse(
                                        p0: Call<FeedBackResponseBody>,
                                        p1: Response<FeedBackResponseBody>
                                    ) {
                                        //打印日志

                                        Log.d(
                                            "检测",
                                            "onResponse: ${p1.body()}"
                                        )
                                        if (p1.body() != null) {
                                            if (p1.code() == 200) {
                                                TipDialog.show(
                                                    "反馈成功",
                                                    WaitDialog.TYPE.SUCCESS
                                                )
                                            } else {
                                                TipDialog.show(
                                                    "反馈失败",
                                                    WaitDialog.TYPE.ERROR
                                                )
                                            }
                                        } else {
                                            TipDialog.show(
                                                "反馈失败,请联系管理员",
                                                WaitDialog.TYPE.ERROR
                                            )
                                        }
                                    }

                                    override fun onFailure(p0: Call<FeedBackResponseBody>, p1: Throwable) {
                                        TipDialog.show(
                                            "反馈失败,请检查网络后重试",
                                            WaitDialog.TYPE.ERROR
                                        )
                                    }

                                })
                        }   catch (e: Exception) {
                            Log.d("检测", "onViewCreated: ${e.message}")
                        }

                    }

                    editReplyCommit.postDelayed({
                        showIME(editMailbox)
                    }, 300)
                }
            }).cancelButton = "取消"
            DialogX.globalStyle = IOSStyle.style()


        }
    }

    fun showIME(editText: EditText?) {
        if (editText == null) {
            return
        }
        editText.requestFocus()
        editText.isFocusableInTouchMode = true
        val imm = context?.getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager


        imm.showSoftInput(editText, InputMethodManager.RESULT_UNCHANGED_SHOWN)
    }


    private fun userInfoInit() {

        val emptyJson = "{}"
        val requestBody = RequestBody.create("application/json".toMediaType(), emptyJson)
        api.getUserInfo(GlobalDataManager.globalToken, GlobalDataManager.globalRoleKey, requestBody)
            .enqueue(object : Callback<GetMoGuDingUserInfoBack> {
                override fun onResponse(
                    p0: Call<GetMoGuDingUserInfoBack>,
                    p1: Response<GetMoGuDingUserInfoBack>
                ) {
                    Log.d("检测", "onResponse: ${p1.body()}")
                    if (p1.body() == null) {
                        TipDialog.show("加载失败,请检查网络后重试", WaitDialog.TYPE.ERROR);
                        return
                    }
                    if (p1.body()?.code != 200) {
                        TipDialog.show(p1.body()?.msg, WaitDialog.TYPE.ERROR);
                        return
                    }
                    //解密数据
                    val encryptionAndDecryptUtils = EncryptionAndDecryptUtils()
                    val userInfoString = encryptionAndDecryptUtils.decryptData(p1.body()?.data!!)
                    //解析数据
                    val gson = Gson()
                    val userInfo = gson.fromJson(userInfoString, MoGuDingInfo::class.java)

                    val orgEntity = userInfo.orgEntity

                    //加载到页面上
                    binding.tvLikeName.text = userInfo.nikeName
                    binding.tvPhone.text = "账号：${userInfo.phone}"
                    binding.tvSchoolName.text = orgEntity.schoolName
                    binding.tvDepName.text = orgEntity.depName
                    binding.tvMajorField.text = orgEntity.majorField
                    binding.tvGrade.text = orgEntity.grade
                    binding.tvClassName.text = orgEntity.className


                    binding.tvUserId.text = userInfo.userId.toString()
                    binding.tvMoguNo.text = userInfo.moguNo
                    var type = ""
                    if (userInfo.userType == "student") {
                        type = "学生"
                    } else if (userInfo.userType == "teacher") {
                        type = "教师"
                    }
                    binding.tvUserType.text = type
                    binding.tvCreateTime.text =
                        TimeStampConverter.convertTimeStampToDateString(userInfo.createTime)
                    binding.tvMoguAge.text = userInfo.moguAge.toString()


                }

                override fun onFailure(p0: Call<GetMoGuDingUserInfoBack>, p1: Throwable) {
                    TipDialog.show("加载失败,请检查网络后重试", WaitDialog.TYPE.ERROR);

                }

            })
    }

    private fun logoutInit() {
        binding.btnLogout.setOnClickListener {
            MessageDialog.show("退出登录", "退出当前账号，返回登录页面", "确定", "取消")
                .setOkTextInfo(TextInfo().setFontColor(Color.RED))
                .setOkButton { dialog, v ->
                    api.logoutServer(GlobalDataManager.globalToken, GlobalDataManager.globalRoleKey)
                        .enqueue(object : Callback<LogoutBack> {
                            override fun onResponse(
                                p0: Call<LogoutBack>,
                                p1: Response<LogoutBack>
                            ) {
                                if (p1.body() == null) {
                                    TipDialog.show("退出失败", WaitDialog.TYPE.ERROR);
                                    return
                                }
                                if (p1.body()?.code != 200) {
                                    TipDialog.show(p1.body()?.msg, WaitDialog.TYPE.ERROR);
                                    return
                                }
                                val activity = activity as MainActivity
                                activity.logout()
                            }

                            override fun onFailure(p0: Call<LogoutBack>, p1: Throwable) {
                                TipDialog.show("退出失败,请检查网络后重试", WaitDialog.TYPE.ERROR);
                            }

                        })
                    false
                }


        }
    }
}