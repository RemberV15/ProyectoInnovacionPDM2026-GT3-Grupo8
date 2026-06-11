package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StockAdapter(
    private var lista: List<Producto>,
    private val onItemClick: (Producto) -> Unit // Escuchador del clic
) : RecyclerView.Adapter<StockAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombre: TextView = view.findViewById(R.id.tv_nombre)
        val sku: TextView = view.findViewById(R.id.tv_sku)
        val categoria: TextView = view.findViewById(R.id.tv_categoria)
        val estado: TextView = view.findViewById(R.id.tv_estado) // <- Añadido para el texto verde/rojo
        val cantidad: TextView = view.findViewById(R.id.tv_cantidad) // <- Para el número de unidades
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
        holder.categoria.text = "Categoría: ${item.categoria}"

        // 1. Mostrar número exacto de unidades abajo
        holder.cantidad.text = "${item.cantidad} unidades"

        if (item.cantidad > 0) {
            holder.estado.text = "Disponible"
            holder.estado.setTextColor(android.graphics.Color.parseColor("#2E7D32")) // Verde
        } else {
            holder.estado.text = "Agotado"
            holder.estado.setTextColor(android.graphics.Color.parseColor("#D32F2F")) // Rojo fuerte
        }

        holder.estante.text = item.ubicacion

        holder.itemView.setOnClickListener {
            onItemClick(item)
        }
    }

    override fun getItemCount() = lista.size
}