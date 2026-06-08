package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class RecienteAdapter(private var lista: List<Producto>) : RecyclerView.Adapter<RecienteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProducto: ImageView = view.findViewById(R.id.iv_producto_reciente)
        val tvNombre: TextView = view.findViewById(R.id.tv_nombre_reciente)

        val tvDetalles: TextView = view.findViewById(R.id.tv_detalles_reciente)
        val ivEscanear: ImageView = view.findViewById(R.id.iv_escanear_reciente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reciente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.tvNombre.text = item.nombre

        holder.tvDetalles.text = "Categoría: ${item.categoria}\nSKU: ${item.codigo}"
    }

    override fun getItemCount() = lista.size

    fun actualizarLista(nuevaLista: List<Producto>) {
        this.lista = nuevaLista
        notifyDataSetChanged()
    }
}