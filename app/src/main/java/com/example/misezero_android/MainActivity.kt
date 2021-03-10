package com.example.misezero_android

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentManager
import com.example.misezero_android.UI.Map.Map_Scene
import com.example.misezero_android.UI.QuoteList.QuoteList_Scene
import com.example.misezero_android.UI.Setting.Help_Scene
import com.example.misezero_android.UI.Setting.Home_Scene
import com.example.misezero_android.UI.Weather.Current_Scene
import com.example.misezero_android.UI.Weather.Today_Scene
import com.example.misezero_android.UI.Weather.Week_Scene
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),View.OnClickListener {
    var coreInfo = MiseCoreClass()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
//        coreInfo.onInitialize()
    }

    private fun onRefreshFragment(scene: Any) {
        supportFragmentManager.beginTransaction().let {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            it.addToBackStack(null)
            when (scene) {
                is Current_Scene-> { it.replace(R.id.main_view,scene)}
                is Today_Scene-> { it.replace(R.id.main_view,scene)}
                is Week_Scene -> { it.replace(R.id.main_view,scene)}
                is QuoteList_Scene -> { it.replace(R.id.main_view,scene).commit() }
                is Map_Scene -> { it.replace(R.id.main_view, scene).commit() }
                is Home_Scene-> { it.replace(R.id.main_view,scene)}
                is Help_Scene-> { it.replace(R.id.main_view,scene)}
                else -> {}
            }
        }
    }

    override fun onClick(v: View?) {
        when(v?.id) {
            R.id.fab1-> {
                onRefreshFragment(Current_Scene())
                fam.close(true)}

            R.id.fab2-> {
                onRefreshFragment(QuoteList_Scene())
                fam.close(true)
            }

            R.id.fab3-> {
                onRefreshFragment(Map_Scene())
                fam.close(true)
            }

            R.id.fab4-> {
                onRefreshFragment(Home_Scene())
                fam.close(true)
            }
        }
    }
}