package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FiltroStockDialog(
    private val listaCompleta: List<Producto>,
    private val onFiltrar: (List<Producto>) -> Unit
) : BottomSheetDialogFragment(R.layout.dialog_filtros) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.btn_cat).setOnClickListener {
            mostrarMenu(it, listaCompleta.map { it.categoria }.distinct()) { item ->
                onFiltrar(listaCompleta.filter { it.categoria == item })
            }
        }

        view.findViewById<Button>(R.id.btn_ubi).setOnClickListener {
            mostrarMenu(it, listaCompleta.map { it.ubicacion }.distinct()) { item ->
                onFiltrar(listaCompleta.filter { it.ubicacion == item })
            }
        }

        view.findViewById<Button>(R.id.btn_est).setOnClickListener {

        }

    }

    private fun mostrarMenu(anchor: View, opciones: List<String>, onSelected: (String) -> Unit) {
        val popup = PopupMenu(requireContext(), anchor)
        opciones.forEach { popup.menu.add(it) }
        popup.setOnMenuItemClickListener {
            onSelected(it.title.toString())
            true
        }
        popup.show()
    }
}