package vip.chuansvip.gongyunxiaozhu.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import vip.chuansvip.gongyunxiaozhu.MyApplication
import vip.chuansvip.gongyunxiaozhu.R
import vip.chuansvip.gongyunxiaozhu.activity.DailyPaperActivity
import vip.chuansvip.gongyunxiaozhu.activity.SignInActivity
import vip.chuansvip.gongyunxiaozhu.bean.AppServer
import vip.chuansvip.gongyunxiaozhu.bean.AppServerRow


class ServiceAdapter(val serviceList: List<AppServerRow>) :
    RecyclerView.Adapter<ServiceAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val serviceImage = view.findViewById<ImageView>(R.id.service_image)
        val serviceName = view.findViewById<TextView>(R.id.service_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.service_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = serviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = serviceList[position]

        Glide.with(MyApplication.context).load(service.url).into(holder.serviceImage)
        holder.serviceName.text = service.name

        holder.itemView.setOnClickListener {
            if (service.name == "日报"){
                DailyPaperActivity.pageType = "day"
                val intent = Intent(MyApplication.context,DailyPaperActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                MyApplication.context.startActivity(intent)
            }
            if (service.name == "周报"){
                DailyPaperActivity.pageType = "week"
                val intent = Intent(MyApplication.context,DailyPaperActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                MyApplication.context.startActivity(intent)
            }

            if (service.name == "月报"){
                DailyPaperActivity.pageType = "month"
                val intent = Intent(MyApplication.context,DailyPaperActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                MyApplication.context.startActivity(intent)
            }

            if (service.name == "签到"){
                val intent = Intent(MyApplication.context,SignInActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                MyApplication.context.startActivity(intent)
            }



        }


    }
}