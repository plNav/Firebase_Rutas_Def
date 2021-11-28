package com.pablolop.firebase

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pablolop.firebase.databinding.ActivityMapsBinding
import kotlin.collections.HashMap


open class MapsActivity :
    AppCompatActivity(),
    OnMapReadyCallback,
    GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityMapsBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val dataBase = Firebase.firestore
    private lateinit var timer: CountDownTimer
    private var pointId: Long = 1L

    companion object{
        const val REQUEST_CODE_LOCATION = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        val user = auth.currentUser
        user?.let {
            currentEmail = user.email.toString()
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        binding.btnStopMap.setOnClickListener {

            var dialog = DialogFragmentMapName()
            dialog.show(supportFragmentManager, "dialog_map_name_fragment")

            when (dialogMapResult) {
                "Descartar" -> onBackPressed()
                "Cancelar" -> {}
                else -> {
                    addToRoutes(registerName)
                    setLatLngFirestore()
                    onBackPressed()
                }
            }

        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        timer.cancel()
    }

    override fun onStart() {
        super.onStart()
        dialogMapResult = "Cancelar"
        dialogName = ""
        countDown()
    }

    private fun countDown() {

        timer = object : CountDownTimer(Long.MAX_VALUE, 180000) { // cada 3min

            override fun onTick(millisUntilFinished: Long) {
                onMapReady(mMap)
                //Problemas al guardar el nombre derivados de asincronia
                setLatLngFirestore()
                Log.w("CONTADOR", "$millisUntilFinished")
            }

            override fun onFinish() {
                timer.start()
            }

        }.start()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        mMap.mapType = GoogleMap.MAP_TYPE_HYBRID
        enableLocation()
        var latitud: Double
        var longitud: Double
        dataBase.collection(currentEmail)
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    var id = document.data["routeId"] as Long
                    if (id != null) {
                        if (id == routeId) {
                            latitud = document.data["latitud"] as Double
                            longitud = document.data["longitud"] as Double
                            mMap.addMarker(MarkerOptions().position(LatLng(latitud, longitud)))
                            mMap.animateCamera(CameraUpdateFactory.
                            newLatLngZoom(LatLng(latitud, longitud),
                                15F), 2000, null)
                        }
                    }
                }
            }
    }


    @SuppressLint("MissingPermission")
    private fun setLatLngFirestore() {

        enableLocation()
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {

                Log.e("\tLatitud =>", location.latitude.toString())
                Log.e("\tLongitud =>", location.longitude.toString())
                Log.e("\tID Ruta => ", "$routeId")
                Log.e("\tPunto de Ruta => ", "$pointId")

                /*registerRouteId = routeId
                registerPoints[pointId.toInt()] = pointId
                registerLatitude[pointId.toInt()] = location.latitude
                registerLongitude[pointId.toInt()] = location.longitude
                currentRoute[pointId] = LatLng(location.latitude, location.longitude)
                pointId++
                dialogName = ""*/

                val latLng: HashMap<String, Any> = hashMapOf(
                    "routeName" to registerName,
                    "routeId" to routeId,
                    "pointId" to pointId,
                    "latitud" to location.latitude,
                    "longitud" to location.longitude
                )

                dataBase.collection(currentEmail).add(latLng)
                    .addOnSuccessListener { documentReference ->
                        Log.e("Information Subida", "ID: ${documentReference.id}")
                        Log.e("Saved currentRoute =>", "$currentRoute")
                        currentRoute[pointId] = LatLng(location.latitude, location.longitude)
                        pointId++
                        dialogName = ""

                    }
                    .addOnFailureListener { e ->
                        Log.e("Error subir datos", e.toString())
                    }
            }
        }

    }


    private fun addToRoutes(name: String = "Unnamed Route") {

        var routeObject = RouteData(name, currentEmail, routeId, currentRoute)
        Log.w("\tObjectSaved", "$routeObject")
        routeList.add(routeObject)
        currentRoute.clear()

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
                REQUEST_CODE_LOCATION)
        }
    }

    @SuppressLint("MissingSuperCall", "MissingPermission")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            REQUEST_CODE_LOCATION -> if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                mMap.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Ve a ajustes y acepta los permisos de ubicacion", Toast.LENGTH_SHORT).show()

            }
            else -> {}
        }
    }

    @SuppressLint("MissingPermission")
    override fun onResumeFragments() {
        super.onResumeFragments()
        if(!::mMap.isInitialized) return
        if(!isLocationPermissionGranted()){
            mMap.isMyLocationEnabled = false
            Toast.makeText(this, "Ve a ajustes y acepta los permisos de ubicacion", Toast.LENGTH_SHORT).show()
        }
    }




}




