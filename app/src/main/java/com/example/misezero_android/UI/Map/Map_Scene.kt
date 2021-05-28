package com.example.misezero_android.UI.Map

import android.os.Build
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.misezero_android.AirpItem
import com.example.misezero_android.MainActivity
import com.example.misezero_android.R
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.ClusterManager
import kotlinx.android.synthetic.main.fragment_map__scene.*

class Map_Scene : Fragment(), OnMapReadyCallback {

    private var activity: MainActivity? = null
    // 지도
    private lateinit var mView: MapView
    private var mClusterManager: ClusterManager<ClusterPosition>? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map__scene, container, false)

        // 액티비티 참조
        activity = getActivity() as MainActivity?

        // 지도
        mView = view.findViewById(R.id.mapview) as MapView
        mView?.onCreate(savedInstanceState)
        mView?.getMapAsync(this)
        return view
    }

    override fun onStart() {
        super.onStart()
        mView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mView?.onDestroy()
    }


    ////////////////////////////////////////////////////////////////////
    /// 디스플레이
    ////////////////////////////////////////////////////////////////////
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)


        infoWindow.visibility = View.GONE
//        // 구글 지도 생성
//        if(mapview != null) {
//            mapview?.onCreate(savedInstanceState)
//        }

//        infoWindow_close.setOnClickListener {
//            infoWindow.visibility = View.GONE
//        }
    }

    ////////////////////////////////////////////////////////////////////
    /// 활성화 될 때.
    ////////////////////////////////////////////////////////////////////
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onResume() {
        super.onResume()
        mView?.onResume()
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        // 시작시 보여지는 지점
        val myLocation = LatLng(36.858403, 127.907922)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        googleMap?.moveCamera(CameraUpdateFactory.zoomTo(6.5f))

        // Position the map.
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    36.858403,
                    127.907922
                ), 6.5f
            )
        )

        //인접한 마커들을 묶어서 보여주는 클러스터 기능 , 리스너 할당
        mClusterManager = ClusterManager<ClusterPosition>(requireContext(), googleMap)
        googleMap?.setOnCameraIdleListener(mClusterManager)
        googleMap?.setOnMarkerClickListener(mClusterManager)

        // 표시할 위치 지점 데이터 로드.
        // 측정소
        var file  = resources.openRawResource(R.raw.test)
        val rows: List<List<String>> = csvReader().readAll(file)

        //파일 내 모든 리스트 불러옴
        for(i in rows) {
            Log.d("MiseLogRow", i.toString())

//            Locale.getDefault().language == "ko"
            // 디바이스가 한글로 설정되어 있을 때.
            var offsetItem = ClusterPosition(i[6].toDouble(), i[5].toDouble(), i[1].toString(), i[3].toString())
            mClusterManager!!.addItem(offsetItem)
        }
        
//        mClusterManager!!.setOnClusterClickListener {
//
//            Log.d("MiseLog", "clicked setOnClusterClickListener")
//            true
//        }

        /// 마커를 클릭하였을 때.
        mClusterManager!!.setOnClusterItemClickListener {
            Log.d("MiseLog", "clicked setOnClusterItemClickListener")
            updateInfoWindow(it.mTitle)
            infoWindow.visibility = View.VISIBLE
            false
        }
    }

    /////////////////////////////////////////////////////////////
    /// 인포 박스 설정
    /////////////////////////////////////////////////////////////
    fun getKorName(name : String) : String{
        // 표시할 위치 지점 데이터 로드.
        var file  = resources.openRawResource(R.raw.test)
        val rows: List<List<String>> = csvReader().readAll(file)

        for(i in rows) {
            if (i[2].toString() == name ||  i[1].toString() == name){
                return i[1].toString()
            }
        }
        return name
    }



    fun updateInfoWindow(name : String){
        activity?.coreInfo?.let{
            var name = getKorName(name)
            pm10Grade.text = it.getAirPolutionStationData(name, AirpItem.PM10_WHO, blocal = true)
            pm10Value.text = it.getAirPolutionStationData(name, AirpItem.미세먼지_농도) + "㎍/m³"
            fineImg.setImageResource(it.getAirPolutionStationImg(name, AirpItem.PM10_WHO))

            pm25Grade.text = it.getAirPolutionStationData(name, AirpItem.PM25_WHO, blocal = true)
            pm25Value.text = it.getAirPolutionStationData(name, AirpItem.초미세먼지_농도) + "㎍/m³"
            microImg.setImageResource(it.getAirPolutionStationImg(name, AirpItem.PM25_WHO))

            o3Grade.text = it.getAirPolutionStationData(name, AirpItem.오존_지수, blocal = true)
            o3Value.text = it.getAirPolutionStationData(name, AirpItem.오존_농도) + "㎍/m³"
            ozoneImg.setImageResource(it.getAirPolutionStationImg(name, AirpItem.오존_지수))

            coGrade.text = it.getAirPolutionStationData(name, AirpItem.일산화탄소_지수, blocal = true)
            coValue.text = it.getAirPolutionStationData(name, AirpItem.일산화탄소_농도) + "㎍/m³"
            carbonImg.setImageResource(it.getAirPolutionStationImg(name, AirpItem.일산화탄소_지수))

            no2Grade.text = it.getAirPolutionStationData(name, AirpItem.이산화질소_지수, blocal = true)
            no2Value.text = it.getAirPolutionStationData(name, AirpItem.이산화질소_농도) + "㎍/m³"
            nitrogenImg.setImageResource(it.getAirPolutionStationImg(name, AirpItem.이산화질소_지수))

            so2Grade.text = it.getAirPolutionStationData(name, AirpItem.아황산가스_지수, blocal = true)
            so2Value.text = it.getAirPolutionStationData(name, AirpItem.아황산가스_농도) + "㎍/m³"
            sulfurousImg.setImageResource(it.getAirPolutionStationImg(name, AirpItem.아황산가스_지수))
        }
    }
}