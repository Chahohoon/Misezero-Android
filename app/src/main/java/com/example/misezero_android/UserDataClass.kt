package com.example.misezero_android

import android.app.Application
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class UserDataClass : RealmObject() {
        private var name : String? = null
        private var latitude : String? = null
        private var longitude : String? = null

        fun setData(name : String, latitude : String, longitude : String ) {
                this.name = name
                this.latitude = latitude
                this.longitude = longitude
        }

        fun isName() : String {
                return this.name ?: ""
        }

        fun islatiude() : String {
                return this.latitude ?: ""
        }

        fun isLongitude() : String {
                return this.longitude ?: ""
        }
}