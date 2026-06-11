package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.annotation.SuppressLint
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView

class RecienteAdapter(private var lista: List<Producto>) : RecyclerView.Adapter<RecienteAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val ivProducto: ImageView = view.findViewById(R.id.iv_producto_reciente)
        val tvNombre: TextView = view.findViewById(R.id.tv_nombre_reciente)
        val tvDetalles: TextView = view.findViewById(R.id.tv_detalles_reciente)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_reciente, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = lista[position]

        holder.tvNombre.text = item.nombre
        holder.tvDetalles.text = holder.itemView.context.getString(R.string.detalle_format, item.categoria, item.codigo)

        if (item.imagenBase64.isNotEmpty()) {
            try {
                val imageBytes = Base64.decode(item.imagenBase64, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                holder.ivProducto.setImageBitmap(decodedImage)
            } catch (_: Exception) {
                holder.ivProducto.setImageBitmap(null)
            }
        } else {
            holder.ivProducto.setImageBitmap(null)
        }

        holder.itemView.setOnClickListener { view ->
            val contexto = view.context

            if (contexto is AppCompatActivity) {
                val dialog = DetalleProductoDialog.newInstance(item)

                dialog.actionListener = object : DetalleProductoDialog.OnContextActionListener {
                    override fun onEditarSelected(codigoProducto: String) {
                        val fragmentoEdicion = AgregarProductoFragment()
                        val args = Bundle()
                        args.putString("ARG_CODIGO_EDITAR", codigoProducto)
                        fragmentoEdicion.arguments = args

                        contexto.supportFragmentManager.beginTransaction()
                            .replace(R.id.content_container, fragmentoEdicion)
                            .addToBackStack(null)
                            .commit()
                    }
                }

                dialog.show(contexto.supportFragmentManager, DetalleProductoDialog.TAG)
            }
        }
    }

    override fun getItemCount() = lista.size

    @SuppressLint("NotifyDataSetChanged")
    fun actualizarLista(nuevaLista: List<Producto>) {
        this.lista = nuevaLista
        notifyDataSetChanged()
    }
}