package vip.chuansvip.gongyunxiaozhu.adapter

import android.content.Intent
import android.graphics.Color
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
import vip.chuansvip.gongyunxiaozhu.bean.AppServer
import vip.chuansvip.gongyunxiaozhu.bean.AppServerRow
import vip.chuansvip.gongyunxiaozhu.bean.ListByStuBackData


class ListByStuAdapter(val serviceList: List<ListByStuBackData>) :
    RecyclerView.Adapter<ListByStuAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val title = view.findViewById<TextView>(R.id.item_daily_paper_title)
        val content = view.findViewById<TextView>(R.id.item_daily_paper_content)
        val time = view.findViewById<TextView>(R.id.item_daily_paper_time)
        val state = view.findViewById<TextView>(R.id.item_daily_paper_state)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.daily_paper_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = serviceList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val service = serviceList[position]

        holder.title.text = service.title
        holder.content.text = service.content
        holder.time.text = service.createTime
        if (service.state == 0) {
            holder.state.text = "未审核"
            holder.state.setTextColor(Color.parseColor("#000000"))
        }else{
            holder.state.text = "已审核"
            holder.state.setTextColor(Color.parseColor("#4fdd64"))
        }


    }
}