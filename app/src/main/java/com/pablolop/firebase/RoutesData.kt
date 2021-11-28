package com.pablolop.firebase

import com.google.android.gms.maps.model.LatLng
import java.io.Serializable

data class RouteData (
    var name: String,
    val email: String,
    val id: Long,
    val pointLatLng: MutableMap<Long, LatLng>,
    var author : String = ""
)
//images?
