package com.example.misezero_android.UI.Map

import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.clustering.ClusterItem

class ClusterPosition : ClusterItem {
    val mPosition: LatLng
    var mTitle: String = "title"
    var mSnippet: String = "snippet"

//    constructor(lat : Double, lng : Double) {
//    mPosition = LatLng(lat, lng)
//    }

    constructor(
        lat: Double,
        lng: Double,
        title: String,
        snippet: String
    ) {
        mPosition = LatLng(lat, lng)
        mTitle = title
        mSnippet = snippet
    }

    override fun getPosition(): LatLng {
        return mPosition
    }

    override fun getTitle(): String {
        return mTitle
    }

    override fun getSnippet(): String {
        return mSnippet
    }

}