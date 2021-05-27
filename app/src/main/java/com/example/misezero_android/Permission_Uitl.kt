package com.example.misezero_android

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat

class Permission_Uitl {
    private val REQ_CODE = 2 // 몇개의 권한을 띄울건지
    val PEMISSION_ARRAY = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    @TargetApi(Build.VERSION_CODES.M)
    fun CheckPermission(activity: Activity) {
        val PermissionCheck = ActivityCompat.checkSelfPermission(
            activity,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) //권한이 있는지 없는지 반환하는 메소드
        val PermissionCheck2 =
            ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION)

        //권한체크    없으면,,
        if (PermissionCheck != PackageManager.PERMISSION_GRANTED || PermissionCheck2 != PackageManager.PERMISSION_DENIED) { //GRAMTED = 권한있음 DENIED = 권한 없음
            ActivityCompat.requestPermissions(activity, PEMISSION_ARRAY, REQ_CODE) //권한설정
            Log.d(activity.javaClass.name, "권한설정")
        } else {
            Toast.makeText(activity, "권한있음", Toast.LENGTH_SHORT).show()
        }
    }


    ///////////////////////////////////////
    //눌렀는지 확인
    ///////////////////////////////////////
    fun ResultPermission(grantresults: IntArray): Boolean {
        if (grantresults.size < 2) { // 2개미만이면 아무권한이 없음
            return false
        }
        for (result in grantresults) {
            if (result != PackageManager.PERMISSION_GRANTED) {
                return false
            }
        }
        return true // 모두허가함
    }
}