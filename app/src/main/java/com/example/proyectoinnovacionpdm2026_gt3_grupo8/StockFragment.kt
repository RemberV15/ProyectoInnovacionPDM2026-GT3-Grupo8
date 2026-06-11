package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class StockFragment : Fragment(R.layout.fragment_stock) {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var stockAdapter: StockAdapter
    private var listaCompleta = listOf<Producto>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvStock = view.findViewById<RecyclerView>(R.id.rv_stock)

        stockAdapter = StockAdapter(emptyList()) { productoSeleccionado ->
            mostrarDetalleProducto(productoSeleccionado)
        }

        rvStock.layoutManager = LinearLayoutManager(context)
        rvStock.adapter = stockAdapter

        db.collection("productos").addSnapshotListener { snapshot, _ ->
            if (snapshot != null) {
                listaCompleta = snapshot.toObjects(Producto::class.java)
                stockAdapter.actualizarLista(listaCompleta)
            }
        }

        view.findViewById<ImageView>(R.id.btn_filtro).setOnClickListener {
            mostrarPopupFiltros()
        }
    }
    private fun mostrarDetalleProducto(producto: Producto) {
        val dialog = DetalleProductoDialog.newInstance(producto)
        dialog.show(parentFragmentManager, DetalleProductoDialog.TAG)
    }

    private fun mostrarPopupFiltros() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_filtros, null)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        val btnCat = dialogView.findViewById<Button>(R.id.btn_cat)
        val btnUbi = dialogView.findViewById<Button>(R.id.btn_ubi)
        val btnEst = dialogView.findViewById<Button>(R.id.btn_est)
        val btnLimpiar = dialogView.findViewById<Button>(R.id.btn_limpiar)

        btnCat.setOnClickListener {
            val opciones = listaCompleta.map { it.categoria }.distinct().toTypedArray()
            mostrarListaSeleccion(opciones, "Categoría", { seleccion ->
                stockAdapter.actualizarLista(listaCompleta.filter { it.categoria == seleccion })
            }, dialog)
        }

        btnUbi.setOnClickListener {
            val opciones = listaCompleta.map { it.ubicacion }.distinct().toTypedArray()
            mostrarListaSeleccion(opciones, "Ubicación", { seleccion ->
                stockAdapter.actualizarLista(listaCompleta.filter { it.ubicacion == seleccion })
            }, dialog)
        }

        btnEst.setOnClickListener {
            val opciones = arrayOf("Disponible", "Agotado")
            mostrarListaSeleccion(opciones, "Estado", { seleccion ->
                val filtrada = when (seleccion) {
                    "Disponible" -> listaCompleta.filter { it.cantidad > 0 }
                    "Agotado" -> listaCompleta.filter { it.cantidad <= 0 }
                    else -> listaCompleta
                }
                stockAdapter.actualizarLista(filtrada)
            }, dialog)
        }

        btnLimpiar.setOnClickListener {
            stockAdapter.actualizarLista(listaCompleta)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun mostrarListaSeleccion(
        opciones: Array<String>,
        titulo: String,
        alSeleccionado: (String) -> Unit,
        dialogPrincipal: AlertDialog
    ) {
        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_1, opciones) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val view = super.getView(position, convertView, parent)
                val textView = view.findViewById<TextView>(android.R.id.text1)
                textView?.setTextColor(Color.parseColor("#050FA3"))
                textView?.setTypeface(null, Typeface.BOLD)

                return view
            }
        }

        val tituloView = TextView(requireContext()).apply {
            text = "Seleccionar $titulo"
            textSize = 18f
            setTypeface(null, Typeface.BOLD)
            setTextColor(Color.BLACK)
            setPadding(48, 48, 48, 48)
        }

        val listDialog = AlertDialog.Builder(requireContext())
            .setCustomTitle(tituloView)
            .setAdapter(adapter) { _, which ->
                alSeleccionado(opciones[which])
                dialogPrincipal.dismiss()
            }
            .create()

        listDialog.window?.setBackgroundDrawableResource(R.drawable.bg_dialog_redondeado)
        listDialog.show()
    }
}