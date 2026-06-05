package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton

class DetalleProductoDialog : DialogFragment() {

    // Interfaz para comunicar eventos de clics de vuelta al fragmento principal si lo necesitas
    interface OnContextActionListener {
        fun onEditarSelected()
    }

    var actionListener: OnContextActionListener? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Hacemos transparente el fondo nativo del diálogo para lucir las esquinas redondeadas del CardView
        dialog?.window?.apply {
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return inflater.inflate(R.layout.dialog_detalle_producto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. ENLAZAR VISTAS COMPLETAS (CONCORDANCIA ABSOLUTA CON EL XML DE 3 FILAS) ---
        val tvTituloDialog = view.findViewById<TextView>(R.id.tvTituloDialog)
        val tvDetalleNombre = view.findViewById<TextView>(R.id.tvDetalleNombre)
        val tvDetalleSKU = view.findViewById<TextView>(R.id.tvDetalleSKU)
        val tvDetalleEstado = view.findViewById<TextView>(R.id.tvDetalleEstado)
        val tvDetalleUbicacion = view.findViewById<TextView>(R.id.tvDetalleUbicacion)
        val tvDetalleCantidad = view.findViewById<TextView>(R.id.tvDetalleCantidad)
        val tvDetalleProveedor = view.findViewById<TextView>(R.id.tvDetalleProveedor)
        val tvDetalleCategoria = view.findViewById<TextView>(R.id.tvDetalleCategoria)

        val ivProductoPreview = view.findViewById<ImageView>(R.id.ivDetalleProductoPreview)
        val ivDetalleCodigo = view.findViewById<ImageView>(R.id.ivDetalleCodigo)

        val btnCerrarX = view.findViewById<ImageButton>(R.id.btnCerrarX)
        val btnEditar = view.findViewById<MaterialButton>(R.id.btnDetalleEditar)
        val btnCerrar = view.findViewById<MaterialButton>(R.id.btnDetalleCerrar)
        val btnHistorial = view.findViewById<MaterialButton>(R.id.btnDetalleHistorial)

        // --- 2. ASIGNAR TEXTOS BASE DE EJEMPLO (CALCO DE TU DISEÑO OBJETIVO) ---
        tvTituloDialog.text = "Detalles del Producto: Office Chair"
        tvDetalleNombre.text = "Office Chair"
        tvDetalleSKU.text = "SKU: SKU-OC-HM-001"
        tvDetalleEstado.text = "✔ Disponible"
        tvDetalleUbicacion.text = "Almacén C, Estante C"
        tvDetalleCantidad.text = "25 unidades"
        tvDetalleProveedor.text = "Herman Miller Inc."
        tvDetalleCategoria.text = "Mobiliario"

        // --- 3. GESTIÓN DE ACCIONES Y CLICS ---

        // Clic en la 'X' superior derecha para cerrar
        btnCerrarX.setOnClickListener { dismiss() }

        // Clic en el botón "Cerrar Detalles" inferior
        btnCerrar.setOnClickListener { dismiss() }

        // Clic en el texto flotante de "Historial de Stock"
        btnHistorial.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo Historial de Stock...", Toast.LENGTH_SHORT).show()
        }

        // Clic en el botón "Editar Producto"
        btnEditar.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo editor de producto...", Toast.LENGTH_SHORT).show()
            actionListener?.onEditarSelected()
            dismiss()
        }
    }

    companion object {
        const val TAG = "DetalleProductoDialog"

        // Método estático seguro para crear una nueva instancia del Diálogo
        fun newInstance(): DetalleProductoDialog {
            return DetalleProductoDialog()
        }
    }
}