package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.firestore.FirebaseFirestore

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recienteAdapter: RecienteAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvRecientes = view.findViewById<RecyclerView>(R.id.rv_recientes)
        val tvContador = view.findViewById<TextView>(R.id.tv_dashboard_contador)
        val fabCamara = view.findViewById<FloatingActionButton>(R.id.fab_camara)

        recienteAdapter = RecienteAdapter(arrayListOf())
        rvRecientes.layoutManager = LinearLayoutManager(context)
        rvRecientes.adapter = recienteAdapter

        fabCamara.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.content_container, AgregarProductoFragment())
                addToBackStack(null)
                commit()
            }
        }

        db.collection("productos")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Toast.makeText(context, "Error al conectar base de datos: ${error.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val totalProductos = snapshot.size()
                    tvContador.text = totalProductos.toString()

                    val listaProductosReal = snapshot.toObjects(Producto::class.java)

                    val listaOrdenada = listaProductosReal.sortedByDescending { it.codigo }

                    recienteAdapter.actualizarLista(listaOrdenada)
                }
            }
    }
}