package com.example.misezero_android

import android.Manifest
import android.content.pm.PackageManager
import android.os.*
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.misezero_android.UI.Map.Map_Scene
import com.example.misezero_android.UI.QuoteList.QuoteList_Scene
import com.example.misezero_android.UI.Setting.Help_Scene
import com.example.misezero_android.UI.Setting.Home_Scene
import com.example.misezero_android.UI.Weather.Current_Scene
import com.example.misezero_android.UI.Weather.OnSwipeTouchListener
import com.example.misezero_android.UI.Weather.Today_Scene
import com.example.misezero_android.UI.Weather.Week_Scene
import com.google.android.gms.location.*
import io.realm.Realm
import io.realm.RealmConfiguration
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity() {
    // 코어 클래스 (앱의 모든 서비스 처리)
    var coreInfo = MiseCoreClass()

    var isRunning = false
    var handler : DisplayHandler? = null

    // 관심지역 데이터 베이스
    var realm : Realm? = null

    // 권한 및 위치 설정
    private val permissionUitl = Permission_Uitl()
    private lateinit var locationRequest: LocationRequest
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // 권한설정 
        permissionUitl.CheckPermission(this)
        
        //메인 클래스 시작
        coreInfo.onInitialize()
        setMainMenuSet()
        createSwipeEvent()
        locationInit()
//        onInitDataBase()
    }

    override fun onResume() {
        super.onResume()
        addLocationListener()

//        backGroundLayout.visibility = View.VISIBLE
        // 삭제
        this.isRunning = true
        handler = DisplayHandler()
        var thread = ThreadClass()
        thread.start()

    }

    //관심지역 데이터 저장할 용도
    fun onInitDataBase() {
        Realm.init(this)
        var config = RealmConfiguration.Builder().name("myrealm.realm").build()
        Realm.setDefaultConfiguration(config)

        realm = Realm.getDefaultInstance()
        getUserData()
    }

    fun getUserData() {
        realm?.executeTransaction {
            var datas = realm?.where(UserDataClass::class.java)?.findAll()

            datas?.let {
                coreInfo.userInfoData.removeAll(coreInfo.userInfoData.asSequence())

                for( i in it) {
                    coreInfo.userInfoData.add(MiseDataClass())
                    coreInfo.userInfoData.last()?.let {
                        it.onUpdate(i.isLongitude().toDouble(), i.islatiude().toDouble(), i.isName())
                    }
                    Log.d("MiseLog22",i.toString())
                }
            }
        }
    }

    ///두번째 위치 설정정
   private fun locationInit() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationCallback = MyLocationCallBack()
        locationRequest = LocationRequest()
        locationRequest.let{
            it.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            it.interval = 10000
            it.fastestInterval = 1000
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 현재 위치 정보가 업데이트 되었을 경우 처리되는 함수
    //////////////////////////////////////////////////////////////////////////////////////
    inner class MyLocationCallBack : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult?) {
            val location = locationResult?.lastLocation

            location?.let {location->
                coreInfo.curInfoData.let{
                    it.onUpdate(location.latitude, location.longitude)
                }
            }
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////
    // 위치 설정 리스너 등록
    //////////////////////////////////////////////////////////////////////////////////////
    private fun addLocationListener() {
        // 권한 체크
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            // 권한이 없을 경우 처리 루틴.
            Log.d("MiseLog", "위치 권한없음")
            return
        }

        // 위치 업데이트 되었을 경우, 실제 처리될 콜백함수를 등록한다.
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }


    //플로팅액션버튼 이벤트
    private fun setMainMenuSet() {
            fab1?.setOnClickListener {
                onRefreshFragment(Current_Scene())
                fam.close(true)}

            fab2?.setOnClickListener {
                onRefreshFragment(QuoteList_Scene())
                fam.close(true)
            }

            fab3?.setOnClickListener {
                onRefreshFragment(Map_Scene())
                fam.close(true)
            }

            fab4?.setOnClickListener {
                onRefreshFragment(Home_Scene())
                fam.close(true)
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////
    // 스와이프 이벤트 등록
    //////////////////////////////////////////////////////////////////////////////////////
    private fun createSwipeEvent(){
        screen_view.setOnTouchListener(object : OnSwipeTouchListener(baseContext){

            // 왼쪽.
            override fun onSwipeLeft() {

                // 현재 보여지는 프래그먼트를 확인하여 다음 실행을 구현.
                for(fragment: Fragment in supportFragmentManager.fragments){
                    if (fragment.isVisible) {
                        when(fragment){
                            is Current_Scene->{onRefreshFragment(Today_Scene())}
                            is Today_Scene->{onRefreshFragment(Week_Scene())}
                            is Week_Scene->{onRefreshFragment(Current_Scene())}
                        }
                    }
                }
            }

            // 오른쪽.
            override fun onSwipeRight() {

                // 현재 보여지는 프래그먼트를 확인하여 다음 실행을 구현.
                for(fragment: Fragment in supportFragmentManager.fragments){
                    if (fragment.isVisible){
                        when(fragment){
                            is Current_Scene->{onRefreshFragment(Week_Scene())}
                            is Today_Scene->{onRefreshFragment(Current_Scene())}
                            is Week_Scene->{onRefreshFragment(Today_Scene())}
                        }
                    }
                }
            }

            // 위.
            override fun onSwipeTop() {
                Toast.makeText(baseContext,"위로",Toast.LENGTH_SHORT).show()
            }

            // 아래.
            override fun onSwipeBottom() {
                Toast.makeText(baseContext,"아래로",Toast.LENGTH_SHORT).show()
            }
        })
    }

    /// 퍼미션 권한 결과
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if(requestCode == 2) {
            if(permissionUitl.ResultPermission(grantResults)) {
                //승인
            } else {
                Toast.makeText(this,"권한을 허용하지 않으시면 프로그램 사용이 제한됩니다.",Toast.LENGTH_LONG).show()
                finish()
            }
        }
    }

     fun onRefreshFragment(scene: Any) {
        supportFragmentManager.beginTransaction().let {
            supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            it.addToBackStack(null)
            when (scene) {
                is Current_Scene-> { it.replace(R.id.main_view,scene).commit()}
                is Today_Scene-> { it.replace(R.id.main_view,scene).commit()}
                is Week_Scene -> { it.replace(R.id.main_view,scene).commit()}
                is QuoteList_Scene -> { it.replace(R.id.main_view,scene).commit() }
                is Map_Scene -> { it.replace(R.id.main_view, scene).commit() }
                is Home_Scene-> { it.replace(R.id.main_view,scene).commit()}
                is Help_Scene-> { it.replace(R.id.main_view,scene).commit()}
                else -> {}
            }
        }
    }

    /////////////////////////////////////////////////////////////
    //// 0.5초에 한번씩 확면을 갱신하여 준다.
    //////////////////////////////////////////////////////////////
    inner class ThreadClass : Thread(){
        @RequiresApi(Build.VERSION_CODES.O)
        override fun run() {
            while (isRunning){
                SystemClock.sleep(500)

                // 데이터 업데이트 상태이면 화면을 갱신한다.
                if (coreInfo.disPlayData!!.iscomplete){
                    handler?.sendEmptyMessage(0)
                }
            }
        }
    }
    /////////////////////////////////////////////////////////////
    //// 화면 갱신 해들러
    //////////////////////////////////////////////////////////////

    inner class DisplayHandler : Handler(){
        @RequiresApi(Build.VERSION_CODES.O)
        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
//            backGroundLayout.visibility = View.GONE
            isRunning = false
        }
    }
}