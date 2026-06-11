package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.graphics.drawable.toDrawable
import androidx.core.graphics.toColorInt
import androidx.fragment.app.DialogFragment
import com.google.android.material.button.MaterialButton

class DetalleProductoDialog : DialogFragment() {

    interface OnContextActionListener {
        fun onEditarSelected(codigoProducto: String) // Exigimos enviar el código
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
            setBackgroundDrawable(Color.TRANSPARENT.toDrawable())
            requestFeature(Window.FEATURE_NO_TITLE)
        }
        return inflater.inflate(R.layout.dialog_detalle_producto, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
            window.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTituloDialog = view.findViewById<TextView>(R.id.tvTituloDialog)
        val tvDetalleNombre = view.findViewById<TextView>(R.id.tvDetalleNombre)
        val tvDetalleSKU = view.findViewById<TextView>(R.id.tvDetalleSKU)
        val tvDetalleEstado = view.findViewById<TextView>(R.id.tvDetalleEstado)
        val tvDetalleUbicacion = view.findViewById<TextView>(R.id.tvDetalleUbicacion)
        val tvDetalleCantidad = view.findViewById<TextView>(R.id.tvDetalleCantidad)
        val tvDetalleCategoria = view.findViewById<TextView>(R.id.tvDetalleCategoria)
        val tvDetalleProveedor = view.findViewById<TextView>(R.id.tvDetalleProveedor)
        val ivDetalleProductoPreview = view.findViewById<ImageView>(R.id.ivDetalleProductoPreview)

        val btnCerrarX = view.findViewById<ImageButton>(R.id.btnCerrarX)
        val btnEditar = view.findViewById<MaterialButton>(R.id.btnDetalleEditar)
        val btnCerrar = view.findViewById<MaterialButton>(R.id.btnDetalleCerrar)
        val btnHistorial = view.findViewById<MaterialButton>(R.id.btnDetalleHistorial)

        val nombreProd = arguments?.getString("ARG_NOMBRE") ?: "Desconocido"
        val codigoProd = arguments?.getString("ARG_CODIGO") ?: ""
        val ubicacionProd = arguments?.getString("ARG_UBICACION") ?: "No asignada"
        val cantidadProd = arguments?.getInt("ARG_CATEGIDAD") ?: 0
        val categoriaProd = arguments?.getString("ARG_CATEGORIA") ?: "General"
        val proveedorProd = arguments?.getString("ARG_PROVEEDOR") ?: "No registrado"

        val base64Image = arguments?.getString("ARG_IMAGEN_BASE64")
        if (!base64Image.isNullOrEmpty()) {
            try {
                val imageBytes = Base64.decode(base64Image, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                ivDetalleProductoPreview.setImageBitmap(decodedImage)
            } catch (e: Exception) { e.printStackTrace() }
        }

        tvTituloDialog.text = "Detalles del Producto"
        tvDetalleNombre.text = nombreProd
        tvDetalleSKU.text = "SKU: $codigoProd"
        tvDetalleUbicacion.text = ubicacionProd
        tvDetalleCantidad.text = "$cantidadProd unidades"
        tvDetalleCategoria.text = categoriaProd
        tvDetalleProveedor.text = proveedorProd

        if (cantidadProd > 0) {
            tvDetalleEstado.text = "Disponible"
            tvDetalleEstado.setTextColor("#2E7D32".toColorInt())
        } else {
            tvDetalleEstado.text = "Agotado"
            tvDetalleEstado.setTextColor(Color.RED)
        }

        btnCerrarX.setOnClickListener { dismiss() }
        btnCerrar.setOnClickListener { dismiss() }

        btnHistorial.setOnClickListener {
            Toast.makeText(requireContext(), "Abriendo Historial...", Toast.LENGTH_SHORT).show()
        }

        btnEditar.setOnClickListener {
            actionListener?.onEditarSelected(codigoProd)
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
                putInt("ARG_CATEGIDAD", producto.cantidad)
                putString("ARG_UBICACION", producto.ubicacion)
                putString("ARG_PROVEEDOR", producto.proveedor)
                putString("ARG_IMAGEN_BASE64", producto.imagenBase64)
            }
            fragment.arguments = args
            return fragment
        }
    }
}