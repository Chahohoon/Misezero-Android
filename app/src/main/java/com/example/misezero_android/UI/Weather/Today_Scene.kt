package com.example.misezero_android.UI.Weather

import android.content.Context
import android.graphics.Color
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.misezero_android.*
import kotlinx.android.synthetic.main.fragment_current__scene.btn_update
import kotlinx.android.synthetic.main.fragment_current__scene.currentBtn
import kotlinx.android.synthetic.main.fragment_current__scene.todayBtn
import kotlinx.android.synthetic.main.fragment_current__scene.weekBtn
import kotlinx.android.synthetic.main.fragment_today__scene.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * 데이터연결작업 해야함
 */

class Today_Scene : Fragment() {

    private var mainActivity: MainActivity? = null
    var handler: DisplayHandler? = null
    var isRunning = false
    var dateOffset : Long = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_today__scene, container, false)
    }

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

        // 오늘, 내일, 모레 세부 디스플레이
        afterdays0.setOnClickListener {
            dateOffset = 0
            onAfterDaysBtnDisplay()
            //android:background="@drawable/btn_bg_line"
        }

        afterdays1.setOnClickListener {
            dateOffset = 1
            onAfterDaysBtnDisplay()

        }

        afterdays2.setOnClickListener {
            dateOffset = 2
            onAfterDaysBtnDisplay()
        }

    }

    // 오늘, 내일, 모레
    // 버튼이 눌러진 부분을 제외한 영역을 투명하게 전환시킴.
    private fun onAfterDaysBtnDisplay() {
        dateOffset.let {
            when(it.toInt()){
                0 -> {
                    afterdays0?.let{btn->
                        btn.setBackgroundResource(R.drawable.btn_bg_clicked)
                    }
                    afterdays1?.let{btn->
                        btn.setBackgroundResource(R.drawable.btn_bg_line)
                    }
                    afterdays2?.let{btn->
                        btn.setBackgroundResource(R.drawable.btn_bg_line)
                    }
                }
                1 -> {
                    afterdays0?.let{btn->
                        btn.setBackgroundResource(R.drawable.btn_bg_line)
                    }
                    afterdays1?.let{btn->
                        btn.setBackgroundResource(R.drawable.btn_bg_clicked)
                    }
                    afterdays2?.let{btn->
                        btn.setBackgroundResource(R.drawable.btn_bg_line)
                    }
                }
                2 -> {
                    afterdays0?.let{btn->
                        btn.setBackgroundResource(R.drawable.btn_bg_line)
                    }
                    afterdays1?.let{btn->
                        btn.setBackgroundResource(R.drawable.btn_bg_line)
                    }
                    afterdays2?.let{btn->
                        btn.setBackgroundResource(R.drawable.btn_bg_clicked)
                    }
                }
                else->{

                }
            }
        }

    }

    //화면이 표시되고 난 후
    //초기화 작업
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        //버튼 이벤트 활성화
        setListenerEnable(true)

        isRunning = true
        handler = DisplayHandler()

        var thread = ThreadClass()
        thread.start()

        onRefreshScreen()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()

        // 버튼 이벤트 비활성화
        setListenerEnable(false)

        // 핸들러 제거
        this.isRunning = false
        this.handler = null
    }

    // 버튼 이벤트 리스너 세팅
    @RequiresApi(Build.VERSION_CODES.O)
    private fun setListenerEnable(value : Boolean){
        currentBtn.isEnabled = value
        todayBtn.isEnabled = value
        weekBtn.isEnabled = value

        afterdays0.isEnabled = value
        afterdays1.isEnabled = value
        afterdays2.isEnabled = value


        if (!btn_update.hasOnClickListeners() && btn_update.isEnabled){
            btn_update.setOnClickListener {
                onRefreshScreen()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onRefreshScreen(red : Int = 0) {
        mainActivity?.coreInfo?.disPlayData?.let {
            textView3.text = it.getInfo(InfoItem.행정동명)


            //하단 디스플레이
            var date = LocalDateTime.now().plusDays(dateOffset).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            pm10Grade.text = it.getAirpFRInfo(AirpItem.미세먼지_지수, date, blocal = true)
            pm10Gradeimg.setImageResource(it.getAirpGradeImgName(AirpItem.미세먼지_지수, date))

            pm25Grade.text = it.getAirpFRInfo(AirpItem.초미세먼지_지수, date, blocal = true)
            pm25Gradeimg.setImageResource(it.getAirpGradeImgName(AirpItem.초미세먼지_지수, date))

            o3Grade.text = it.getAirpFRInfo(AirpItem.오존_지수, date, blocal = true)
            o3Gradeimg.setImageResource(it.getAirpGradeImgName(AirpItem.오존_지수, date))

            // 배경색 바꾸기
            val strColor = it.getBackGroundColor(AirpItem.PM10_WHO)
            todaybg_color.setBackgroundColor(Color.parseColor(strColor))

            //날씨

            for (i in 0 until 8)  {        // 0, 3, 6, 9, 12, 15, 18,21
                 var refdate = date + "-" + String.format("%02d", i*3) //날짜 뒤에 시간

                var weatherImg = it.getWeatherGradeImgName(WeatherItem.기상상태, WeatherFrstType.단기, refdate)
                var temp = it.getWeatherSTInfo(WeatherItem.기온, refdate)
                var rain = it.getWeatherSTInfo(WeatherItem.강수확률, refdate)
                var hum = it.getWeatherSTInfo(WeatherItem.습도, refdate)
                var wind = it.getWeatherSTInfo(WeatherItem.풍속, refdate)
                var weathertext = it.getWeatherSTInfo(WeatherItem.기상상태, refdate, blocal = true)
                updateIcon(index = i, imgName = weatherImg, temp = temp, rain = rain, hum = hum, wind = wind, weather = weathertext)
            }
        }
    }

    fun updateIcon(index : Int, imgName : Int, temp : String, rain: String, hum: String, wind: String, weather : String){
        when(index){
            0 ->{
                wimg_0.setImageResource(imgName)
                temp_0.text = temp
                rain_0.text = rain
                humid_0.text = hum
                wind_0.text = wind
                weather_0.text = weather
            }
            1 ->{
                wimg_3.setImageResource(imgName)
                temp_3.text = temp
                rain_3.text = rain
                humid_3.text = hum
                wind_3.text = wind
                weather_3.text = weather
            }
            2 ->{
                wimg_6.setImageResource(imgName)
                temp_6.text = temp
                rain_6.text = rain
                humid_6.text = hum
                wind_6.text = wind
                weather_6.text = weather
            }
            3 ->{
                wimg_9.setImageResource(imgName)
                temp_9.text = temp
                rain_9.text = rain
                humid_9.text = hum
                wind_9.text = wind
                weather_9.text = weather
            }
            4 ->{
                wimg_12.setImageResource(imgName)
                temp_12.text = temp
                rain_12.text = rain
                humid_12.text = hum
                wind_12.text = wind
                weather_12.text = weather
            }
            5 ->{
                wimg_15.setImageResource(imgName)
                temp_15.text = temp
                rain_15.text = rain
                humid_15.text = hum
                wind_15.text = wind
                weather_15.text = weather
            }
            6 ->{
                wimg_18.setImageResource(imgName)
                temp_18.text = temp
                rain_18.text = rain
                humid_18.text = hum
                wind_18.text = wind
                weather_18.text = weather
            }
            7 ->{
                wimg_21.setImageResource(imgName)
                temp_21.text = temp
                rain_21.text = rain
                humid_21.text = hum
                wind_21.text = wind
                weather_21.text = weather
            }
            else->{return}
        }
    }



    inner class ThreadClass : Thread() {
        override fun run() {
            SystemClock.sleep(500)

            mainActivity?.coreInfo?.disPlayData?.let {
                if(it.isUpdate) {
                    handler?.sendEmptyMessage(0)
                    it.isUpdate = false
                }
            }
        }
    }

    inner class DisplayHandler : Handler() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            onRefreshScreen()
        }
    }
}