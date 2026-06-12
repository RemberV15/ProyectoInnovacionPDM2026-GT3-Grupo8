package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class InicioFragment : Fragment(R.layout.fragment_inicio) {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recienteAdapter: RecienteAdapter
    private lateinit var auth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        auth = FirebaseAuth.getInstance()

        val rvRecientes = view.findViewById<RecyclerView>(R.id.rv_recientes)
        val tvContador = view.findViewById<TextView>(R.id.tv_dashboard_contador)
        val fabCamara = view.findViewById<FloatingActionButton>(R.id.fab_camara)
        val fabExportar = view.findViewById<FloatingActionButton>(R.id.fab_exportar)

        // Referencia al nuevo botón superior de Logout
        val btnLogout = view.findViewById<ImageView>(R.id.btn_logout)

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

        // Lógica del botón Exportar
        fabExportar.setOnClickListener {
            val bottomSheet = ExportarBottomSheet()
            bottomSheet.show(parentFragmentManager, "ExportarBottomSheet")
        }

        // Lógica del botón superior Cerrar Sesión
        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(context, "Sesión cerrada correctamente", Toast.LENGTH_SHORT).show()

            // Redirigimos a LoginActivity limpiando el historial
            val intent = Intent(activity, LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            activity?.finish()
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

                    val listaOrdenada = listaProductosReal
                        .sortedByDescending { it.timestamp?.toDate()?.time ?: 0L }
                        .take(5)

                    recienteAdapter.actualizarLista(listaOrdenada)
                }
            }
    }
}