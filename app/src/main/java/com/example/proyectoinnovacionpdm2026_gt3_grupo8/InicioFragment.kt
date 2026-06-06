package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Vincular el RecyclerView del layout
        val rvRecientes = view.findViewById<RecyclerView>(R.id.rv_recientes)

        // 2. Definir datos de prueba para la lista de recientes
        val listaRecientes = listOf(
            Producto("Producto Vala", "3238359", "20 unidades", "Estante A1"),
            Producto("Producto Aramentos", "3238355", "20 unidades", "Estante B2"),
            Producto("Producto Producto", "0000000", "0 unidades", "Sin asignar")
        )

        // 3. Configurar el LayoutManager y el Adapter
        rvRecientes.layoutManager = LinearLayoutManager(context)
        rvRecientes.adapter = RecienteAdapter(listaRecientes)
    }
}