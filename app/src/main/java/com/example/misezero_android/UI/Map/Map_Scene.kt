package com.example.misezero_android.UI.Map

import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.misezero_android.MainActivity
import com.example.misezero_android.R
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class Map_Scene : Fragment(), OnMapReadyCallback {

    private var activity: MainActivity? = null
    // 지도
    private lateinit var mView: MapView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map__scene, container, false)

        // 액티비티 참조
        activity = getActivity() as MainActivity?

        // 지도
        mView = view.findViewById(R.id.mapview) as MapView
        mView?.onCreate(savedInstanceState)
        mView?.getMapAsync(this)

        return view
    }

    override fun onStart() {
        super.onStart()
        mView?.onStart()
    }

    override fun onStop() {
        super.onStop()
        mView?.onStop()
    }

    override fun onPause() {
        super.onPause()
        mView?.onPause()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mView?.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mView?.onDestroy()
    }
    override fun onMapReady(googleMap: GoogleMap?) {
        // 시작시 보여지는 지점
        val myLocation = LatLng(36.858403, 127.907922)
        googleMap?.moveCamera(CameraUpdateFactory.newLatLng(myLocation))
        googleMap?.moveCamera(CameraUpdateFactory.zoomTo(6.5f))

        // Position the map.
        googleMap?.moveCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    36.858403,
                    127.907922
                ), 6.5f
            )
        )
    }
}