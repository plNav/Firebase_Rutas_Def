package com.pablolop.firebase

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.DialogFragment

class DialogFragmentMapName: DialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView = inflater.inflate(R.layout.dialog_fragment_map_name, container, false)

        val butCancel = rootView.findViewById<Button>(R.id.but_cancelar)
        val butDescart = rootView.findViewById<Button>(R.id.but_descartar)
        val butGuard = rootView.findViewById<Button>(R.id.but_guardar)
        val textName = rootView.findViewById<TextView>(R.id.text_name_map)

        butCancel.setOnClickListener {
            dialogMapResult = "Cancelar"
            dismiss()
        }

        butDescart.setOnClickListener {
            dialogMapResult = "Descartar"
            dismiss()
        }

        butGuard.setOnClickListener {
            dialogMapResult = textName.text.toString()
            dialogName = textName.text.toString()
            registerName = textName.text.toString()
            dismiss()
        }

        return rootView
    }
}