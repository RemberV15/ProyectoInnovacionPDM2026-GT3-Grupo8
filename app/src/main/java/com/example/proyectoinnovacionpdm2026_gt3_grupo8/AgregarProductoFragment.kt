package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AgregarProductoFragment : Fragment(R.layout.fragment_agregar_producto) {

    // Variable global para manejar la cantidad en pantalla
    private var cantidadStock = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // --- 1. ENLAZAR VISTAS (MATCH CON EL NUEVO XML) ---
        val cardTomarFoto = view.findViewById<MaterialCardView>(R.id.cardTomarFoto)
        val ivProductoPreview = view.findViewById<ImageView>(R.id.ivProductoPreview)

        // Para detectar el clic en los íconos de la derecha, usamos el TextInputLayout padre
        val inputNombreLayout = view.findViewById<TextInputEditText>(R.id.etNombreProducto).parent.parent as TextInputLayout
        val inputSKULayout = view.findViewById<TextInputEditText>(R.id.etSKU).parent.parent as TextInputLayout

        // Fila del stock
        val btnMenosStock = view.findViewById<TextView>(R.id.btnMenosStock)
        val btnMasStock = view.findViewById<TextView>(R.id.btnMasStock)
        val tvCantidadStock = view.findViewById<TextView>(R.id.tvCantidadStock)

        // Botones de acción inferiores
        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardarProducto)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelar)


        // --- 2. LÓGICA DEL CONTADOR DE STOCK ---
        btnMasStock.setOnClickListener {
            cantidadStock++
            tvCantidadStock.text = cantidadStock.toString()
        }

        btnMenosStock.setOnClickListener {
            if (cantidadStock > 0) { // Evita números negativos
                cantidadStock--
                tvCantidadStock.text = cantidadStock.toString()
            }
        }


        // --- 3. CLICS PARA LOS SENSORES IMPORTANTES ---
        cardTomarFoto.setOnClickListener {
            Toast.makeText(requireContext(), "Preparando cámara...", Toast.LENGTH_SHORT).show()
            // Aquí irá el ActivityResultContracts.TakePicturePreview()
        }

        inputNombreLayout.setEndIconOnClickListener {
            Toast.makeText(requireContext(), "Escuchando micrófono...", Toast.LENGTH_SHORT).show()
            // Aquí irá el Intent de RecognizerIntent (Speech-to-Text)
        }

        inputSKULayout.setEndIconOnClickListener {
            Toast.makeText(requireContext(), "Abriendo escáner ZXing...", Toast.LENGTH_SHORT).show()
            // Aquí irá el IntentIntegrator de ZXing
        }


        // --- 4. BOTONES PRINCIPALES ---
        btnGuardar.setOnClickListener {
            // Aquí recuperaremos los textos de los EditText y los subiremos a Firebase
            Toast.makeText(requireContext(), "Guardando producto en Firebase...", Toast.LENGTH_SHORT).show()
        }

        btnCancelar.setOnClickListener {
            // Acción rápida para regresar a la pantalla anterior
            requireActivity().supportFragmentManager.popBackStack()
        }
    }
}