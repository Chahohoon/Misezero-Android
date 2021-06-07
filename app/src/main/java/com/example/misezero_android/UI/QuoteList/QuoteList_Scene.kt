 package com.example.misezero_android.UI.QuoteList

import android.content.Context
import android.content.Intent
import android.os.*
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.example.misezero_android.*
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.fragment_quote_list__scene.*
import kotlinx.android.synthetic.main.quotelist_item.view.*
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

 class QuoteList_Scene : Fragment() {
    var mainActivity: MainActivity? = null
    var isRunning = false
    var handler: DisplayHandler? = null
    var adapter = QuoteAdapter()

    var backKeyPressedTime: Long = 0
    var toast: Toast? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        mainActivity = activity as MainActivity
    }

    //액티비티 ui작업은 onCreate 프래그먼트는 onCreateView
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_quote_list__scene, container, false)
    }

    //액티비티와 연결되는 시점
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        addBtn.setOnClickListener {
            val intent = Intent(activity,Search_Scene::class.java)
            startActivityForResult(intent, mainActivity!!.REQUEST_CODE)
        }
    }

    override fun onPause() {
        super.onPause()
        this.isRunning = false
        this.handler = null
    }

    override fun onResume() {
        super.onResume()
        this.isRunning = true
        handler = DisplayHandler()
        var thread = ThreadClass()
        thread.start()
        onRefreshScreen()
    }

    fun onRefreshScreen() {
        mainActivity?.coreInfo?.let{
            adapter.replaceItems(it.userInfoData)
            cityList.adapter = adapter
        }
    }

    // 스레드
    inner class ThreadClass : Thread() {
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            while (isRunning) {
                SystemClock.sleep(100)

                mainActivity?.coreInfo?.userInfoData?.let {
                    for (i in it) {
                        if (i.isUpdate) {
                            handler?.sendEmptyMessage(0)
                            i.isUpdate = false
                        }
                    }
                }
            }
        }
    }

    // 핸들러
    inner class DisplayHandler : Handler(){
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            onRefreshScreen()

        }
    }
}

//////////////////////////////////////////////////////////////////////////////////
 //리사이클러뷰 아답터
///////////////////////////////////////////////////////////////////////////////
class QuoteAdapter : RecyclerView.Adapter<QuoteAdapter.ViewHolder>(),itemTouchHelper {

    ////////////////////////////////////////////////////////////////////////////////////////////////
    // 데이터를 저장할 아이템 리스트
    private var items = mutableListOf<MiseDataClass>() // 변하기 쉬운 MutableList로 해줘야함 하지만 RealmResults랑 충돌을 일으키나,,

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.quotelist_item,parent,false)
        return ViewHolder(view)
    }
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items.get(position)

        holder.itemView.cityName.text = item.getInfo(InfoItem.이름)
        holder.itemView.cityAddress.text = item.getInfo(InfoItem.행정동명)
        holder.itemView.cityTemp.text = item.getWeatherRTInfo(WeatherItem.기온) + " ℃"
        holder.itemView.cityHumid.text = item.getWeatherRTInfo(WeatherItem.습도) + " %"

        var date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd-HH"))

        holder.itemView.weatherText.text = item.getWeatherSSTInfo(WeatherItem.기상상태,date,true)
        holder.itemView.pm10Text.text = item.getAirpRTInfo(AirpItem.PM10_WHO,true)
        holder.itemView.listWeatherImg.setImageResource(item.getWeatherGradeImgName(WeatherItem.기상상태, WeatherFrstType.초단기, date))
        holder.itemView.listAirImg.setImageResource(item.getAirpGradeImgName(AirpItem.PM10_WHO))

        holder.itemView.setOnClickListener{
            itemClickListner.onClick(it, position)
        }
    }

    override fun getItemCount(): Int = items.size

    override fun onItemMove(from: Int, to: Int) {
        TODO("Not yet implemented")
    }

    override fun onItemSwipe(position: Int) {

    }

    override fun onItemClick(position: Int) {
        //TODO("Not yet implemented")
    }

    ///////////////////////////////////////////////////////////////////////////////////////////
    // 클릭 이벤트 인터페이스 정의
    interface ItemClickListener {
        fun onClick(view: View, position: Int)
    }
    // 클릭 리스너 선언
    private lateinit var itemClickListner: ItemClickListener
    // 클릭 리스너 등록 메소드
    fun setItemClickListener(itemClickListener: ItemClickListener) {
        this.itemClickListner = itemClickListener
    }

    inner class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer

    fun replaceItems(items : MutableList<MiseDataClass>) {
        this.items = items
        notifyDataSetChanged()
    }
}