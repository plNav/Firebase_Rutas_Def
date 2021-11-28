package com.pablolop.firebase

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import java.lang.IllegalArgumentException

class RecyclerAdapter (
    private val context: Context,
    private val allRoutes : MutableList<RouteData>,
    private val mapClickListener : OnMapClick
    ) : RecyclerView.Adapter<BaseRecyclerViewHolder<*>>() {

    interface OnMapClick{
        fun onClick(id: Long, pointLatLng: MutableMap<Long, LatLng>)
        fun butClick(id: Long, email: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseRecyclerViewHolder<*> {
       return RoutesViewHolder(LayoutInflater.from(context).inflate(R.layout.routes_row, parent, false))
    }

    override fun getItemCount(): Int = allRoutes.size

    override fun onBindViewHolder(holder: BaseRecyclerViewHolder<*>, position: Int) {
        when(holder){
            is RoutesViewHolder -> holder.bind(allRoutes[position], position)
            else -> throw IllegalArgumentException("Error al pasar el viewHolder")
        }
    }

    inner class RoutesViewHolder(itemView: View) :BaseRecyclerViewHolder<RouteData>(itemView) {
        override fun bind(item: RouteData, position: Int) {

            val butDetails : Button = itemView.findViewById<Button>(R.id.but_details)
            val textRoute : TextView = itemView.findViewById(R.id.text_route)

            butDetails.setOnClickListener { mapClickListener.butClick(item.id, item.email) }
            itemView.setOnClickListener { mapClickListener.onClick(item.id, item.pointLatLng) }


            if(item.author != ""){
                butDetails.visibility = View.GONE
                textRoute.text = "${item.name} [${item.id}] by \n ${item.author}"

            }else{
                textRoute.text = "${item.name} [${item.id}] by \n ${item.email}"

            }

        }
    }

}