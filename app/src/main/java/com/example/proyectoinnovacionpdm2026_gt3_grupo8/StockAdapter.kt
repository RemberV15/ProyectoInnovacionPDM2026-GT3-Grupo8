package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StockAdapter(private var lista: List<Producto>) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tv_nombre)
        val sku: TextView = view.findViewById(R.id.tv_sku)
        val cantidad: TextView = view.findViewById(R.id.tv_cantidad)
        val estante: TextView = view.findViewById(R.id.tv_estante)
    }

    fun actualizarLista(nuevaLista: List<Producto>) {
        this.lista = nuevaLista
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_stock, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]
        holder.nombre.text = item.nombre
        holder.sku.text = "SKU: ${item.codigo}"
        holder.cantidad.text = "${item.cantidad} unidades"
        holder.estante.text = item.ubicacion
    }

    override fun getItemCount() = lista.size
}