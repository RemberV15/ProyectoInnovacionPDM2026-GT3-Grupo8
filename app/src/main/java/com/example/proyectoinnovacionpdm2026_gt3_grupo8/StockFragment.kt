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

        val listaProductos = listOf(
            Producto(
                codigo = "2238359",
                nombre = "Café Vala",
                descripcion = "Café instantáneo premium en frasco",
                categoria = "Bebidas",
                cantidad = 50,
                ubicacion = "Estante A1",
                imagen_url = ""
            ),
            Producto(
                codigo = "3238355",
                nombre = "Botella Aramentos",
                descripcion = "Botella de plástico biodegradable 500ml",
                categoria = "Envases",
                cantidad = 20,
                ubicacion = "Estante B2",
                imagen_url = ""
            ),
            Producto(
                codigo = "3238359",
                nombre = "Producto Vala",
                descripcion = "Descripción detallada del producto de prueba",
                categoria = "General",
                cantidad = 20,
                ubicacion = "Estante A1",
                imagen_url = ""
            )
        )

        rvStock.layoutManager = LinearLayoutManager(context)
        rvStock.adapter = StockAdapter(listaProductos)
    }
}