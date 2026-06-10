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

        // 1. Declaramos el nuevo botón de exportar/enviar
        val fabExportar = view.findViewById<FloatingActionButton>(R.id.fab_exportar)

        recienteAdapter = RecienteAdapter(arrayListOf())
        rvRecientes.layoutManager = LinearLayoutManager(context)
        rvRecientes.adapter = recienteAdapter

        // Navegación de la Cámara
        fabCamara.setOnClickListener {
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.content_container, AgregarProductoFragment())
                addToBackStack(null)
                commit()
            }
        }

        // 2. Agregamos la navegación del botón Exportar
        fabExportar.setOnClickListener {
            // Un Toast de prueba mientras creas la pantalla
            Toast.makeText(context, "Abriendo pantalla de envío...", Toast.LENGTH_SHORT).show()

            // TODO: Agregar la funcionalidad de la nuevo pop up / pantalla
            /*
            parentFragmentManager.beginTransaction().apply {
                replace(R.id.content_container, TuFragmentoDeEnvio())
                addToBackStack(null)
                commit()
            }
            */
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

                    // MODIFICACIÓN CLAVE: Usamos tu variable 'timestamp'.
                    // Lo convertimos a Date y sacamos el tiempo. Si es nulo, usa 0L (lo manda al fondo).
                    val listaOrdenada = listaProductosReal
                        .sortedByDescending { it.timestamp?.toDate()?.time ?: 0L }
                        .take(5)

                    recienteAdapter.actualizarLista(listaOrdenada)
                }
            }

        fabExportar.setOnClickListener {
            val bottomSheet = ExportarBottomSheet()
            bottomSheet.show(parentFragmentManager, "ExportarBottomSheet")
        }
    }
}