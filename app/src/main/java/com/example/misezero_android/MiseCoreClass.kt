package com.example.misezero_android

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

@Suppress("DEPRECATION")
class MiseCoreClass {

    // 관심 지역 정보
    var userInfoData = mutableListOf<MiseDataClass>()

    // 모든 측정소 정보
    var AirpStationInfo = mutableMapOf<String, MutableMap<String, MutableMap<String, String>>>()

    // 현재 및 디스플레이 정보
    var curInfoData = MiseDataClass()
    var disPlayData : MiseDataClass? = null

    fun onInitialize() {
        disPlayData = curInfoData
        getAirPolutionSatationInfo()
    }

    ////////////////////////////////////////////////////
    // 디스플레이 정보 설정
    ////////////////////////////////////////////////////
    fun setDispInfo(index : Int = -1){
        if (index > -1){
            //관심지역 클릭했을때
            disPlayData = userInfoData[index]
        }else{
            //아니면
            disPlayData = curInfoData
        }
    }

    ///대기오염 측정소 데이터 모두 가져오는곳
    fun getAirPolutionSatationInfo() {
        FirebaseDatabase.getInstance().getReference("대기오염/실시간").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                AirpStationInfo = p0.value as MutableMap<String, MutableMap<String, MutableMap<String, String>>>
            }
            override fun onCancelled(p0: DatabaseError) {
            }
        })
    }

    fun getAirPolutionStationData(station :  String, airpItem : AirpItem , blocal : Boolean = false) : String{
        for (sido in AirpStationInfo){
            for (city in sido.value){
                if (city.key == station){
                    var res = city.value.get(airpItem.name) ?: ""
                    res = res.split("_")[0]

                    return res
                }
            }
        }
        return ""
    }

    fun getAirPolutionStationImg(station :  String, airpItem: AirpItem) : Int{
        for (sido in AirpStationInfo){
            for (city in sido.value){
                if (city.key == station){
                    var res = city.value.get(airpItem.name) ?: ""
                    return when(res){
                        "좋음"->{R.drawable.goodw}
                        "보통"->{R.drawable.averagew}
                        "나쁨"->{R.drawable.badw}
                        "매우나쁨"->{R.drawable.seriousw}
                        "매우좋음_WHO"->{R.drawable.verygood}
                        "좋음_WHO"->{R.drawable.good}
                        "보통_WHO"->{R.drawable.moderate}
                        "조심_WHO"->{R.drawable.careful}
                        "약간나쁨_WHO"->{R.drawable.unhealthyfor}
                        "나쁨_WHO"->{R.drawable.bad}
                        "매우나쁨_WHO"->{R.drawable.verybad}
                        "심각_WHO"->{R.drawable.serious}
                        else->{R.drawable.nodata}
                    }
                }
            }
        }
        return R.drawable.nodata
    }
}