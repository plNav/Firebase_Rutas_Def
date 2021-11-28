package com.pablolop.firebase

import com.google.android.gms.maps.model.LatLng

var routeId : Long = 0L
var routeList : MutableList<RouteData> = mutableListOf()
var currentRoute : MutableMap<Long,LatLng> = mutableMapOf()
var currentEmail : String = ""
var routesRecycler : MutableList<Long> = mutableListOf()
var routeListRecycler : MutableList<RouteData> = mutableListOf()
var dialogMapResult : String = "Cancelar"
var dialogName : String = ""

var routesCommunity : MutableList<RouteData> = mutableListOf()
var routesCommunityRecycler : MutableList<RouteData> = mutableListOf()
var routesCount : MutableList<Long> = mutableListOf()

var registerName : String  = ""
/*var registerRouteId : Long = 0L
var registerPoints : MutableList<Long> = mutableListOf()
var registerLatitude : MutableList<Double> = mutableListOf()
var registerLongitude : MutableList<Double> = mutableListOf()*/


