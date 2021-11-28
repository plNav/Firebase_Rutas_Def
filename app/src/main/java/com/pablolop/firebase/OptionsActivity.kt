package com.pablolop.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pablolop.firebase.databinding.ActivityOptionsBinding

class OptionsActivity : AppCompatActivity() {
    private lateinit var binding : ActivityOptionsBinding
    private val dataBase = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityOptionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val id = intent.extras?.getLong("Ruta")!!

        var routeToEdit : RouteData = findRoute(id)!!

        val mapNameEdit = binding.editTextNombreMapa
        mapNameEdit.setText(routeToEdit.name)

        val authorEdit = binding.editTextAutor
        authorEdit.setText(routeToEdit.email)

        val emailOption = binding.radioButEmail
        emailOption.isChecked = true
        if(emailOption.isChecked)authorEdit.isEnabled = false


        emailOption.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                authorEdit.setText(routeToEdit.email)
                authorEdit.isEnabled = false
            }
        }

        binding.radioButAlias.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                authorEdit.setText("")
                authorEdit.hint = "Alias"
                authorEdit.isEnabled = true
            }
        }

        binding.butCambiaSalir.setOnClickListener {
            routeToEdit.name = mapNameEdit.text.toString()
            routeToEdit.author = authorEdit.text.toString()

            // TODO actualizar name en firestore email donde el id sea el de la ruta
            Toast.makeText(this, "Sin implementar", Toast.LENGTH_SHORT).show()
        }

        binding.butCompartirSalir.setOnClickListener {
            if(binding.radioButAlias.isChecked && authorEdit.text.isNullOrEmpty()) authorEdit.setText("Alias")
            routeToEdit.name = mapNameEdit.text.toString()
            routeToEdit.author = authorEdit.text.toString()
            setFirestoreCommunity(routeToEdit)
        }
    }

    private fun findRoute(idTarget : Long) : RouteData {
        var map = mutableMapOf<Long, LatLng>(0L to LatLng(0.0,0.0))
        var selectedRoute : RouteData = RouteData("Defecto", "Email", 0L, map, "")
        for (route in routeListRecycler) if(route.id == idTarget) selectedRoute = route
        return selectedRoute
    }

    private fun setFirestoreCommunity(route : RouteData){

        var countMarkers = 0

        val map : MutableMap<String, Any> = mutableMapOf(
            "routeName" to route.name,
            "routeAuthor" to route.author,
            "routeEmail" to route.email,
            "routeId" to route.id
        )

        route.pointLatLng.forEach() { (key, value) ->
            map["$key - Latitude"] = value.latitude
            map["$key - Longitude"] = value.longitude
            countMarkers++
        }

        map["countMarkers"] = countMarkers

        dataBase.collection("Community").add(map)
            .addOnSuccessListener { documentReference ->
                Log.e("Ruta Subida", "ID: ${documentReference.id}")
                Toast.makeText(this, "Ruta subida", Toast.LENGTH_LONG).show()
                finish()
                startActivity(Intent(this, UserActivity::class.java))
            }
            .addOnFailureListener { err ->
                Log.e("Error Subida", err.toString())
                Toast.makeText(this, "Error de subida", Toast.LENGTH_LONG).show()
                finish()
            }
    }
}