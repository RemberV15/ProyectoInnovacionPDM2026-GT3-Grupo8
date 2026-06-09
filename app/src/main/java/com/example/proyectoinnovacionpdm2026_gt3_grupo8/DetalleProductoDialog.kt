package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton

class DetalleProductoDialog : DialogFragment() {

    interface OnContextActionListener {
        fun onEditarSelected()
    }

    var actionListener: OnContextActionListener? = null

    interface OnDismissListener {
        fun onDialogDismissed()
    }

    var dismissListener: OnDismissListener? = null

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        dismissListener?.onDialogDismissed()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dialog?.window?.apply {
            // CORRECCIÓN: Se utiliza la función de extensión KTX 'Int.toDrawable()' recomendada
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return inflater.inflate(R.layout.dialog_detalle_producto, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Enlace de vistas
        val tvTituloDialog = view.findViewById<TextView>(R.id.tvTituloDialog)
        val tvDetalleNombre = view.findViewById<TextView>(R.id.tvDetalleNombre)
        val tvDetalleSKU = view.findViewById<TextView>(R.id.tvDetalleSKU)
        val tvDetalleEstado = view.findViewById<TextView>(R.id.tvDetalleEstado)
        val tvDetalleUbicacion = view.findViewById<TextView>(R.id.tvDetalleUbicacion)
        val tvDetalleCantidad = view.findViewById<TextView>(R.id.tvDetalleCantidad)
        val tvDetalleCategoria = view.findViewById<TextView>(R.id.tvDetalleCategoria)

        val btnCerrarX = view.findViewById<ImageButton>(R.id.btnCerrarX)
        val btnEditar = view.findViewById<MaterialButton>(R.id.btnDetalleEditar)
        val btnCerrar = view.findViewById<MaterialButton>(R.id.btnDetalleCerrar)
        val btnHistorial = view.findViewById<MaterialButton>(R.id.btnDetalleHistorial)

        val nombreProd = arguments?.getString("ARG_NOMBRE") ?: "Desconocido"
        val codigoProd = arguments?.getString("ARG_CODIGO") ?: ""
        val ubicacionProd = arguments?.getString("ARG_UBICACION") ?: "No asignada"
        val cantidadProd = arguments?.getInt("ARG_CANTIDAD") ?: 0
        val categoriaProd = arguments?.getString("ARG_CATEGORIA") ?: "General"

        // Asignación de textos con plantillas de Kotlin
        tvTituloDialog.text = "Detalles del Producto: $nombreProd"
        tvDetalleNombre.text = nombreProd
        tvDetalleSKU.text = "SKU: $codigoProd"
        tvDetalleUbicacion.text = ubicacionProd
        tvDetalleCantidad.text = "$cantidadProd unidades"
        tvDetalleCategoria.text = categoriaProd

        if (cantidadProd > 0) {
            tvDetalleEstado.text = "✔ Disponible"
            tvDetalleEstado.setTextColor("#2E7D32".toColorInt())
        } else {
            tvDetalleEstado.text = "❌ Agotado"
            tvDetalleEstado.setTextColor(Color.RED)
        }

        btnCerrarX.setOnClickListener { dismiss() }
        btnCerrar.setOnClickListener { dismiss() }

        btnHistorial.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo Historial de Stock...", Toast.LENGTH_SHORT).show()
        }

        btnEditar.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo editor de producto...", Toast.LENGTH_SHORT).show()
            actionListener?.onEditarSelected()
            dismiss()
        }
    }

    companion object {
        const val TAG = "DetalleProductoDialog"

        fun newInstance(producto: Producto): DetalleProductoDialog {
            val fragment = DetalleProductoDialog()
            val args = Bundle().apply {
                putString("ARG_CODIGO", producto.codigo)
                putString("ARG_NOMBRE", producto.nombre)
                putString("ARG_CATEGORIA", producto.categoria)
                putInt("ARG_CANTIDAD", producto.cantidad)
                putString("ARG_UBICACION", producto.ubicacion)
            }
            fragment.arguments = args
            return fragment
        }
    }
}