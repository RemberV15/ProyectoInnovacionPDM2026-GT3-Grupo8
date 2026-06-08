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

        // Estructurados correctamente respetando tipos (Int en cantidad)
        val listaProductos = listOf(
            Producto("2238359", "Café Vala", "Bebidas", 50, "Estante A1", ""),
            Producto("3238355", "Botella Aramentos", "Envases", 20, "Estante B2", ""),
            Producto("3238359", "Producto Vala", "General", 20, "Estante A1", "")
        )

        rvStock.layoutManager = LinearLayoutManager(context)
        rvStock.adapter = StockAdapter(listaProductos)
    }
}