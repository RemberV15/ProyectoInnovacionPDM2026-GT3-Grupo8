package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StockFragment : Fragment(R.layout.fragment_stock) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvStock = view.findViewById<RecyclerView>(R.id.rv_stock)

        // Datos de prueba (aquí luego conectarás tu base de datos)
        val listaProductos = listOf(
            Producto("Café Vala", "2238359", "50 bolsas", "Estante A1"),
            Producto("Botella Aramentos", "3238355", "20 unidades", "Estante B2"),
            Producto("Producto Vala", "3238359", "20 unidades", "Estante A1")
        )

        rvStock.layoutManager = LinearLayoutManager(context)
        rvStock.adapter = StockAdapter(listaProductos)
    }
}