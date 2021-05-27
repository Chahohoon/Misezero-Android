package com.example.misezero_android.UI.Weather

import android.content.Context
import android.graphics.Color
import android.os.*
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.misezero_android.*
import kotlinx.android.synthetic.main.fragment_current__scene.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class Current_Scene : Fragment() {
    // TODO: Rename and change types of parameters

    private var mainActivity: MainActivity? = null

    var isRunning = false
    var handler: DisplayHandier? = null
    var refCnt = 0

    /**
     * fFragment가 Activity에 접근할 때
     */
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }
    /**
     * 레이아웃을 참조하는 곳 버튼, 텍스트뷰 등 Ui 초기화 가능
     */
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_current__scene, container, false)
    }

    /**
     * 액티비티에서 onCreate()가 호출되고 나서 호출되는 메소드 View 변경 가능
     */
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        currentBtn.setOnClickListener {
            mainActivity?.onRefreshFragment(Current_Scene())
        }
        todayBtn.setOnClickListener {
            mainActivity?.onRefreshFragment(Today_Scene())
        }
        weekBtn.setOnClickListener {
            mainActivity?.onRefreshFragment(Week_Scene())
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        setListenerEnable(true)
        this.isRunning = true
        handler = DisplayHandier()

        var thread = ThreadClass()
        thread.start()

        onRefreshScreen()

    }

    //화면이 가려질때
    override fun onPause() {
        super.onPause()
        this.isRunning = false
        this.handler = null
    }

    override fun onStop() {
        super.onStop()
        isRunning = false
    }

    override fun onDestroy() {
        super.onDestroy()
        this.isRunning = false
    }

    override fun onDetach() {
        super.onDetach()
        mainActivity = null
        isRunning = false
    }

    ////////////////////////////////////////////////////////////////
    //// 화면이 가져지거나, 백그라운드로 진입할 때, 리스닝 속성을 제거
    ////////////////////////////////////////////////////////////////
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListenerEnable(value : Boolean){
        currentBtn.isEnabled = value
        todayBtn.isEnabled = value
        weekBtn.isEnabled = value
        btn_update.isEnabled = value
    }


    //화면 갱신
    @RequiresApi(Build.VERSION_CODES.O)
    private fun onRefreshScreen(red: Int = 0) {
        mainActivity?.coreInfo?.disPlayData?.let {
            currentText.text = it.getInfo(InfoItem.행정동명)
            measureText.text = it.getInfo(InfoItem.측정소주소)


            // 날씨
            CurrentTemp.text = it.getWeatherRTInfo(WeatherItem.기온) + " ℃"
            currentHumid.text = it.getWeatherRTInfo(WeatherItem.습도) + " %"
            currentWid.text = it.getWeatherRTInfo(WeatherItem.풍속) + " m/s"

            var date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"))
            CurrentWeather.text = it.getWeatherSSTInfo(WeatherItem.기상상태, date, blocal = true)
            weatherImg.setImageResource(it.getWeatherGradeImgName(WeatherItem.기상상태, WeatherFrstType.현재))


            // 대기오염
            conditionImg.setImageResource(it.getAirpGradeImgName(AirpItem.PM10_WHO))
            conditionValue.text = it.getAirpRTInfo(AirpItem.미세먼지_농도) + " ㎍/m³"
            conditionGrade.text = it.getAirpRTInfo(AirpItem.PM10_WHO, blocal = true)

            // 배경색 바꾸기
            val strColor = it.getBackGroundColor(AirpItem.PM10_WHO)
            background_color.setBackgroundColor(Color.parseColor(strColor))


            pm10Value.text = it.getAirpRTInfo(AirpItem.미세먼지_농도) + "㎍/m³"
            pm10Grade.text = it.getAirpRTInfo(AirpItem.PM10_WHO, blocal = true)
            fineImg.setImageResource(it.getAirpGradeImgName(AirpItem.PM10_WHO))

            pm25Value.text = it.getAirpRTInfo(AirpItem.초미세먼지_농도) + "㎍/m³"
            pm25Grade.text = it.getAirpRTInfo(AirpItem.PM25_WHO, blocal = true)
            microImg.setImageResource(it.getAirpGradeImgName(AirpItem.PM25_WHO))

            coValue.text = it.getAirpRTInfo(AirpItem.일산화탄소_농도) + "㎍/m³"
            coGrade.text = it.getAirpRTInfo(AirpItem.일산화탄소_지수, blocal = true)
            carbonImg.setImageResource(it.getAirpGradeImgName(AirpItem.일산화탄소_지수))

            o3Value.text = it.getAirpRTInfo(AirpItem.오존_농도) + "㎍/m³"
            o3Grade.text = it.getAirpRTInfo(AirpItem.오존_지수, blocal = true)
            ozoneImg.setImageResource(it.getAirpGradeImgName(AirpItem.오존_지수))

            no2Value.text = it.getAirpRTInfo(AirpItem.이산화질소_농도) + "㎍/m³"
            no2Grade.text = it.getAirpRTInfo(AirpItem.이산화질소_지수, blocal = true)
            nitrogenImg.setImageResource(it.getAirpGradeImgName(AirpItem.이산화질소_지수))

            so2Value.text = it.getAirpRTInfo(AirpItem.아황산가스_농도) + "㎍/m³"
            so2Grade.text = it.getAirpRTInfo(AirpItem.아황산가스_지수, blocal = true)
            sulfurousImg.setImageResource(it.getAirpGradeImgName(AirpItem.아황산가스_지수))

            Log.d("MiseLog", "현재 페이지 업데이트 됨")

        }
    }

    inner class ThreadClass : Thread() {
        override fun run() {
            super.run()
            while (isRunning) {
                SystemClock.sleep(500)

                mainActivity?.coreInfo?.disPlayData?.let{
                    if(it.isUpdate) {
                        handler?.sendEmptyMessage(0)
                        refCnt +=1

                        if(refCnt == 3) {
                            it.isUpdate = false
                            refCnt = 0
                        }
                    }
                }
            }
        }
    }

        inner class DisplayHandier : Handler() {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                //화면갱신 소스
                onRefreshScreen()
            }
        }
}
