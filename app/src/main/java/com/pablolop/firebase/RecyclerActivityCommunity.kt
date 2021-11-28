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
import com.pablolop.firebase.databinding.ActivityRecyclerCommunityBinding

class RecyclerActivityCommunity : AppCompatActivity(), RecyclerAdapter.OnMapClick {
    private lateinit var binding: ActivityRecyclerCommunityBinding
    private val dataBase = Firebase.firestore


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRecyclerCommunityBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    override fun onStart() {
        super.onStart()
        setupRecyclerView()
    }

    private fun setupRecyclerView(){
        for (route in routesCommunity){
            var idroute = route.id
            var emailroute = route.email

            if(routesCount.contains(idroute)) {

                var pointId = route.pointLatLng

                for(r in routesCommunityRecycler){
                    if(r.id == idroute && r.email == emailroute){
                        r.pointLatLng.putAll(pointId)
                    }
                }

            }else{
                routesCount.add(idroute)
                routesCommunityRecycler.add(route)
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
        binding.recyclerView.addItemDecoration(
            DividerItemDecoration(this,
                DividerItemDecoration.VERTICAL)
        )
        binding.recyclerView.adapter = RecyclerAdapter(this, routesCommunityRecycler, this)
    }



    override fun onClick(id: Long, pointLatLng: MutableMap<Long, LatLng>) {
        Toast.makeText(this, "Clicado en ${id}", Toast.LENGTH_SHORT).show()
        val intent = Intent(this,MapsActivityRecycler::class.java)
        intent.putExtra("Ruta", id)
        intent.putExtra("Community", true)
        startActivity(intent)
    }

    override fun butClick(id: Long, email: String) {

        /*val intent = Intent(this,OptionsActivity::class.java)
        intent.putExtra("Ruta", id)
        startActivity(intent)*/


    }

}