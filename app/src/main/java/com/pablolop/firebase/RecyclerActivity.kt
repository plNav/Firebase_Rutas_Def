package com.pablolop.firebase

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.pablolop.firebase.databinding.ActivityRecyclerBinding

class RecyclerActivity : AppCompatActivity(),RecyclerAdapter.OnMapClick {
    private lateinit var binding: ActivityRecyclerBinding
    private val dataBase = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        for (route in routeList){
            var idroute = route.id

            if(routesRecycler.contains(idroute)) {

                var pointId = route.pointLatLng

                for(r in routeListRecycler){
                    if(r.id == idroute){
                        r.pointLatLng.putAll(pointId)
                    }
                }

            }else{
                routesRecycler.add(idroute)
                routeListRecycler.add(route)

            }
        }

        //TEST
    /*    for (test in routeListRecycler){
            Log.e("\t00001", "NUEVO HASHMAP")
            for ((key,value) in test.pointLatLng) {
                Log.e("\t\tVALORES", "${key} -> ${value}")
            }

        }*/


        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.addItemDecoration(DividerItemDecoration(this,DividerItemDecoration.VERTICAL))
        binding.recyclerView.adapter = RecyclerAdapter(this, routeListRecycler, this)
    }

    override fun onClick(id: Long, pointLatLng: MutableMap<Long, LatLng>) {
        Toast.makeText(this, "Clicado en ${id}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this,MapsActivityRecycler::class.java)
        intent.putExtra("Ruta", id)
        intent.putExtra("Community", false)
        startActivity(intent)
    }

    override fun butClick(id: Long, email: String) {

        val intent = Intent(this,OptionsActivity::class.java)
        intent.putExtra("Ruta", id)
        startActivity(intent)
    }
}