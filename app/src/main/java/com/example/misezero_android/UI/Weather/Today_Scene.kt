package com.example.misezero_android.UI.Weather

import android.content.Context
import android.os.*
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import com.example.misezero_android.InfoItem
import com.example.misezero_android.MainActivity
import com.example.misezero_android.R
import kotlinx.android.synthetic.main.fragment_current__scene.*
import kotlinx.android.synthetic.main.fragment_current__scene.currentBtn
import kotlinx.android.synthetic.main.fragment_current__scene.todayBtn
import kotlinx.android.synthetic.main.fragment_current__scene.weekBtn
import kotlinx.android.synthetic.main.fragment_today__scene.*
import kotlinx.android.synthetic.main.fragment_current__scene.btn_update


class Today_Scene : Fragment() {

    private var mainActivity: MainActivity? = null
    var handler: DisplayHandler? = null
    var isRunning = false

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
    }

    //화면이 표시되고 난 후
    //초기화 작업
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

    private fun onRefreshScreen(red : Int = 0) {
        mainActivity?.coreInfo?.disPlayData?.let {
            textView3.text = it.getInfo(InfoItem.행정동명)


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
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            onRefreshScreen()
        }
    }
}