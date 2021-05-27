package com.example.misezero_android

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.firebase.database.*
import okhttp3.*
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

enum class InfoItem(name: String){
    이름("이름"),
    행정동명("행정동명"),
    행정동명_영문("행정동명 영문"),
    측정소명("측정소명"),
    측정소명_영문("측정소명 영문"),
    측정소주소("측정소주소"),
    측정소주소_영문("측정소주소 영문"),
    날씨_기상특보_경로("측정소주소 영문"),
    날씨_단기예보_경로("날씨 단기예보 경로"),
    날씨_실시간_경로("날씨 실시간 경로"),
    날씨_중기예보_경로("날씨 중기예보 경로"),
    날씨_초단기예보_경로("날씨 초단기예보 경로"),
    대기_시군구평균_경로("대기 시군구평균 경로"),
    대기_시도평균_경로("대기 시도평균 경로"),
    대기_실시간_경로("대기 실시간 경로"),
    대기_예보_경로("대기 예보 경로")
}

enum class WeatherItem(name : String){
    기온("기온"),
    습도("습도"),
    풍속("풍속"),
    강수량("강수량"),
    강수형태("강수형태"),
    기상상태("기상상태"),
    낙뢰("낙뢰"),
    하늘상태("하늘상태"),
    강수확률("강수확률"),
    최고기온("최고기온"),
    최저기온("최저기온"),
}

enum class WeatherFrstType(name : String){
    단기("단기"),
    초단기("초단기"),
    중기("중기"),
    현재("현재"),
}

//대기오염
enum class AirpItem(name : String){
    미세먼지_농도("미세먼지_농도"),
    미세먼지_지수("미세먼지_지수"),
    미세먼지1시간_지수("미세먼지1시간_지수"),
    초미세먼지_농도("초미세먼지_농도"),
    초미세먼지1시간_농도("초미세먼지1시간_농도"),
    초미세먼지_지수("초미세먼지_지수"),
    아황산가스_농도("아황산가스_농도"),
    아황산가스_지수("아황산가스_지수"),
    오존_농도("오존_농도"),
    오존_지수("오존_지수"),
    일산화탄소_농도("일산화탄소_농도"),
    일산화탄소_지수("일산화탄소_지수"),
    이산화질소_농도("이산화질소_농도"),
    이산화질소_지수("이산화질소_지수"),
    PM10_WHO("PM10_WHO"),
    PM25_WHO("PM25_WHO"),
}

class MiseDataClass {
    // 파이어베이스 레퍼런스
    var curAddress = ""
    var curName = ""
    var listener: ValueEventListener? = null
    var database: FirebaseDatabase? = null
    var ref: DatabaseReference? = null
    var firebaseInfo = mutableMapOf<String, String>()
    var firebaseUpdateInfo = mutableMapOf<String, MutableMap<String, String>>()

    // 데이터 업데이트 상태 여부

    var isUpdate: Boolean = false
    var iscomplete: Boolean = false
    var weather_rt_info = mutableMapOf<String, String>()
    var weather_st_info = mutableMapOf<String, MutableMap<String, String>>()
    var weather_sst_info = mutableMapOf<String, MutableMap<String, String>>()
    var weather_mt_info = mutableMapOf<String, MutableMap<String, String>>()
    var weather_fr_info = mutableMapOf<String, MutableMap<String, String>>()
    var air_fr_info = mutableMapOf<String, MutableMap<String, String>>()
    var air_rt_info = mutableMapOf<String, String>()
    var air_sido_info = mutableMapOf<String, String>()
    var air_sigungu_info = mutableMapOf<String, String>()

    ///////////////////////////////////////////////////////
    //// 디바이스 언어
    ///////////////////////////////////////////////////////
    fun getDeviceLang(): String {
//        return Locale.getDefault().language
//        System.out.println()

        return "ko"
    }

    ///////////////////////////////////////////////////////
    //// 정보 얻기
    ///////////////////////////////////////////////////////
    fun getInfo(info: InfoItem): String {
        var lang = getDeviceLang()
        return when (info) {
            InfoItem.이름 -> {
                return this.curName
            }
            InfoItem.행정동명 -> {
                firebaseInfo[InfoItem.행정동명.name].toString()
            }
            InfoItem.측정소명 -> {
                firebaseInfo[InfoItem.측정소명.name].toString()
            }
            InfoItem.측정소주소 -> {
                firebaseInfo[InfoItem.측정소주소.name].toString()
            }
            else -> {
                ""
            }
        }
    }

    fun getWeatherSSTInfo(name: WeatherItem, date: String, blocal: Boolean = false): String {

        if (name == WeatherItem.기상상태) {
            if (getDeviceLang() == "ko") {
                return weather_sst_info.get(date)?.get(name.name) ?: ""
            } else {
                var res = weather_sst_info.get(date)?.get(name.name) ?: ""

                if (blocal == false) {
                    return res
                }

                return when (res) {
                    "맑음" -> {
                        "sunny"
                    }
                    "맑고 비" -> {
                        "sunnyrain"
                    }
                    "맑고 진눈개비" -> {
                        "sunnysleet"
                    }
                    "맑고 눈" -> {
                        "sunnysnow"
                    }
                    "맑고 소나기" -> {
                        "sunnyshower"
                    }
                    "맑고 빗방울" -> {
                        "sunnydrop"
                    }
                    "맑고 빗방울/눈날림" -> {
                        "sunnyrainsnow"
                    }
                    "맑고 눈날림" -> {
                        "sunnydrifting"
                    }
                    "구름많음" -> {
                        "cloud"
                    }
                    "구름많고 비" -> {
                        "rain"
                    }
                    "구름많고 진눈개비" -> {
                        "cloudsleet"
                    }
                    "구름많고 눈" -> {
                        "cloudsnow"
                    }
                    "구름많고 소나기" -> {
                        "cloudshower"
                    }
                    "구름많고 빗방울" -> {
                        "clouddrop"
                    }
                    "구름많고 빗방울/눈날림" -> {
                        "cloudrainsnow"
                    }
                    //"구름많고 눈날림" -> {R.drawable.d}
                    "흐림" -> {
                        "blackcloud"
                    }
                    "흐리고 비" -> {
                        "blackcloudrain"
                    }
                    "흐리고 진눈개비" -> {
                        "blackcloudsleet"
                    }
                    "흐리고 눈" -> {
                        "blackcloudsnow"
                    }
                    "흐리고 소나기" -> {
                        "blackcloudshower"
                    }
                    "흐리고 빗방울" -> {
                        "blackclouddrop"
                    }
                    "흐리고 빗방울/눈날림" -> {
                        "blackcloudrainsnow"
                    }
                    "흐리고 눈날림" -> {
                        "blackclouddrifting"
                    }
                    else -> {
                        res
                    }
                }
            }
        } else {
            return weather_sst_info.get(date)?.get(name.name) ?: ""
        }
    }

    fun getWeatherSTInfo(item: WeatherItem, date: String, blocal: Boolean = false): String {
        var res = weather_st_info.get(date)?.get(item.name) ?: ""

        if (blocal && getDeviceLang() != "ko") {
            return when (res) {
                "맑음" -> {
                    "sunny"
                }
                "맑고 비" -> {
                    "sunnyrain"
                }
                "맑고 진눈개비" -> {
                    "sunnysleet"
                }
                "맑고 눈" -> {
                    "sunnysnow"
                }
                "맑고 소나기" -> {
                    "sunnyshower"
                }
                "맑고 빗방울" -> {
                    "sunnydrop"
                }
                "맑고 빗방울/눈날림" -> {
                    "sunnyrainsnow"
                }
                "맑고 눈날림" -> {
                    "sunnydrifting"
                }
                "구름많음" -> {
                    "cloud"
                }
                "구름많고 비" -> {
                    "rain"
                }
                "구름많고 진눈개비" -> {
                    "cloudsleet"
                }
                "구름많고 눈" -> {
                    "cloudsnow"
                }
                "구름많고 소나기" -> {
                    "cloudshower"
                }
                "구름많고 빗방울" -> {
                    "clouddrop"
                }
                "구름많고 빗방울/눈날림" -> {
                    "cloudrainsnow"
                }
                //"구름많고 눈날림" -> {R.drawable.d}
                "흐림" -> {
                    "blackcloud"
                }
                "흐리고 비" -> {
                    "blackcloudrain"
                }
                "흐리고 진눈개비" -> {
                    "blackcloudsleet"
                }
                "흐리고 눈" -> {
                    "blackcloudsnow"
                }
                "흐리고 소나기" -> {
                    "blackcloudshower"
                }
                "흐리고 빗방울" -> {
                    "blackclouddrop"
                }
                "흐리고 빗방울/눈날림" -> {
                    "blackcloudrainsnow"
                }
                "흐리고 눈날림" -> {
                    "blackclouddrifting"
                }
                else -> {
                    res
                }
            }
        } else {
            return res
        }
    }

    fun getWeatherRTInfo(name: WeatherItem): String {
        return weather_rt_info.get(name.name) ?: ""
    }

    fun getWeatherMTInfo(name: WeatherItem, date: String): String {
        return weather_mt_info.get(date)?.get(name.name) ?: ""
    }

    fun getBackGroundColor(name: AirpItem): String {
        val res = getAirpRTInfo(name)

        return when (res) {
            "매우좋음_WHO" -> {
                "#E63399FF"
            } //90%
            "좋음_WHO" -> {
                "#E600CC33"
            } //90%
            "보통_WHO" -> {
                "#E6FFD400"
            }  //90%
            "조심_WHO" -> {
                "#E6FFC700"
            } //90%
            "약간나쁨_WHO" -> {
                "#E6FF8A00"
            } //90%
            "나쁨_WHO" -> {
                "#E6FF6666"
            } //90%
            "매우나쁨_WHO" -> {
                "#CCB20045"
            } //80%
            "심각_WHO" -> {
                "#B3666666"
            } //70%
            else -> {
                "#ffffff"
            }
        }
    }

    fun getAirpRTInfo(name: AirpItem, blocal: Boolean = false): String {
        var res = air_rt_info.get(name.name) ?: ""

        if (blocal == false) {
            return res
        }
        if (getDeviceLang() == "ko") {
            return when (res) {
                "매우좋음_WHO" -> {
                    "매우좋음"
                }
                "좋음_WHO" -> {
                    "좋음"
                }
                "보통_WHO" -> {
                    "보통"
                }
                "조심_WHO" -> {
                    "조심"
                }
                "약간나쁨_WHO" -> {
                    "약간나쁨"
                }
                "나쁨_WHO" -> {
                    "나쁨"
                }
                "매우나쁨_WHO" -> {
                    "매우나쁨"
                }
                "심각_WHO" -> {
                    "심각"
                }
                else -> {
                    res
                }
            }
        } else {
            return when (res) {
                "좋음" -> {
                    "Good"
                }
                "보통" -> {
                    "Average"
                }
                "나쁨" -> {
                    "Bad"
                }
                "매우나쁨" -> {
                    "Serious"
                }
                "매우좋음_WHO" -> {
                    "veryGood"
                }
                "좋음_WHO" -> {
                    "good"
                }
                "보통_WHO" -> {
                    "moderate"
                }
                "조심_WHO" -> {
                    "careful"
                }
                "약간나쁨_WHO" -> {
                    "unhealthyfor"
                }
                "나쁨_WHO" -> {
                    "bad"
                }
                "매우나쁨_WHO" -> {
                    "verybad"
                }
                "심각_WHO" -> {
                    "serious"
                }
                else -> {
                    res
                }
            }
        }
    }

    fun getAirpFRInfo(name: AirpItem, date: String, blocal: Boolean = false): String {
        var res = air_fr_info.get(date)?.get(name.name) ?: ""

        if (blocal == false) {
            return res
        }
        if (getDeviceLang() == "ko") {
            return res
        }

        return when (res) {
            "좋음" -> { "Good" }
            "보통" -> { "Average" }
            "나쁨" -> { "Bad" }
            "매우나쁨" -> { "Serious" }
            else -> { res }
        }
    }

    fun getAirpGradeImgName(item: AirpItem, date: String? = null): Int {
        var res = ""

        if (date == null) {
            res = getAirpRTInfo(item)
        } else {
            res = getAirpFRInfo(item, date)
        }

        return when (res) {
            "좋음" -> {
                R.drawable.goodw
            }
            "보통" -> {
                R.drawable.averagew
            }
            "나쁨" -> {
                R.drawable.badw
            }
            "매우나쁨" -> {
                R.drawable.seriousw
            }
            "매우좋음_WHO" -> {
                R.drawable.verygood
            }
            "좋음_WHO" -> {
                R.drawable.good
            }
            "보통_WHO" -> {
                R.drawable.moderate
            }
            "조심_WHO" -> {
                R.drawable.careful
            }
            "약간나쁨_WHO" -> {
                R.drawable.unhealthyfor
            }
            "나쁨_WHO" -> {
                R.drawable.bad
            }
            "매우나쁨_WHO" -> {
                R.drawable.verybad
            }
            "심각_WHO" -> {
                R.drawable.serious
            }
            else -> {
                R.drawable.nodata
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)  //
    fun getWeatherGradeImgName(item: WeatherItem, type: WeatherFrstType, date: String = ""): Int {

        var grade = when (type) {
            WeatherFrstType.단기 -> {
                getWeatherSTInfo(item, date)
            }
            WeatherFrstType.중기 -> {
                getWeatherMTInfo(item, date)
            }
            WeatherFrstType.초단기 -> {
                getWeatherSSTInfo(item, date)
            }
            WeatherFrstType.현재 -> {
                // 현재 시간 구하기.
                var date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"))
                getWeatherSSTInfo(item, date)
            }
            else -> { "" }
    }
        return when(grade){
            "맑음" -> {R.drawable.sunny}
            "맑고 비" -> {R.drawable.sunnyrain}
            "맑고 진눈개비" -> {R.drawable.sunnysleet}
            "맑고 눈" -> {R.drawable.sunnysnow}
            "맑고 소나기" -> {R.drawable.sunnyshower}
            "맑고 빗방울" -> {R.drawable.sunnydrop}
            "맑고 빗방울/눈날림" -> {R.drawable.sunnyrainsnow}
            "맑고 눈날림" -> {R.drawable.sunnydrifting}
            "구름많음" -> {R.drawable.cloud}
            "구름많고 비" -> {R.drawable.rain}
            "구름많고 진눈개비" -> {R.drawable.cloudsleet}
            "구름많고 눈" -> {R.drawable.cloudsnow}
            "구름많고 소나기" -> {R.drawable.cloudshower}
            "구름많고 빗방울" -> {R.drawable.clouddrop}
            "구름많고 빗방울/눈날림" -> {R.drawable.cloudrainsnow}
            //"구름많고 눈날림" -> {R.drawable.d}
            "흐림" -> {R.drawable.blackcloud}
            "흐리고 비" -> {R.drawable.blackcloudrain}
            "흐리고 진눈개비" -> {R.drawable.blackcloudsleet}
            "흐리고 눈" -> {R.drawable.blackcloudsnow}
            "흐리고 소나기" -> {R.drawable.blackcloudshower}
            "흐리고 빗방울" -> {R.drawable.blackclouddrop}
            "흐리고 빗방울/눈날림" -> {R.drawable.blackcloudrainsnow}
            "흐리고 눈날림" -> {R.drawable.blackclouddrifting}
            else->{R.drawable.nodata}
        }
    }

    /////////////////////////////////////////////////////
    // 업데이트
    /////////////////////////////////////////////////////
    fun onUpdate(x : Double, y : Double, name : String = "My Location"){
        curName = name
        // 좌표를 이용하여 주소 찾기
        getAddressByKaKao(x, y, name)
        // 파이어베이스 정보 가져오기
        //getFireBaseData()
    }

    fun getInfoDataByFirebase(address: String) {
        getAddressParse(address)?.let {
            var arrStr = it.split("-")

            var path = "행정구역정보/${arrStr[0]}/${arrStr[1]}/${arrStr[2]}/"
            FirebaseDatabase.getInstance().getReference(path).addListenerForSingleValueEvent(object : ValueEventListener{
                override fun onCancelled(p0: DatabaseError) {}

                override fun onDataChange(snapshot: DataSnapshot) {
                    firebaseInfo = snapshot.value as MutableMap<String, String>
                    getFireBaseUpateInfo()
                }
            })
        }
    }

    //////////////////////////////////////////////////////////////
    /// 파이어베이스에서 데이터를 가져온다.
    //////////////////////////////////////////////////////////////
    fun getFireBaseUpateInfo(){

        FirebaseDatabase.getInstance().getReference("업데이트").addValueEventListener(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                var localData = snapshot.value as MutableMap<String, MutableMap<String, String>>

                for (gubun in localData) {
                    for (sub in gubun.value){
                        getFirebaseData(gubun.key, sub.key, sub.value)
                    }
                }

                // check updaet
                //isUpdate = true
                iscomplete = true
            }
        })
    }

    fun getFirebaseData(gubun : String, name : String, value : String){

        var a = InfoItem.날씨_단기예보_경로

        var path = when("$gubun $name"){
            "날씨정보 단기예보"->{
                firebaseInfo["날씨 단기예보 경로"]
            }
            "날씨정보 초단기예보"->{
                firebaseInfo["날씨 초단기예보 경로"]
            }
            "날씨정보 중기예보"->{
                firebaseInfo["날씨 중기예보 경로"]
            }
            "날씨정보 실시간"->{
                firebaseInfo["날씨 실시간 경로"]
            }
            "대기오염 실시간"->{
                firebaseInfo["대기 실시간 경로"]
            }
            //"대기오염 시군구평균"->{InfoItem.날씨_단기예보_경로.name}
            //"대기오염 시도평균"->{InfoItem.날씨_단기예보_경로.name}
            "대기오염 예보"->{
                firebaseInfo["대기 예보 경로"]
            }
            else ->{null}
        }

        if (path == null){return}

        // 업데이트 일자가 변화없으면 리턴
        if (firebaseUpdateInfo[gubun]?.get(name) == value){ return}

        // 측정 시간 업데이트
        firebaseUpdateInfo[gubun]?.set(name, value)

        // 데이터 업데이트
        FirebaseDatabase.getInstance().getReference(path).addListenerForSingleValueEvent(object : ValueEventListener{
            override fun onCancelled(p0: DatabaseError) {}

            override fun onDataChange(snapshot: DataSnapshot) {
                when("$gubun $name"){
                    "날씨정보 단기예보"->{
                        var localData = snapshot.value as? MutableMap<String, MutableMap<String, String>>
                        localData?.let{
                            weather_st_info = it
                            isUpdate = true
                        }
                    }
                    "날씨정보 초단기예보"->{
                        var localData = snapshot.value as? MutableMap<String, MutableMap<String, String>>
                        localData?.let{
                            weather_sst_info = it
                            isUpdate = true
                        }
                    }
                    "날씨정보 중기예보"->{
                        var localData = snapshot.value as? MutableMap<String, MutableMap<String, String>>
                        localData?.let{
                            weather_mt_info = it
                            isUpdate = true
                        }
                    }
                    "대기오염 예보"->{
                        var localData = snapshot.value as? MutableMap<String, MutableMap<String, String>>
                        localData?.let{
                            air_fr_info = it
                            isUpdate = true
                        }
                    }
                    "날씨정보 실시간"->{
                        var localData = snapshot.value as? MutableMap<String, String>
                        localData?.let{
                            weather_rt_info = it
                            isUpdate = true
                        }
                    }
                    "대기오염 실시간"->{
                        var localData = snapshot.value as? MutableMap<String, String>
                        localData?.let{
                            air_rt_info =  it
                            isUpdate = true
                        }
                    }
                    else->{}
                }
            }
        })
    }

    fun getAddressByKaKao(x : Double, y: Double, name : String){
        val url = "https://dapi.kakao.com/v2/local/geo/coord2regioncode.json?x=$y&y=$x"
        val okHttpClient = OkHttpClient()
        val request = Request.Builder().addHeader("Authorization", "KakaoAK 5e8d63b477cb5684045f8ec30f4ccbbd").url(url).build()

        okHttpClient.newCall(request).enqueue(object : Callback {

            override fun onFailure(call: okhttp3.Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: okhttp3.Call, response: okhttp3.Response) {
                val res = response.body?.string()
                try {

                    var address = JSONArray(JSONObject(res).getString("documents")).getJSONObject(1).getString("address_name")

                    if (address != curAddress){
                        curAddress = address
                        curName = name
                        getInfoDataByFirebase(address)
                    }
                } catch (e: JSONException) {
                    e.printStackTrace()
                }
            }
        })
    }
    //////////////////////////////////////////////////////////////////
    /// 주소 파싱
    ///////////////////////////////////////////////////////////////////
    fun getAddressParse(address: String):String?{
        var arrStr = address.split(" ")
        var umd = arrStr.last().replace(".", "_")
        return when(arrStr.count()){
            2 -> { "${arrStr[0]}-${arrStr[0]}-${umd}" }
            4 -> { "${arrStr[0]}-${arrStr[1]} ${arrStr[2]}-${umd}"} // ex) "경기도" "수원시" "영통구" "광교1동" -> "경기도" "수원시영통구" "광교1동"
            3 -> { "${arrStr[0]}-${arrStr[1]}-${umd}"}
            else ->{ null }
        }
    }
}