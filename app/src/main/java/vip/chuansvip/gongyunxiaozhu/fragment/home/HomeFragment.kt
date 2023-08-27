package vip.chuansvip.gongyunxiaozhu.fragment.home

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.kongzue.dialogx.dialogs.TipDialog
import com.kongzue.dialogx.dialogs.WaitDialog
import com.tencent.bugly.crashreport.CrashReport
import com.youth.banner.adapter.BannerImageAdapter
import com.youth.banner.holder.BannerImageHolder
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import vip.chuansvip.gongyunxiaozhu.adapter.MyBannerAdapter
import vip.chuansvip.gongyunxiaozhu.adapter.ServiceAdapter
import vip.chuansvip.gongyunxiaozhu.bean.AppBannerInfo
import vip.chuansvip.gongyunxiaozhu.bean.AppServer
import vip.chuansvip.gongyunxiaozhu.databinding.FragmentHomeBinding
import vip.chuansvip.gongyunxiaozhu.network.MyApiServer
import vip.chuansvip.gongyunxiaozhu.network.MyServerCreator
import vip.chuansvip.gongyunxiaozhu.util.joinQQGroup


class HomeFragment : Fragment() {


    private lateinit var binding: FragmentHomeBinding
    private lateinit var api: MyApiServer

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        api = MyServerCreator.create(MyApiServer::class.java)
        loadServerList()
        loadBanner()

    }

    private fun loadServerList() {


        api.getServerListServer().enqueue(object : Callback<AppServer>{
            override fun onResponse(p0: Call<AppServer>, p1: Response<AppServer>) {
                val serverList = p1.body()!!.rows
                val layoutManager = StaggeredGridLayoutManager(4,StaggeredGridLayoutManager.VERTICAL)
                binding.serverList.layoutManager = layoutManager
                val adapter = ServiceAdapter(serverList)
                binding.serverList.adapter = adapter

            }

            override fun onFailure(p0: Call<AppServer>, p1: Throwable) {
                CrashReport.postCatchedException(p1)
            }

        })


    }
    private fun loadBanner() {

        api.getBannerListServer().enqueue(object : Callback<AppBannerInfo>{
            override fun onResponse(p0: Call<AppBannerInfo>, p1: Response<AppBannerInfo>) {
                if (p1.body() == null){
                    return
                }
                if (p1.body()!!.rows.isEmpty()){
                    return
                }
                val bannerList = p1.body()!!.rows
                val imgs = ArrayList<String>()

                for (i in bannerList){
                    imgs.add(i.imgUrl)
                }

                binding.banner.setAdapter(object : MyBannerAdapter<String>(imgs) {
                    override fun onBindView(
                        holder: BannerImageHolder,
                        data: String,
                        position: Int,
                        size: Int
                    ) {
                        //图片加载自己实现
                        Glide.with(holder.itemView)
                            .load(data)
                            .into(holder.imageView)
                    }
                })
                    .addBannerLifecycleObserver(this@HomeFragment)
                    .setBannerGalleryEffect(20,4,0.85f)
//                    .setBannerGalleryMZ(20,0.8f)
                    .setLoopTime(3000)
                    .setOnBannerListener { any, i ->
//                        TipDialog.show("点击了第${i+1}张图片", WaitDialog.TYPE.SUCCESS);\
                        if (bannerList[i].name == "加入QQ群"){
                            joinQQGroup("kEfI5W8unKVYRSpvMWBvhJICiBPYZPfC",context!!)
                            return@setOnBannerListener
                        }

                        val linkUrl = bannerList[i].linkUrl
                        if (!linkUrl.isNullOrEmpty()) {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkUrl))
                            startActivity(intent)
                        } else {
                            // Handle case where linkUrl is empty or null
                        }





                    }



            }

            override fun onFailure(p0: Call<AppBannerInfo>, p1: Throwable) {
                CrashReport.postCatchedException(p1)

            }

        })



    }
}