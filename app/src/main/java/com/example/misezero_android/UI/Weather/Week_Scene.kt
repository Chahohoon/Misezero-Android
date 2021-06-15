package com.example.misezero_android.UI.Weather

import android.content.Context
import android.graphics.Color
import android.os.*
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.misezero_android.*
import kotlinx.android.synthetic.main.fragment_current__scene.currentBtn
import kotlinx.android.synthetic.main.fragment_current__scene.todayBtn
import kotlinx.android.synthetic.main.fragment_current__scene.weekBtn
import kotlinx.android.synthetic.main.fragment_week__scene.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 *
 * 데이터연결작업 해야함
 */


class Week_Scene : Fragment() {

    private var mainActivity: MainActivity? = null
    var isRunning = false
    var handler : DisplayHandler? =null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_week__scene, container, false)
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

        //현재 위치로 돌아가는 버튼
        week_update.setOnClickListener {
            mainActivity?.coreInfo?.let {
                it.setDispInfo(-1)
                onRefreshScreen()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onResume() {
        super.onResume()

        this.isRunning = true
        handler = DisplayHandler()

        var thread = ThreadClass()
        thread.start()

        onRefreshScreen()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun onRefreshScreen() {

        var data : MiseDataClass = mainActivity?.coreInfo?.disPlayData ?: return

        weekAdd.text = data.getInfo(InfoItem.행정동명)

        //배경색 설정
        var setColor = data.getBackGroundColor(AirpItem.PM10_WHO)
        weekbg_color.setBackgroundColor((Color.parseColor(setColor)))


        //Week Page는 현재일 기준 저번주 , 다음주 디스플레이
        var day = listOf<Int>(7,1,2,3,4,5,6)
        var dayinWeek = LocalDateTime.now().dayOfWeek.value
        var offset = day.indexOf(dayinWeek) + 7
        var refdate = LocalDateTime.now().minusDays(offset.toLong())

        for(index in 0 until 21) {

            var date = refdate.plusDays(index.toLong()).format(DateTimeFormatter.ofPattern("dd"))
            setDateTime(index,date)

            date = refdate.plusDays(index.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-12"))
            var weatherImg = data.getWeatherGradeImgName(WeatherItem.기상상태, WeatherFrstType.중기, date)
            var tempMax = data.getWeatherMTInfo(WeatherItem.최고기온, date)
            var tempMin = data.getWeatherMTInfo(WeatherItem.최저기온, date)

            if (weatherImg == R.drawable.nodata){
                date = refdate.plusDays(index.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-00"))
                tempMax = data.getWeatherSTInfo(WeatherItem.최고기온, date)
                tempMin = data.getWeatherMTInfo(WeatherItem.최저기온, date)
                weatherImg = data.getWeatherGradeImgName(WeatherItem.기상상태, WeatherFrstType.중기, date)

                if (weatherImg == R.drawable.nodata){
                    date = refdate.plusDays(index.toLong()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd-06"))
                    tempMax = data.getWeatherSTInfo(WeatherItem.최고기온, date)
                    tempMin = data.getWeatherSTInfo(WeatherItem.최저기온, date)
                    weatherImg = data.getWeatherGradeImgName(WeatherItem.기상상태, WeatherFrstType.단기, date)
                }
            }

            if (tempMax != ""){
                tempMax += "°"
            }

            if (tempMin != "") {
                tempMin += "°"
            }

            if (weatherImg == R.drawable.nodata){
                weatherImg = R.drawable.empty
            }

            onMaxValue(index, tempMax)
            onMinValue(index, tempMin)
            setWeatherImg(index, weatherImg)
        }
        Log.d("MiseLog", "주간 페이지 업데이트 됨")
    }

    private fun setWeatherImg(index : Int, data : Int){
        when(index){
            0->{weahter00.setImageResource(data)}
            1->{weahter01.setImageResource(data)}
            2->{weahter02.setImageResource(data)}
            3->{weahter03.setImageResource(data)}
            4->{weahter04.setImageResource(data)}
            5->{weahter05.setImageResource(data)}
            6->{weahter06.setImageResource(data)}
            7->{weahter07.setImageResource(data)}
            8->{weahter08.setImageResource(data)}
            9->{weahter09.setImageResource(data)}
            10->{weather10.setImageResource(data)}
            11->{weather11.setImageResource(data)}
            12->{weather12.setImageResource(data)}
            13->{weather13.setImageResource(data)}
            14->{weather14.setImageResource(data)}
            15->{weather15.setImageResource(data)}
            16->{weather16.setImageResource(data)}
            17->{weather17.setImageResource(data)}
            18->{weather18.setImageResource(data)}
            19->{weather19.setImageResource(data)}
            20->{weather20.setImageResource(data)}
            else->{return}
        }
    }

    // 최저기온 보여주기
    private fun onMinValue(index : Int, data : String?){
        val value = data ?: ""
        when(index){
            0->{min00.text = value; if(value == ""){min00Label.text = ""}}
            1->{min01.text = value; if(value == ""){min01Label.text = ""}}
            2->{min02.text = value; if(value == ""){min02Label.text = ""}}
            3->{min03.text = value; if(value == ""){min03Label.text = ""}}
            4->{min04.text = value; if(value == ""){min04Label.text = ""}}
            5->{min05.text = value; if(value == ""){min05Label.text = ""}}
            6->{min06.text = value; if(value == ""){min06Label.text = ""}}
            7->{min07.text = value; if(value == ""){min07Label.text = ""}}
            8->{min08.text = value; if(value == ""){min08Label.text = ""}}
            9->{min09.text = value; if(value == ""){min09Label.text = ""}}
            10->{min10.text = value; if(value == ""){min10Label.text = ""}}
            11->{min11.text = value; if(value == ""){min11Label.text = ""}}
            12->{min12.text = value; if(value == ""){min12Label.text = ""}}
            13->{min13.text = value; if(value == ""){min13Label.text = ""}}
            14->{min14.text = value; if(value == ""){min14Label.text = ""}}
            15->{min15.text = value; if(value == ""){min15Label.text = ""}}
            16->{min16.text = value; if(value == ""){min16Label.text = ""}}
            17->{min17.text = value; if(value == ""){min17Label.text = ""}}
            18->{min18.text = value; if(value == ""){min18Label.text = ""}}
            19->{min19.text = value; if(value == ""){min19Label.text = ""}}
            20->{min20.text = value; if(value == ""){min20Label.text = ""}}
            else->{}
        }
    }

    // 최고기온 보여주기
    private fun onMaxValue(index : Int, data : String?){
        val value = data ?: ""
        when(index){
            0->{max00.text = value; if(value == ""){max00Label.text = ""}}
            1->{max01.text = value; if(value == ""){max01Label.text = ""}}
            2->{max02.text = value; if(value == ""){max02Label.text = ""}}
            3->{max03.text = value; if(value == ""){max03Label.text = ""}}
            4->{max04.text = value; if(value == ""){max04Label.text = ""}}
            5->{max05.text = value; if(value == ""){max05Label.text = ""}}
            6->{max06.text = value; if(value == ""){max06Label.text = ""}}
            7->{max07.text = value; if(value == ""){max07Label.text = ""}}
            8->{max08.text = value; if(value == ""){max08Label.text = ""}}
            9->{max09.text = value; if(value == ""){max09Label.text = ""}}
            10->{max10.text = value; if(value == ""){max10Label.text = ""}}
            11->{max11.text = value; if(value == ""){max11Label.text = ""}}
            12->{max12.text = value; if(value == ""){max12Label.text = ""}}
            13->{max13.text = value; if(value == ""){max13Label.text = ""}}
            14->{max14.text = value; if(value == ""){max14Label.text = ""}}
            15->{max15.text = value; if(value == ""){max15Label.text = ""}}
            16->{max16.text = value; if(value == ""){max16Label.text = ""}}
            17->{max17.text = value; if(value == ""){max17Label.text = ""}}
            18->{max18.text = value; if(value == ""){max18Label.text = ""}}
            19->{max19.text = value; if(value == ""){max19Label.text = ""}}
            20->{max20.text = value; if(value == ""){max20Label.text = ""}}
            else->{}
        }
    }

    private fun setDateTime(index : Int, strDay : String) {
        var value = "$strDay/"
        when(index) {
            0 -> {date00.text = value}
            1->{date01.text = value}
            2->{date02.text = value}
            3->{date03.text = value}
            4->{date04.text = value}
            5->{date05.text = value}
            6->{date06.text = value}
            7->{date07.text = value}
            8->{date08.text = value}
            9->{date09.text = value}
            10->{date10.text = value}
            11->{date11.text = value}
            12->{date12.text = value}
            13->{date13.text = value}
            14->{date14.text = value}
            15->{date15.text = value}
            16->{date16.text = value}
            17->{date17.text = value}
            18->{date18.text = value}
            19->{date19.text = value}
            20->{date20.text = value}
            else->{}
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