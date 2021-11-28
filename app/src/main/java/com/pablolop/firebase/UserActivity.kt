package com.pablolop.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pablolop.firebase.databinding.ActivityUserBinding

class UserActivity : AppCompatActivity() {
    private lateinit var binding : ActivityUserBinding
    private lateinit var auth : FirebaseAuth
    private val dataBase = Firebase.firestore
    private lateinit var email : String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = FirebaseAuth.getInstance()

        val user = auth.currentUser

        user?.let {
            email = user.email.toString()
            binding.userMail.setText(email)
        }

        binding.butSignOut.setOnClickListener {
            auth.signOut()
            routeId = 0
            currentEmail = ""
            routeListRecycler.clear()
            routeList.clear()
            routesRecycler.clear()
            currentRoute.clear()
            startActivity(Intent(baseContext, MainActivity::class.java))
        }
        binding.butVerRutas.setOnClickListener {
            loadAllRoutes()
        }
        binding.butVerRutasComunidad.setOnClickListener {
            loadAllRoutesCommunity()
        }

        binding.butMaps.setOnClickListener {
            dataBase.collection(email)
                .get()
                .addOnSuccessListener { result ->
                    var max : Long = 1
                    for (document in result){
                        if(document.data["routeId"] != null){
                            var num : Long = document.data["routeId"] as Long
                            Log.w("databasefun", "$num")
                            if (num >= max){
                                max = num
                                max++
                            }
                        }
                    }
                    routeId = max
                    startActivity(Intent(this,MapsActivity::class.java))
                }

            }
    }

    private fun loadAllRoutes(){

        routeList.clear()

        try{
            dataBase.collection(email)
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {

                        Log.e("\tLoad", document.toString())
                        Log.e("\tLoad", result.toString())

                        var name : String = document.data["routeName"] as String
                        var id : Long = document.data["routeId"] as Long
                        var pointId = document.data["pointId"] as Long
                        var longitud : Double = document.data["longitud"] as Double
                        var latitud : Double = document.data["latitud"] as Double
                        var coor = LatLng(latitud, longitud)
                        var map : MutableMap<Long, LatLng> = mutableMapOf(pointId to coor)

                        routeList.add(RouteData(name, email, id, map))
                        }

                    startActivity(Intent(baseContext,RecyclerActivity::class.java))
                }
                .addOnFailureListener { e ->
                    Log.e("Error obtener datos", e.toString())
                }
        }catch(err : Exception){
            Log.e("error", "Error al cargar las rutas de $currentEmail", err)

        }
    }

    private fun loadAllRoutesCommunity(){

        routeList.clear()

        try{
            dataBase.collection("Community")
                .get()
                .addOnSuccessListener { result ->
                    for (document in result) {

                        Log.e("\tLoad", document.toString())
                        Log.e("\tLoad", result.toString())

                        var name : String = document.data["routeName"] as String
                        var id : Long = document.data["routeId"] as Long
                        var email : String = document.data["routeEmail"] as String
                        var author : String = document.data["routeAuthor"] as String
                        var countMarkers : Long = document.data["countMarkers"] as Long

                        for(i in 1L..countMarkers){

                            var longitud : Double = document.data["$i - Longitude"] as Double
                            var latitud : Double = document.data["$i - Latitude"] as Double
                            var coor = LatLng(latitud, longitud)
                            var map : MutableMap<Long, LatLng> = mutableMapOf(i to coor)

                            routesCommunity.add(RouteData(name, email, id, map, author))
                            //Log.e("\t\tRouteData", "$routesCommunity")
                        }
                    }

                    startActivity(Intent(baseContext,RecyclerActivityCommunity::class.java))
                }
                .addOnFailureListener { e ->
                    Log.e("Error obtener datos", e.toString())
                }
        }catch(err : Exception){
            Log.e("error", "Error al cargar las rutas de la comunidad", err)

        }
    }
}