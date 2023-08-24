package vip.chuansvip.gongyunxiaozhu.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapsInitializer
import com.amap.api.maps.model.BitmapDescriptorFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.Marker
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.geocoder.GeocodeResult
import com.amap.api.services.geocoder.GeocodeSearch
import com.amap.api.services.geocoder.RegeocodeQuery
import com.amap.api.services.geocoder.RegeocodeResult
import com.amap.api.services.help.Inputtips
import com.amap.api.services.help.InputtipsQuery
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.permissionx.guolindev.PermissionX
import vip.chuansvip.gongyunxiaozhu.R
import vip.chuansvip.gongyunxiaozhu.adapter.PoiAdapter
import vip.chuansvip.gongyunxiaozhu.adapter.PoiAdapterListener
import vip.chuansvip.gongyunxiaozhu.bean.BaseActivity
import vip.chuansvip.gongyunxiaozhu.bean.LocationInfo
import vip.chuansvip.gongyunxiaozhu.databinding.ActivitySelectAddressBinding


class SelectAddressActivity : BaseActivity(), GeocodeSearch.OnGeocodeSearchListener,
    PoiAdapterListener {
    lateinit var binding: ActivitySelectAddressBinding

    lateinit var aMap: AMap
    private var shouldRefreshList = true


    val poiDataList = ArrayList<LocationInfo>()
    val candidateDataList = ArrayList<LocationInfo>()
    lateinit var marker: Marker

    private var locationInfo = LocationInfo()
    private lateinit var mLocationClient: AMapLocationClient

    lateinit var inputTips: Inputtips


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySelectAddressBinding.inflate(layoutInflater)
        setContentView(binding.root)

        MapsInitializer.setApiKey("036b70b12eba4450a967f9835b4432f3")
        AMapLocationClient.setApiKey("036b70b12eba4450a967f9835b4432f3")



        initPermission()

        binding.map.onCreate(savedInstanceState)

        mapInit()
        searchEdInit()
        submitInit()


    }

    private fun submitInit() {

        binding.btnSelectAddressReturn.setOnClickListener {

            finish()
        }


        binding.btnSelectAddressSubmit.setOnClickListener {
            val position = marker.position
            val positionLatStr = "%.5f".format(position.latitude.toDouble())
            val positionLonStr = "%.5f".format(position.longitude.toDouble())

            for (i in poiDataList) {
                val latStr = "%.5f".format(i.latitude.toDouble())
                val lonStr = "%.5f".format(i.longitude.toDouble())
                if (latStr == positionLatStr && lonStr == positionLonStr) {
                    locationInfo = i
                }
            }

            for (i in candidateDataList) {
                val latStr = "%.5f".format(i.latitude.toDouble())
                val lonStr = "%.5f".format(i.longitude.toDouble())
                if (latStr == positionLatStr && lonStr == positionLonStr) {
                    locationInfo = i
                }
            }

            Log.d("intent检查", "$locationInfo")
            //toast输出检查


            val resultIntent = Intent()
            val bundle = Bundle()
            //下面的全部添加到bundle中返回
            bundle.putString("name", locationInfo.name)
            bundle.putString("address", locationInfo.address)
            bundle.putString("province", locationInfo.province)
            bundle.putString("city", locationInfo.city)
            bundle.putString("district", locationInfo.district)
            bundle.putString("latitude", locationInfo.latitude)
            bundle.putString("longitude", locationInfo.longitude)
            resultIntent.putExtras(bundle)
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
        }
    }

    private fun searchEdInit() {

        binding.etSearch.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // EditText 获取焦点时的操作
                // 例如，显示输入法
                binding.rvPoiList.visibility = android.view.View.GONE
                binding.btnSearchCancel.visibility = android.view.View.VISIBLE
                binding.rvCandidateList.visibility = android.view.View.VISIBLE

            } else {
                // EditText 失去焦点时的操作
                // 例如，隐藏输入法

            }
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {


            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                candidateDataList.clear()
                val inputQueue = InputtipsQuery(s.toString(), "")
                inputTips = Inputtips(this@SelectAddressActivity, inputQueue)
                inputTips.setInputtipsListener { tips, i ->

                    // 输出日志
                    Log.d("TAG", "onTextChanged: $tips")


                    for (tip in tips) {
                        //判空
                        if (tip.point == null) {
                            continue
                        }
                        val locationInfo = LocationInfo()
                        locationInfo.address = tip.district + tip.address
                        locationInfo.name = tip.name
                        locationInfo.province = tip.district
                        locationInfo.city = tip.district
                        locationInfo.district = tip.district
                        locationInfo.latitude = tip.point.latitude.toString()
                        locationInfo.longitude = tip.point.longitude.toString()
                        candidateDataList.add(locationInfo)

                    }

                    val poiAdapter = PoiAdapter(this@SelectAddressActivity, candidateDataList)
                    binding.rvCandidateList.layoutManager =
                        StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                    binding.rvCandidateList.adapter = poiAdapter


                }

                inputTips.requestInputtipsAsyn()
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        binding.btnSearchCancel.setOnClickListener {
            binding.etSearch.setText("")
            binding.etSearch.clearFocus()

            // 隐藏输入键盘
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
            binding.rvPoiList.visibility = android.view.View.VISIBLE
            binding.btnSearchCancel.visibility = android.view.View.GONE
            binding.rvCandidateList.visibility = android.view.View.GONE
        }
    }


    private fun mapInit() {


        binding.map.map.isTrafficEnabled = true
        aMap = binding.map.map


        val myLocationStyle = MyLocationStyle()
        myLocationStyle.interval(2000)
//        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER)
        val name = intent.getStringExtra("name")
        locationInfo.name = name.toString()
        val latitude = intent.getStringExtra("latitude")
        locationInfo.latitude = latitude.toString()
        val longitude = intent.getStringExtra("longitude")
        locationInfo.longitude = longitude.toString()
        val city = intent.getStringExtra("city")
        locationInfo.city = city.toString()
        val district = intent.getStringExtra("district")
        locationInfo.district = district.toString()
        val address = intent.getStringExtra("address")
        locationInfo.address = address.toString()
        val province = intent.getStringExtra("province")
        locationInfo.province = province.toString()

        Log.d("intent检查", "$latitude, $longitude, $name, $city, $district, $address, $province ")


        var locationType = MyLocationStyle.LOCATION_TYPE_LOCATE
        var intentLatLng: LatLng? = null
        if (latitude != "0.0" && longitude != "0.0" && latitude != "" && longitude != "" && latitude != null && longitude != null) {
            intentLatLng = LatLng(latitude.toDouble(), longitude.toDouble())

            locationType = MyLocationStyle.LOCATION_TYPE_SHOW

        }

        myLocationStyle.myLocationType(locationType)




        myLocationStyle.showMyLocation(true)

        val mUiSettings = aMap.uiSettings
        mUiSettings.isMyLocationButtonEnabled = true
        mUiSettings.isCompassEnabled = false
        mUiSettings.isMyLocationButtonEnabled = false
        aMap.isMyLocationEnabled = true
        mUiSettings.isScaleControlsEnabled = true

//        binding.map.map.myLocationStyle = myLocationStyle

        aMap.myLocationStyle = myLocationStyle


        val customIcon = BitmapDescriptorFactory.fromResource(R.drawable.maodian)
        val markerOptions = MarkerOptions()

        markerOptions.position(LatLng(0.0, 0.0)) // 初始位置可以是地图中心点
        markerOptions.icon(customIcon)
        marker = aMap.addMarker(markerOptions)
        aMap.setOnCameraChangeListener(object : AMap.OnCameraChangeListener {
            override fun onCameraChange(p0: CameraPosition) {
                // 监听地图移动事件
                marker.position = p0.target
            }

            override fun onCameraChangeFinish(p0: CameraPosition) {
                // 在地图移动结束后，将指针位置设为地图中心点
                if (shouldRefreshList) {
                    poiDataList.clear()
                    val centerLatLng = p0.target
                    searchCurrentLocation(centerLatLng)
                    searchPOIs(centerLatLng)
                }
                shouldRefreshList = true


            }
        })

        //获取上个页面传递的值

        if (intentLatLng != null) {
            marker.position = intentLatLng
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(intentLatLng, 15f))
        }

    }

    private fun searchCurrentLocation(centerLatLng: LatLng?) {

        val geocoderSearch = GeocodeSearch(this)
        geocoderSearch.setOnGeocodeSearchListener(this)
        val latLonPoint = LatLonPoint(centerLatLng?.latitude!!, centerLatLng.longitude)

// 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
        val query = RegeocodeQuery(latLonPoint, 200f, GeocodeSearch.AMAP)
        geocoderSearch.getFromLocationAsyn(query)

    }

    private fun searchPOIs(latLng: LatLng) {
        val poiSearchQuery = PoiSearch.Query("", "", "")
        poiSearchQuery.pageSize = 10
        poiSearchQuery.pageNum = 1
        val poiSearch = PoiSearch(this@SelectAddressActivity, poiSearchQuery)
        poiSearchQuery.requireSubPois(true)
        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
            override fun onPoiSearched(result: PoiResult?, rCode: Int) {


                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    // 在这里处理搜索结果，result.getPois()获取POI列表
                    val pois = result?.pois
                    // 处理搜索结果

                    pois?.let {
                        for (poi in it) {
                            // 对每个POI名称发起单独的POI检索请求，以获取详细信息
                            searchPoiDetail(poi)
                        }
                    }

                } else {
                    // 搜索失败处理
                    // ...
                    Log.d("检测", "搜索失败")
                }
            }

            override fun onPoiItemSearched(item: PoiItem?, rCode: Int) {
                // 不会被调用，因为这是在地图移动结束后进行的POI搜索


            }
        })
        poiSearch.bound =
            PoiSearch.SearchBound(LatLonPoint(latLng.latitude, latLng.longitude), 1000)

        poiSearch.searchPOIAsyn()
    }

    private fun searchPoiDetail(poi: PoiItem) {

        // 在这里发起单独的POI检索请求，以获取详细信息
        val poiSearchQuery = PoiSearch.Query(poi.title, "", "")
        val poiSearch = PoiSearch(this@SelectAddressActivity, poiSearchQuery)
        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener,
            PoiAdapterListener {
            override fun onPoiSearched(result: PoiResult?, rCode: Int) {
                if (rCode == AMapException.CODE_AMAP_SUCCESS) {
                    val pois = result?.pois
                    pois?.let {
                        // 在这里处理每个POI的详细信息，包括经纬度和地址
                        for (detailPoi in it) {

                            poiDataList.add(
                                LocationInfo(
                                    detailPoi.latLonPoint.latitude.toString(),
                                    detailPoi.latLonPoint.longitude.toString(),
                                    detailPoi.snippet,
                                    detailPoi.provinceName,
                                    detailPoi.cityName,
                                    detailPoi.adName,
                                    detailPoi.toString()
                                )
                            )
                        }
                        val layoutManager =
                            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
                        binding.rvPoiList.layoutManager = layoutManager
                        val adapter = PoiAdapter(this, poiDataList)
                        binding.rvPoiList.adapter = adapter
                    }


                } else {
                    // 搜索失败处理
                    Log.d("检测", "搜索失败")
                }
            }

            override fun onPoiItemSearched(item: PoiItem?, rCode: Int) {
                // 不会被调用
            }

            override fun onPoiItemSelected(position: Int, selectedLatLng: LatLng) {
                // 在这里处理选中的POI项，例如移动地图视角和Marker位置等操作
                val cameraUpdate = CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f) // 设置缩放级别
                aMap.animateCamera(cameraUpdate, 1000, null)
                shouldRefreshList = false

            }
        })

        poiSearch.searchPOIAsyn()
    }


    private fun initPermission() {
        PermissionX.init(this)
            .permissions(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
            )
            .onExplainRequestReason { scope, deniedList ->
                val message = "APP需要您同意以下权限才能正常使用"
                scope.showRequestReasonDialog(deniedList, message, "允许", "取消")
            }
            .onForwardToSettings { scope, deniedList ->
                // 判断用户是否手动开启了权限
                if (PermissionX.isGranted(
                        this@SelectAddressActivity,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) &&
                    PermissionX.isGranted(
                        this@SelectAddressActivity,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                ) {
                    Toast.makeText(
                        this@SelectAddressActivity,
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
                    haveLocationPermission = true
                    getLocationService() // 在这里调用获取位置服务方法
                } else {
                    haveLocationPermission = false
                    Toast.makeText(this, "您拒绝了如下权限：$deniedList", Toast.LENGTH_SHORT).show()
                    System.exit(0)
                }
            }
    }

    private var haveLocationPermission = false
    private fun getLocationService() {
        if (haveLocationPermission) {
            //判断是否开启服务getSaveResponse
            var locationManager =
                this@SelectAddressActivity.getSystemService(LOCATION_SERVICE) as LocationManager
            if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                BLogUtils.e(TAG, "用户打开定位服务")
                getLocation()
            } else {
//                BLogUtils.e(TAG, "用户关闭定位服务")
                MaterialAlertDialogBuilder(this@SelectAddressActivity)
                    .setMessage("请打开位置服务开关，否则可能无法正常使用")
                    .setCancelable(false)
                    .setPositiveButton("去打开") { _, _ ->
                        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                        startActivityForResult(intent, 1)
                        startActivity(intent)
                    }
                    .setNegativeButton("退出") { _, _ -> System.exit(0) }
                    .show()
            }
        }
    }

    private fun getLocation() {
        AMapLocationClient.updatePrivacyShow(this, true, true);
        AMapLocationClient.updatePrivacyAgree(this, true);
        //声明AMapLocationClient类对象

        //声明定位回调监听器
        //val mLocationListener = AMapLocationListener() {}

        var mLocationOption = AMapLocationClientOption();
        mLocationOption.setLocationPurpose(AMapLocationClientOption.AMapLocationPurpose.SignIn)


        mLocationClient = AMapLocationClient(applicationContext)
        mLocationClient.setLocationOption(mLocationOption);
        //设置场景模式后最好调用一次stop，再调用start以保证场景模式生效
        mLocationClient.stopLocation();
        mLocationClient.startLocation();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //设置是否允许模拟位置,默认为true，允许模拟位置
        mLocationOption.setMockEnable(true);
        //单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
        mLocationOption.setHttpTimeOut(20000);
        //关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation()
        //设置定位回调监听
//        mLocationClient.setLocationListener(MyLocationListener())
    }


    override fun onDestroy() {
        super.onDestroy()
        binding.map.onDestroy()
        mLocationClient.stopLocation()

    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
        getLocationService()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        binding.map.onSaveInstanceState(outState)
    }

    override fun onRegeocodeSearched(p0: RegeocodeResult?, p1: Int) {


        val regeocodeAddress = p0?.regeocodeAddress
        val regeocodeQuery = p0?.regeocodeQuery


        poiDataList.add(
            LocationInfo(
                regeocodeQuery?.point?.latitude.toString(),
                regeocodeQuery?.point?.longitude.toString(),
                regeocodeAddress?.formatAddress.toString(),
                regeocodeAddress?.province.toString(),
                regeocodeAddress?.city.toString(),
                regeocodeAddress?.district.toString(),
                "我的位置"
            )
        )
        val layoutManager =
            StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL)
        binding.rvPoiList.layoutManager = layoutManager
        val adapter = PoiAdapter(this, poiDataList)
        binding.rvPoiList.adapter = adapter
    }

    override fun onGeocodeSearched(p0: GeocodeResult?, p1: Int) {
        TODO("Not yet implemented")

    }

    override fun onPoiItemSelected(position: Int, selectedLatLng: LatLng) {
        // 在这里处理选中的POI项，例如移动地图视角和Marker位置等操作
        val cameraUpdate = CameraUpdateFactory.newLatLngZoom(selectedLatLng, 15f) // 设置缩放级别
        aMap.animateCamera(cameraUpdate, 1000, null)
        shouldRefreshList = false

    }


}