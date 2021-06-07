package com.example.misezero_android.UI.Weather

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.example.misezero_android.MainActivity
import com.example.misezero_android.R
import kotlinx.android.synthetic.main.fragment_current__scene.*
/**
 *
 * 데이터연결작업 해야함
 */
// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Week_Scene.newInstance] factory method to
 * create an instance of this fragment.
 */
class Week_Scene : Fragment() {

    private var mainActivity: MainActivity? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        mainActivity = activity as MainActivity
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
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
    }
}