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
import vip.chuansvip.gongyunxiaozhu.bean.SignInListSynchroResponseBodyData


class SignInListAdapter(val signInDataList: List<SignInListSynchroResponseBodyData>) :
    RecyclerView.Adapter<SignInListAdapter.ViewHolder>() {
    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val signInType = view.findViewById<TextView>(R.id.tv_sign_in_type)
        val address = view.findViewById<TextView>(R.id.tv_sign_address_item)
        val time = view.findViewById<TextView>(R.id.item_sign_in_time)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.sign_in_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = signInDataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val signInData = signInDataList[position]
        if (signInData.type == "START"){
            holder.signInType.text = "上班"
        }else if (signInData.type == "END"){
            holder.signInType.text = "下班"
        }

        holder.address.text = "${signInData.province}-${signInData.city}-${signInData.address}"
        holder.time.text = signInData.createTime


    }
}