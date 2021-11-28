package com.pablolop.firebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import com.pablolop.firebase.databinding.ActivityMapsRecyclerBinding
import android.graphics.Bitmap

import android.R

import android.graphics.drawable.BitmapDrawable
import com.google.android.gms.maps.model.BitmapDescriptorFactory

import com.google.android.gms.maps.model.BitmapDescriptor

import android.graphics.BitmapFactory







class MapsActivityRecycler :
    AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener{

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsRecyclerBinding
    private var id : Long? = 0L
    private var community : Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        id = intent.extras?.getLong("Ruta")
        community = intent.extras?.getBoolean("Community") == true
        //Toast.makeText(this, "$id", Toast.LENGTH_LONG).show()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(com.pablolop.firebase.R.id.map_recycler) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.btnStopMapRecycler.setOnClickListener { onBackPressed() }
    }


    override fun onMapReady(googleMap: GoogleMap) {


        mMap = googleMap
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID


        var routes = if(community) routesCommunityRecycler else routeListRecycler
        //Toast.makeText(this, "Bolean : $community \n $routes", Toast.LENGTH_LONG).show()
        Log.e("\t\t\tRutas $id", "$routes")
        enableLocation()

        for (route in routes){
            if(route.id == id){
                createPolylines(route.pointLatLng)
                route.pointLatLng.map {
                    if(it.key == 1L){
                        mMap.addMarker(MarkerOptions().position(it.value).title("1: START!!"))
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(it.value, 15F), 2000, null)
                    }else mMap.addMarker(MarkerOptions().position(it.value).title(it.key.toString()))
                }
            }
        }
    }

    private fun createPolylines(pointLatLng: MutableMap<Long, LatLng>) {

        val pointCount = pointLatLng.size
        if (pointCount <= 2) return

        val polylineOptions = PolylineOptions()

        var set : Set<Long> = pointLatLng.keys
        var list : List<Long> = ArrayList(set)
        var listSorted = list.sorted()

        var initEndPoint = pointLatLng[1]

        for (l in listSorted){
            polylineOptions.add(pointLatLng[l])
        }
        polylineOptions.add(initEndPoint)
            .width(20f)
            .color(ContextCompat.getColor(this, android.R.color.holo_purple))
        val polyline : Polyline = mMap.addPolyline(polylineOptions)
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(this, "MOVIENDO A POSICION ACTUAL\nPulsa en tu icono azul!", Toast.LENGTH_SHORT).show()
        return false
    }

    override fun onMyLocationClick(p0: Location) {
        Toast.makeText(this, "INFORMACION DE POSICION:" +
                "\nLatitud : ${p0.latitude}" +
                "\nLongitud : ${p0.longitude}" +
                "\nAltitud : ${p0.altitude}" +
                "\nVelocidad : ${p0.speed}",
            Toast.LENGTH_LONG).show()
    }

    //FUNCIONES DE PERMISOS

    private fun isLocationPermissionGranted() =
        ContextCompat.checkSelfPermission(
            this, Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

    @SuppressLint("MissingPermission")
    private fun enableLocation(){
        if(!::mMap.isInitialized) return
        if(isLocationPermissionGranted()){
            mMap.isMyLocationEnabled = true
        }else{
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Ve a ajustes y acepta los permisos de ubicacion", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                MapsActivity.REQUEST_CODE_LOCATION
            )
        }
    }

    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            MapsActivity.REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mMap.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Ve a ajustes y acepta los permisos de ubicacion", Toast.LENGTH_SHORT).show()

            }
            else -> {}
        }
    }



}