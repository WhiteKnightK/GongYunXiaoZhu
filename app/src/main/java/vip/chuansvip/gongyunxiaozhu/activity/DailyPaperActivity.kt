package vip.chuansvip.gongyunxiaozhu.activity

import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginTop
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.kongzue.dialogx.dialogs.BottomMenu
import com.kongzue.dialogx.dialogs.MessageDialog
import com.kongzue.dialogx.dialogs.PopMenu
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.kongzue.dialogx.interfaces.BaseDialog
import com.kongzue.dialogx.interfaces.OnDialogButtonClickListener
import com.kongzue.dialogx.interfaces.OnMenuItemClickListener
import com.kongzue.dialogx.util.TextInfo
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.chuansvip.gongyunxiaozhu.R
import vip.chuansvip.gongyunxiaozhu.adapter.ListByStuAdapter
import vip.chuansvip.gongyunxiaozhu.bean.DailyPaperRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.ListByStuBack
import vip.chuansvip.gongyunxiaozhu.bean.ListByStuBackData
import vip.chuansvip.gongyunxiaozhu.bean.ListByStuResRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.LoginBack
import vip.chuansvip.gongyunxiaozhu.databinding.ActivityDailyPaperBinding
import vip.chuansvip.gongyunxiaozhu.network.ApiServer
import vip.chuansvip.gongyunxiaozhu.network.GongXueYunServerCreator
import vip.chuansvip.gongyunxiaozhu.util.EncryptionAndDecryptUtils
import vip.chuansvip.gongyunxiaozhu.util.GlobalDataManager
import vip.chuansvip.gongyunxiaozhu.util.SignUtil
import com.kongzue.dialogx.interfaces.OnMenuItemSelectListener
import vip.chuansvip.gongyunxiaozhu.bean.MonthPaperRequestBody
import vip.chuansvip.gongyunxiaozhu.bean.WeekPaperRequestBody
import vip.chuansvip.gongyunxiaozhu.util.getAllWeeksSinceLastYear
import vip.chuansvip.gongyunxiaozhu.util.getYearMonthRange
import vip.chuansvip.gongyunxiaozhu.util.parseDateRange
import java.time.LocalDate


class DailyPaperActivity : AppCompatActivity() {
    lateinit var binding: ActivityDailyPaperBinding
    lateinit var api: ApiServer
    lateinit var signUtil: SignUtil
    var listPages = 1
    lateinit var rcDataList: ArrayList<ListByStuBackData>

    companion object {
        var pageType = "day"
        var planName = ""
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDailyPaperBinding.inflate(layoutInflater)
        setContentView(binding.root)
        api = GongXueYunServerCreator.create(ApiServer::class.java)
        signUtil = SignUtil()
        rcDataList = ArrayList<ListByStuBackData>()
        submitInit()

        listInit()
        pageInit()

        // 在适当的位置添加滚动监听
        binding.nc.setOnScrollChangeListener { _, _, scrollY, _, _ ->
            val contentHeight = binding.nc.getChildAt(0).height
            val scrollViewHeight = binding.nc.height
            val isScrolledToBottom = (scrollY + scrollViewHeight) >= contentHeight
            //判断列表的数量是否大于等于10


            if (rcDataList.size >= 10) {
                if (isScrolledToBottom) {
                    // NestedScrollView 滑动到底部了
                    // 在此处执行相应的操作

                    listPages++
                    if (listPages <= 3) {
                        listInit()
                    }
                }
            }
            }


    }

    private fun pageInit() {

        binding.edPaperContent.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tvPaperContentTextSize.text = "${s?.length} / 8000"
                if (s?.length!! > 235) {
                    // 将输入框的高度设置为 wrap_content
                    binding.edPaperContent.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                } else {
                    // 将输入框的高度设置为固定值（你可以根据需要进行调整）
                    val dpValue = 250 // 你的 dp 值
                    val pixels = (dpValue * resources.displayMetrics.density).toInt()
                    binding.edPaperContent.layoutParams.height = pixels
                }

                // 请求重新布局以应用高度变化
                binding.edPaperContent.requestLayout()
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        binding.tvInternshipProgram.text = planName
        when (pageType) {
            "day" -> {
                binding.tvWeeks.visibility = View.GONE
                binding.tvWeeksContent.visibility = View.GONE
                binding.tvWeeksDate.visibility = View.GONE
                binding.tvWeeksDateContent.visibility = View.GONE
                binding.tvYearMonth.visibility = View.GONE
                binding.tvYearMonthContent.visibility = View.GONE
                val currentDate = LocalDate.now()
                binding.edPaperTitle.setText("${currentDate.monthValue}月${currentDate.dayOfMonth}日总结")
            }

            "week" -> {

                binding.tvYearMonth.visibility = View.GONE
                binding.tvYearMonthContent.visibility = View.GONE
                var menuItems: List<CharSequence> = mutableListOf()
                val currentDate = LocalDate.now() // Current date
                val weeksSinceLastYear = getAllWeeksSinceLastYear(currentDate)
                for ((index, week) in weeksSinceLastYear.withIndex().reversed()) {
                    println("Week ${index + 1}: ${week.first} ~ ${week.second}")
                    menuItems = menuItems + "${week.first} ~ ${week.second}"

                }
                binding.tvWeeksContent.text = "第1周"
                binding.edPaperTitle.setText("第1周总结")
                binding.tvWeeksDateContent.text = menuItems[0]
                binding.paperPageTitle.text = "周报"
                binding.tvPaperTitle.text = "周报标题"
                binding.edPaperTitle.hint = "请输入周报标题"
                binding.tvPaperContent.text = "周报内容"
                binding.edPaperContent.hint = "请输入周报内容"
                binding.paperHistoricalContent.text = "历史周报"


                //创建一个周次字符数组要1-53周
                val weeksList = arrayOfNulls<String>(53)
                for (i in 0..52) {
                    weeksList[i] = "第" + (i + 1) + "周"
                }

                binding.tvWeeksContent.setOnClickListener {
                    PopMenu.show(binding.tvWeeksContent, weeksList)
                        .setOnMenuItemClickListener { _, text, _ ->
                            binding.tvWeeksContent.text = text
                            binding.edPaperTitle.setText("${text}总结")
                            false
                        }
                }




                binding.tvWeeksDateContent.setOnClickListener {

                    PopMenu.show(binding.tvWeeksDateContent, menuItems)
                        .setOnMenuItemClickListener { _, text, _ ->
                            binding.tvWeeksDateContent.text = text
                            false
                        }


                }
            }

            "month" -> {
                binding.tvWeeks.visibility = View.GONE
                binding.tvWeeksContent.visibility = View.GONE
                binding.tvWeeksDate.visibility = View.GONE
                binding.tvWeeksDateContent.visibility = View.GONE
                binding.paperPageTitle.text = "月报"
                binding.tvPaperTitle.text = "月报标题"
                binding.edPaperTitle.hint = "请输入月报标题"
                binding.tvPaperContent.text = "月报内容"
                binding.edPaperContent.hint = "请输入月报内容"
                binding.paperHistoricalContent.text = "历史月报"



                val currentDate = LocalDate.now() // Current date
                binding.edPaperTitle.setText("${currentDate.year}年${currentDate.monthValue}月总结")
                //将当前日期的年月截取出来，变成字符串形式，值为 2023-8这种形式
                val yearMonth = currentDate.toString().substring(0, 7)

                binding.tvYearMonthContent.text = yearMonth

                val yearMonthRange = getYearMonthRange(currentDate)


                binding.tvYearMonthContent.setOnClickListener {
                    PopMenu.show(binding.tvYearMonthContent, yearMonthRange)
                        .setOverlayBaseView(true).setOnMenuItemClickListener { _, text, _ ->
                            binding.tvYearMonthContent.text = text
                            val parts = text.split("-")
                            val year = parts[0]
                            val month = parts[1]
                            if (month.length == 1) {
                                binding.edPaperTitle.setText("${year}年0${month}月总结")
                            } else {
                                binding.edPaperTitle.setText("${year}年${month}月总结")
                            }
                            false
                        }
                }

            }
        }

    }

    private fun listInit() {
        val sign = signUtil.getListByStuSign(pageType)
        val listByStuResRequestBody = ListByStuResRequestBody(
            listPages.toString(),
            "10",
            GlobalDataManager.globalPlanId,
            pageType
        )
        Log.d("检测", "listByStuServer:${listByStuResRequestBody} ")
        api.listByStuServer(
            GlobalDataManager.globalToken,
            sign,
            pageType,
            listByStuResRequestBody
        ).enqueue(object : Callback<ListByStuBack> {
            override fun onResponse(p0: Call<ListByStuBack>, p1: Response<ListByStuBack>) {
                //判空
                if (p1.body() == null) {
                    TipDialog.show("请求失败，请检查网络", WaitDialog.TYPE.ERROR)
                    return
                }
                if (p1.body()?.code != 200) {
                    TipDialog.show("请求失败，请检查网络", WaitDialog.TYPE.ERROR)
                    return
                }

                Log.d("检测", "listByStuServer:${p1.body()} ")
                Log.d("检测", "listByStuServer:${p1.body()?.data} ")
//                Log.d("检测", "listByStuServer:${p1.body()?.data!![3]} ")


                val listByStuBackData = p1.body()?.data

                for (i in listByStuBackData!!) {
                    rcDataList.add(i)
                }
                val layoutManager =
                    StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                binding.listbystuRv.layoutManager = layoutManager
                val adapter = ListByStuAdapter(rcDataList)
                binding.listbystuRv.adapter = adapter
                if (rcDataList.size == 0) {
                    binding.paperHistoricalContent.visibility = View.GONE

                } else {
                    binding.paperHistoricalContent.visibility = View.VISIBLE

                }
            }

            override fun onFailure(p0: Call<ListByStuBack>, p1: Throwable) {
                TipDialog.show("提交失败，请检查网络", WaitDialog.TYPE.ERROR)
            }

        })
    }


    private fun submitInit() {

        binding.btnDailyPaperSubmit.setOnClickListener {
            //判空
            val title = binding.edPaperTitle.text.toString()
            val content = binding.edPaperContent.text.toString()
            if (title == "") {
                TipDialog.show("标题不能为空", WaitDialog.TYPE.ERROR);
                return@setOnClickListener
            }
            if (content == "") {
                TipDialog.show("日报内容不能为空", WaitDialog.TYPE.ERROR)
                return@setOnClickListener
            }
            MessageDialog.show("日报提交", "确认内容无误，提交日报", "确定", "取消")
                .setCancelTextInfo(TextInfo().setFontColor(Color.RED))
                .setOkButton { dialog, v ->
                    WaitDialog.show("提交中...");

                    val signUtil = SignUtil()
                    val sign = signUtil.getDailyPaperSign(pageType, title)
                    Log.d("检测", "sign:${sign} ")

                    when (pageType) {
                        "day" -> {
                            val dailyPaperRequestBody: DailyPaperRequestBody =
                                DailyPaperRequestBody()
                            val encryptionAndDecryptUtils = EncryptionAndDecryptUtils()
                            val currentTimeMillis = System.currentTimeMillis()
                            val t =
                                encryptionAndDecryptUtils.encryptAndPrint(currentTimeMillis.toString())
                            dailyPaperRequestBody.address = ""
                            dailyPaperRequestBody.reportType = "day"
                            dailyPaperRequestBody.title = title
                            dailyPaperRequestBody.content = content
                            dailyPaperRequestBody.t = t
                            dailyPaperRequestBody.planId = GlobalDataManager.globalPlanId

                            Log.d("检测", "dailyPaperRequestBody:${dailyPaperRequestBody} ")


                            api.submitDayReports(
                                GlobalDataManager.globalToken,
                                sign,
                                GlobalDataManager.globalRoleKey,
                                dailyPaperRequestBody
                            ).enqueue(object : Callback<LoginBack> {
                                override fun onResponse(
                                    p0: Call<LoginBack>,
                                    p1: Response<LoginBack>
                                ) {
                                    //判空

                                    if (p1.body() == null) {
                                        TipDialog.show("提交失败，请检查网络", WaitDialog.TYPE.ERROR)
                                        return
                                    }
                                    Log.d("检测", "submitDayReports:${p1.body()} ")

                                    if (p1.body()?.code != 200) {
                                        TipDialog.show(p1.body()?.msg, WaitDialog.TYPE.ERROR)
                                        return
                                    }


                                    TipDialog.show("提交成功", WaitDialog.TYPE.SUCCESS)
                                    listInit()



                                }

                                override fun onFailure(p0: Call<LoginBack>, p1: Throwable) {
                                    TipDialog.show("提交失败，请检查网络", WaitDialog.TYPE.ERROR)

                                }

                            })
                        }

                        "week" -> {
                            val WeekPaperRequestBody = WeekPaperRequestBody()
                            val encryptionAndDecryptUtils = EncryptionAndDecryptUtils()
                            val currentTimeMillis = System.currentTimeMillis()
                            val t =
                                encryptionAndDecryptUtils.encryptAndPrint(currentTimeMillis.toString())
                            WeekPaperRequestBody.address = ""
                            WeekPaperRequestBody.reportType = "week"
                            WeekPaperRequestBody.title = title
                            WeekPaperRequestBody.content = content
                            WeekPaperRequestBody.t = t
                            WeekPaperRequestBody.planId = GlobalDataManager.globalPlanId

                            val dateRange =
                                parseDateRange(binding.tvWeeksDateContent.text.toString())

                            WeekPaperRequestBody.startTime = dateRange!!.first
                            WeekPaperRequestBody.endTime = dateRange.second
                            WeekPaperRequestBody.weeks = binding.tvWeeks.text.toString()

                            Log.d("检测", "WeekPaperRequestBody:${WeekPaperRequestBody} ")


                            api.submitDayReports(
                                GlobalDataManager.globalToken,
                                sign,
                                GlobalDataManager.globalRoleKey,
                                WeekPaperRequestBody
                            ).enqueue(object : Callback<LoginBack> {
                                override fun onResponse(
                                    p0: Call<LoginBack>,
                                    p1: Response<LoginBack>
                                ) {
                                    //输出返回
                                    Log.d("检测", "onResponse: ${p1.body()} ")
                                    if (p1.body() == null) {
                                        TipDialog.show("提交失败，请检查网络", WaitDialog.TYPE.ERROR)
                                        return
                                    }
                                    Log.d("检测", "submitDayReports:${p1.body()} ")
                                    if (p1.body()?.code != 200) {
                                        TipDialog.show(p1.body()?.msg, WaitDialog.TYPE.ERROR)
                                        return
                                    }
                                    TipDialog.show("提交成功", WaitDialog.TYPE.SUCCESS)
                                    listInit()
                                }

                                override fun onFailure(p0: Call<LoginBack>, p1: Throwable) {

                                }

                            })
                        }
                        "month" ->{
                                val MonthPaperRequestBody = MonthPaperRequestBody()
                                val encryptionAndDecryptUtils = EncryptionAndDecryptUtils()
                                val currentTimeMillis = System.currentTimeMillis()
                                val t =
                                    encryptionAndDecryptUtils.encryptAndPrint(currentTimeMillis.toString())
                                MonthPaperRequestBody.address = ""
                                MonthPaperRequestBody.reportType = "month"
                                MonthPaperRequestBody.title = title
                                MonthPaperRequestBody.content = content
                                MonthPaperRequestBody.t = t
                                MonthPaperRequestBody.planId = GlobalDataManager.globalPlanId
                                MonthPaperRequestBody.yearmonth = binding.tvYearMonthContent.text.toString()

                            Log.d("检测", "responseBody: ${MonthPaperRequestBody} ")
                            api.submitDayReports(
                                GlobalDataManager.globalToken,
                                sign,
                                GlobalDataManager.globalRoleKey,
                                MonthPaperRequestBody
                            ).enqueue(object : Callback<LoginBack> {
                                override fun onResponse(
                                    p0: Call<LoginBack>,
                                    p1: Response<LoginBack>
                                ) {
                                    //输出返回
                                    Log.d("检测", "onResponse: ${p1.body()} ")
                                    if (p1.body() == null) {
                                        TipDialog.show("提交失败，请检查网络", WaitDialog.TYPE.ERROR)
                                        return
                                    }
                                    Log.d("检测", "submitDayReports:${p1.body()} ")
                                    if (p1.body()?.code != 200) {
                                        TipDialog.show(p1.body()?.msg, WaitDialog.TYPE.ERROR)
                                        return
                                    }
                                    TipDialog.show("提交成功", WaitDialog.TYPE.SUCCESS)
                                    listInit()
                                }

                                override fun onFailure(p0: Call<LoginBack>, p1: Throwable) {

                                }

                            })


                        }

                    }


                    false
                }

        }
    }

    fun back(view: View) {
        finish()
    }
}