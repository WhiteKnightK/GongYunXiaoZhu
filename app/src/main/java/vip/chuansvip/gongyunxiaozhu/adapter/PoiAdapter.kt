package vip.chuansvip.gongyunxiaozhu.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.model.LatLng
import com.bumptech.glide.Glide
import vip.chuansvip.gongyunxiaozhu.MyApplication
import vip.chuansvip.gongyunxiaozhu.R
import vip.chuansvip.gongyunxiaozhu.activity.DailyPaperActivity
import vip.chuansvip.gongyunxiaozhu.bean.AppServer
import vip.chuansvip.gongyunxiaozhu.bean.AppServerRow
import vip.chuansvip.gongyunxiaozhu.bean.LocationInfo


interface PoiAdapterListener {
    fun onPoiItemSelected(position: Int, selectedLatLng: LatLng)
}
class PoiAdapter(private val listener: PoiAdapterListener,private val poiDataList: ArrayList<LocationInfo>) :
    RecyclerView.Adapter<PoiAdapter.ViewHolder>() {

    private var selectedItemPosition: Int = -1 // 初始选中的项为-1，表示没有选中项

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name = view.findViewById<TextView>(R.id.tv_poi_name)
        val address = view.findViewById<TextView>(R.id.tv_poi_address)
        val dui = view.findViewById<ImageView>(R.id.iv_poi_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.poi_item, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = poiDataList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val poi = poiDataList[holder.adapterPosition] // 使用 getAdapterPosition()

        holder.address.text = poi.address
        holder.name.text = poi.name
        if (selectedItemPosition == -1 && poi.name == "我的位置") {
            selectedItemPosition = holder.adapterPosition
        }

        // 设置选中状态
        holder.dui.visibility = if (holder.adapterPosition == selectedItemPosition) View.VISIBLE else View.GONE

        // 设置默认选中“我的位置”


        holder.itemView.setOnClickListener {
            val clickedPosition = holder.adapterPosition
            if (clickedPosition != RecyclerView.NO_POSITION) {
                selectedItemPosition = clickedPosition
                notifyDataSetChanged()

                val clickedPosition = holder.adapterPosition
                val selectedLatLng = LatLng(poi.latitude.toDouble(), poi.longitude.toDouble())
                listener.onPoiItemSelected(clickedPosition, selectedLatLng)
            }
        }
    }



}
