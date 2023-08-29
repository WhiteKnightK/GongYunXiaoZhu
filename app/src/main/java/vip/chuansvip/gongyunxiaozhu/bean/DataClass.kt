package vip.chuansvip.gongyunxiaozhu.bean


data class LoginData(
    val captcha: String? = null,
    val device: String = "android",
    var password: String,
    val loginType: String = "android",
    var t: String,
    val uuid: String = "",
    val version: String = "5.3.0",
    var phone: String
) {
    constructor() : this(
        "",
        "android",
        "",
        "android",
        "",
        "",
        "5.3.0",
        ""
    )
}

data class LoginBack(
    val code: Int,
    val `data`: Data,
    val msg: String
)

data class Data(
    val authType: Int,
    val dataList: Any,
    val expiredTime: Long,
    val gender: Int,
    val headImg: String,
    val isAccount: Int,
    val moguNo: String,
    val msgConfig: MsgConfig,
    val neteaseImId: String,
    val nikeName: String,
    val orgJson: OrgJson,
    val phone: String,
    val plan: Any,
    val roleGroup: List<RoleGroup>,
    val roleId: String,
    val roleKey: String,
    val roleLevel: String,
    val roleName: String,
    val sessionKey: Any,
    val token: String,
    val umengId: Any,
    val userId: String,
    val userType: String
)

class MsgConfig

data class OrgJson(
    val classId: String,
    val className: String,
    val companyId: Any,
    val companyName: Any,
    val depId: String,
    val depName: String,
    val grade: String,
    val majorField: String,
    val majorId: String,
    val majorName: String,
    val post: Any,
    val schoolId: String,
    val schoolName: String,
    val snowFlakeId: String,
    val studentId: String,
    val studentNumber: String,
    val teacheId: Any,
    val teacherNumber: Any,
    val userName: String
)

data class RoleGroup(
    val backup: Any,
    val createBy: Any,
    val createByName: Any,
    val createTime: Any,
    val currPage: Int,
    val isDeleted: Any,
    val modifiedBy: Any,
    val modifiedByName: Any,
    val modifiedTime: Any,
    val orderBy: String,
    val pageSize: Int,
    val roleId: String,
    val roleKey: String,
    val roleLevel: String,
    val roleName: String,
    val roleUserId: Any,
    val sort: String,
    val state: Any,
    val totalCount: Int,
    val totalPage: Int,
    val userId: Any
)

data class AppVersionInfo(
    val Code: Int,
    val DownloadUrl: String,
    val ModifyContent: String,
    val Msg: String,
    val VersionCode: Int,
    val VersionName: String
)

data class AppServer(
    val code: Int,
    val msg: String,
    val rows: List<AppServerRow>,
    val total: Int
)

data class AppServerRow(
    val id: Int,
    val name: String,
    val url: String
)

data class GetMoGuDingUserInfoBack(
    val code: Int,
    val `data`: String,
    val msg: String
)

data class MoGuDingInfo(
    val authType: Int,
    val createTime: Long,
    val currPage: Int,
    val gender: Int,
    val headImg: String,
    val isDeleted: Int,
    val isFriend: Int,
    val modifiedTime: Long,
    val moguAge: String,
    val moguNo: String,
    val neteaseImId: String,
    val nikeName: String,
    val orderBy: String,
    val orgEntity: OrgEntity,
    val orgJson: String,
    val pageSize: Int,
    val phone: String,
    val points: Int,
    val sort: String,
    val state: Int,
    val totalCount: Int,
    val totalPage: Int,
    val userId: Int,
    val userType: String
)

data class OrgEntity(
    val classId: String,
    val className: String,
    val depId: String,
    val depName: String,
    val grade: String,
    val majorField: String,
    val majorId: String,
    val majorName: String,
    val schoolId: String,
    val schoolName: String,
    val snowFlakeId: String?,
    val studentId: String,
    val studentNumber: String,
    val userName: String
)

data class LogoutBack(
    val code: Int,
    val msg: String
)

data class DailyPaperRequestBody(
    var address: String,
    var content: String,
    val latitude: String,
    val longitude: String,
    var planId: String,
    var reportType: String,
    var t: String,
    var title: String,
    val yearmonth: String
) {
    constructor() : this(
        "",
        "",
        "0.0",
        "0.0",
        "",
        "day",
        "",
        "",
        ""
    )
}

data class GetPlanByStuBack(
    val code: Int,
    val data: List<GetPlanByStuBackData>,
    val msg: String
)

data class GetPlanByStuBackData(
    val aboutType: Any,
    val attachmentNum: Int,
    val attachments: Any,
    val attachmentsList: Any,
    val auditName: Any,
    val auditState: Any,
    val backup: Any,
    val batchId: String,
    val batchName: String,
    val comment: Any,
    val companyMultiple: Any,
    val createBy: Any,
    val createByName: Any,
    val createName: String,
    val createTime: Any,
    val currPage: Int,
    val depId: Any,
    val depName: Any,
    val description: Any,
    val endTime: String,
    val insuranceList: Any,
    val isApply: Int,
    val isAuto: String,
    val isBuyInsurance: Any,
    val isCopy: Any,
    val isCopyAllocate: Any,
    val isDeleted: Any,
    val isShowUpDel: Any,
    val isSign: Int,
    val isSysDefault: Int,
    val isTalentPlan: Any,
    val levelEntity: Any,
    val majorField: Any,
    val majorId: Any,
    val majorName: Any,
    val majorNames: Any,
    val majorTeacher: Any,
    val majorTeacherId: Any,
    val mobile: String,
    val modifiedBy: Any,
    val modifiedByName: Any,
    val modifiedTime: Any,
    val multipleTheory: Any,
    val orderBy: String,
    val pageSize: Int,
    val planAppraiseItem: Any,
    val planAppraiseItemDtos: Any,
    val planAppraiseItemEntities: Any,
    val planClasses: Any,
    val planExtra: Any,
    val planGrades: Any,
    val planId: String,
    val planIds: Any,
    val planLevel: Any,
    val planMajors: Any,
    val planName: String,
    val planNumber: Any,
    val planPaper: PlanPaper,
    val planPaperMap: Any,
    val planState: Any,
    val practiceState: Int,
    val practiceStateNum: Any,
    val practiceStus: Any,
    val practiceTeas: Any,
    val programId: Any,
    val schoolId: Any,
    val schoolTeacher: Any,
    val selfMultiple: Any,
    val semester: Any,
    val signCount: Any,
    val snowFlakeId: Any,
    val sort: String,
    val startTime: String,
    val stuItemIds: Any,
    val subsidy: Any,
    val teacherId: Any,
    val teacherName: String,
    val totalCount: Int,
    val totalPage: Int,
    val type: String
)

data class PlanPaper(
    val createTime: String,
    val dayPaper: Boolean,
    val dayPaperNum: Any,
    val isDeleted: Int,
    val maxDayNum: Int,
    val maxMonthNum: Int,
    val maxSummaryNum: Int,
    val maxWeekNum: Int,
    val monthPaper: Boolean,
    val monthPaperNum: Any,
    val monthReportCount: Any,
    val paperReportCount: Any,
    val planId: String,
    val planPaperId: String,
    val snowFlakeId: String?,
    val summaryPaper: Boolean,
    val summaryPaperNum: Int,
    val summaryReportCount: Int,
    val weekPaper: Boolean,
    val weekPaperNum: Any,
    val weekReportCount: Any
)

data class GetPlanByStuRequestBody(
    val state: String
) {
    constructor() : this("")
}

data class ListByStuResRequestBody(
    val currPage: String,
    val pageSize: String,
    val planId: String,
    val reportType: String
)

data class ListByStuBack(
    val code: Int,
    val `data`: List<ListByStuBackData>,
    val flag: Int,
    val msg: String
)

data class ListByStuBackData(
    val address: String,
    val applyId: Any,
    val applyName: Any,
    val applyTime: Any,
    val attachmentList: Any,
    val attachments: Any,
    val backup: Any,
    val batchId: Any,
    val classId: Any,
    val classIds: Any,
    val className: Any,
    val commentContent: Any,
    val commentNum: Any,
    val companyName: Any,
    val content: String,
    val createBy: String,
    val createByName: Any,
    val createTime: String,
    val currPage: Int,
    val dateTime: Any,
    val depId: Any,
    val depIds: Any,
    val depName: Any,
    val endTime: Any,
    val endTimeStr: Any,
    val grade: Any,
    val headImg: String,
    val imageList: Any,
    val isCanRead: Any,
    val isDeleted: Int,
    val isFine: Any,
    val isOnTime: Int,
    val isReject: Any,
    val isSeeEnd: Any,
    val jobId: Any,
    val jobName: Any,
    val latitude: String,
    val longitude: String,
    val majorField: Any,
    val majorId: Any,
    val majorIds: Any,
    val majorName: Any,
    val modifiedBy: Any,
    val modifiedByName: Any,
    val modifiedTime: String,
    val orderBy: String,
    val pageSize: Int,
    val planId: String,
    val planName: Any,
    val reDetalTabName: Any,
    val reject: Any,
    val reportComments: Any,
    val reportId: String,
    val reportIds: Any,
    val reportTabName: Any,
    val reportTime: String,
    val reportType: String,
    val schoolId: String,
    val schoolName: Any,
    val score: Int,
    val scoreLevel: Any,
    val sign: Any,
    val snowFlakeId: Int,
    val sort: String,
    val starNum: Any,
    val startTime: Any,
    val startTimeStr: Any,
    val state: Int,
    val stuTabName: Any,
    val studentId: String,
    val studentNumber: Any,
    val supportNum: Any,
    val t: Any,
    val teaId: Any,
    val teaStuTabName: Any,
    val teacherName: Any,
    val title: String,
    val totalCount: Int,
    val totalPage: Int,
    val userId: String,
    val username: String,
    val videoUrl: Any,
    val weeks: Any,
    val yearmonth: String
)

data class WeekPaperRequestBody(
    var address: String,
    var content: String,
    var endTime: String,
    val latitude: String,
    val longitude: String,
    var planId: String,
    var reportType: String,
    var startTime: String,
    var t: String,
    var title: String,
    var weeks: String,
    val yearmonth: String
) {
    constructor() : this(
        "",
        "",
        "",
        "0.0",
        "0.0",
        "",
        "day",
        "",
        "",
        "",
        "",
        ""
    )
}

data class MonthPaperRequestBody(
    var address: String,
    var content: String,
    val latitude: String,
    val longitude: String,
    var planId: String,
    var reportType: String,
    var t: String,
    var title: String,
    var yearmonth: String
) {
    constructor() : this(
        "",
        "",
        "0.0",
        "0.0",
        "",
        "month",
        "",
        "",
        ""
    )
}

//创建一个数据类存储地点信息 包含经纬度和地点名称 详细地址 省份 城市
data class LocationInfo(
    var latitude: String,
    var longitude: String,
    var address: String,
    var province: String,
    var city: String,
    var district: String,
    var name: String

) {
    constructor() : this(
        "0.0",
        "0.0",
        "",
        "",
        "",
        "",
        ""
    )
}

data class SignInRequestBody(
    val address: String,
    val city: String,
    val country: String,
    val description: String,
    val device: String,
    val latitude: String,
    val longitude: String,
    val planId: String,
    val province: String,
    val t: String,
    val type: String
)

data class SignInResponseBody(
    val code: Int,
    val `data`: SignInResponseBodyData,
    val msg: String
)

data class SignInResponseBodyData(
    val attendanceId: String,
    val createTime: String
)

data class AttachmentsSignInRequestBody(
    val address: String,
    val attachments: String,
    val city: String,
    val country: String,
    val description: String,
    val device: String,
    val latitude: String,
    val longitude: String,
    val planId: String,
    val province: String,
    val t: String,
    val type: String
)


data class SignInListSynchroRequestBody(

    val startTime: String,
    val endTime: String
)
data class SignInListSynchroResponseBody(
    val code: Int,
    val `data`: List<SignInListSynchroResponseBodyData>,
    val flag: Int,
    val msg: String
)

data class SignInListSynchroResponseBodyData(
    val address: String,
    val attendanceId: String,
    val attendanceType: Any,
    val attendanceTypeNumber: Int,
    val attendenceTimeLong: Long,
    val city: String,
    val classId: String,
    val country: String,
    val createTime: String,
    val dateYmd: String,
    val depId: String,
    val description: String,
    val device: String,
    val gradeNumber: Int,
    val isReplace: Int,
    val latitude: String,
    val longitude: String,
    val majorId: String,
    val memberId: String,
    val memberNumber: String,
    val modifiedTime: String,
    val planId: String,
    val province: String,
    val schoolId: String,
    val snowFlakeId: Int,
    val state: String,
    val stateNumber: Int,
    val type: String,
    val userId: String,
    val userIdNumber: Int,
    val username: String
)
data class FeedBackRequestBody(
    var email: String,
    var content: String,
    var isAnonymity: Boolean
){
    constructor():this("","",false)
}
data class FeedBackResponseBody(
    val code: String,
    val msg: String
)
data class AppBannerInfo(
    val code: Int,
    val msg: String,
    val rows: List<Row>,
    val total: Int
)

data class Row(
    val id: Int,
    val imgUrl: String,
    val linkUrl: String,
    val name: String
)

data class RewindSignInRequestBody(
    val address: String,
    val city: String,
    val country: String,
    val createTime: String,
    val description: String,
    val device: String,
    val latitude: String,
    val longitude: String,
    val planId: String,
    val province: String,
    val t: String,
    val type: String
)
data class RewindSignInResponseBody(
    val code: Int,
    val msg: String
)


data class AttachmentRewindSignInRequestBody(
    val address: String,
    val attachments: String,
    val city: String,
    val country: String,
    val createTime: String,
    val description: String,
    val device: String,
    val latitude: String,
    val longitude: String,
    val planId: String,
    val province: String,
    val t: String,
    val type: String
)

data class GetPaperResponseBody(
    val code: Int,
    val `data`: GetPaperResponseBodyData,
    val msg: String
)

data class GetPaperResponseBodyData(
    val content: String,
    val id: Int,
    val word_count: Int
)

data class UploadingImgResponseBody(
    val hash: String,
    val key: String
)

data class GetUploadTokenResponseBody(
    val code: Int,
    val `data`: String,
    val msg: String
)
data class GetUploadTokenRequestBody(
    val t: String
)

