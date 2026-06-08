package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore

class AgregarProductoFragment : Fragment() {

    private var cantidadStock = 0
    private val db = FirebaseFirestore.getInstance()

    private val categoriasPreestablecidas = arrayOf(
        "Mobiliario", "Electrónicos", "Papelería", "Herramientas", "Limpieza"
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_agregar_producto, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardTomarFoto = view.findViewById<MaterialCardView>(R.id.cardTomarFoto)
        val etNombreProducto = view.findViewById<TextInputEditText>(R.id.etNombreProducto)
        val etSKU = view.findViewById<TextInputEditText>(R.id.etSKU)
        val etUbicacion = view.findViewById<TextInputEditText>(R.id.etUbicacion)
        val autoCompleteCategoria = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteCategoria)

        val btnMenosStock = view.findViewById<TextView>(R.id.btnMenosStock)
        val btnMasStock = view.findViewById<TextView>(R.id.btnMasStock)
        val tvCantidadStock = view.findViewById<TextView>(R.id.tvCantidadStock)

        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardarProducto)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelar)

        // CONFIGURACIÓN DE ARGUMENTOS: Captura el SKU enviado por el escáner si existe
        val skuRecibido = arguments?.getString("sku_enviado_escaner")
        if (!skuRecibido.isNullOrEmpty()) {
            etSKU.setText(skuRecibido) // Se auto-rellena el campo del código
        }

        val adapterCategorias = ArrayAdapter(
            requireContext(), android.R.layout.simple_dropdown_item_1line, categoriasPreestablecidas
        )
        autoCompleteCategoria.setAdapter(adapterCategorias)
        autoCompleteCategoria.setText(categoriasPreestablecidas[0], false)

        btnMasStock.setOnClickListener {
            cantidadStock++
            tvCantidadStock.text = cantidadStock.toString()
        }

        btnMenosStock.setOnClickListener {
            if (cantidadStock > 0) {
                cantidadStock--
                tvCantidadStock.text = cantidadStock.toString()
            }
        }

        cardTomarFoto.setOnClickListener {
            Toast.makeText(requireContext(), "Preparando cámara...", Toast.LENGTH_SHORT).show()
        }

        btnGuardar.setOnClickListener {
            val nombre = etNombreProducto.text.toString().trim()
            val codigo = etSKU.text.toString().trim()
            val ubicacion = etUbicacion.text.toString().trim()
            val categoriaSeleccionada = autoCompleteCategoria.text.toString()

            if (nombre.isEmpty() || codigo.isEmpty() || ubicacion.isEmpty()) {
                Toast.makeText(requireContext(), "Por favor rellena todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val nuevoProducto = Producto(
                codigo = codigo,
                nombre = nombre,
                categoria = categoriaSeleccionada,
                cantidad = cantidadStock,
                ubicacion = ubicacion,
                imagen_url = "",
                timestamp = com.google.firebase.Timestamp.now()
            )

            db.collection("productos").document(codigo)
                .set(nuevoProducto)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "¡Producto guardado exitosamente!", Toast.LENGTH_SHORT).show()
                    etNombreProducto.text?.clear()
                    etSKU.text?.clear()
                    etUbicacion.text?.clear()
                    autoCompleteCategoria.setText(categoriasPreestablecidas[0], false)
                    cantidadStock = 0
                    tvCantidadStock.text = "0"
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al guardar: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        btnCancelar.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}