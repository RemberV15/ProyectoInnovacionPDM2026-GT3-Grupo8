package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.speech.RecognizerIntent
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.util.Locale

class AgregarProductoFragment : Fragment() {

    private var cantidadStock = 0
    private val db = FirebaseFirestore.getInstance()
    private lateinit var etDescripcionProducto: TextInputEditText
    private lateinit var ivProductoPreview: ImageView
    private var imageBitmap: Bitmap? = null

    private val categoriasPreestablecidas = arrayOf(
        "Mobiliario", "Electrónicos", "Papelería", "Herramientas", "Limpieza"
    )

    private val lanzarSpeechToText = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { res ->
        if (res.resultCode == Activity.RESULT_OK && res.data != null) {
            val resList = res.data!!.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)
            if (!resList.isNullOrEmpty()) etDescripcionProducto.setText(resList[0])
        }
    }

    private val lanzarCamara = registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap ->
        if (bitmap != null) { imageBitmap = bitmap; ivProductoPreview.setImageBitmap(bitmap) }
    }

    private val lanzarGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P)
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, uri))
            else MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            ivProductoPreview.setImageBitmap(imageBitmap)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_agregar_producto, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val cardTomarFoto = view.findViewById<MaterialCardView>(R.id.cardTomarFoto)
        ivProductoPreview = view.findViewById(R.id.ivProductoPreview)
        val etNombreProducto = view.findViewById<TextInputEditText>(R.id.etNombreProducto)
        etDescripcionProducto = view.findViewById(R.id.etDescripcionProducto)
        val tilDescripcion = etDescripcionProducto.parent.parent as? TextInputLayout
        val etSKU = view.findViewById<TextInputEditText>(R.id.etSKU)
        val etUbicacion = view.findViewById<TextInputEditText>(R.id.etUbicacion)
        val autoCompleteCategoria = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteCategoria)
        val btnMenosStock = view.findViewById<TextView>(R.id.btnMenosStock)
        val btnMasStock = view.findViewById<TextView>(R.id.btnMasStock)
        val tvQuantityStock = view.findViewById<TextView>(R.id.tvCantidadStock)
        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardarProducto)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelar)

        val skuRecibido = arguments?.getString("sku_enviado_escaner")
        if (!skuRecibido.isNullOrEmpty()) etSKU.setText(skuRecibido)

        autoCompleteCategoria.setAdapter(ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriasPreestablecidas))
        autoCompleteCategoria.setText(categoriasPreestablecidas[0], false)

        tilDescripcion?.setEndIconOnClickListener { activarDictadoPorVoz() }
        btnMasStock.setOnClickListener { cantidadStock++; tvQuantityStock.text = cantidadStock.toString() }
        btnMenosStock.setOnClickListener { if (cantidadStock > 0) { cantidadStock--; tvQuantityStock.text = cantidadStock.toString() } }

        cardTomarFoto.setOnClickListener {
            AlertDialog.Builder(requireContext()).setItems(arrayOf("Cámara", "Galería")) { _, w ->
                if (w == 0) lanzarCamara.launch(null) else lanzarGaleria.launch("image/*")
            }.show()
        }

        btnGuardar.setOnClickListener {
            val nom = etNombreProducto.text.toString().trim()
            val des = etDescripcionProducto.text.toString().trim()
            val cod = etSKU.text.toString().trim()
            val ub = etUbicacion.text.toString().trim()
            val cat = autoCompleteCategoria.text.toString()

            if (nom.isEmpty() || cod.isEmpty()) {
                Toast.makeText(context, "Por favor rellena todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnGuardar.isEnabled = false
            Toast.makeText(context, "Guardando producto...", Toast.LENGTH_SHORT).show()

            // Convertimos la imagen a Base64 (Texto)
            val base64Image = imageBitmap?.let { bitmapToBase64(it) } ?: ""

            // AQUÍ CONECTAMOS CON TU ESTRUCTURA ORIGINAL
            val nuevoProducto = hashMapOf(
                "codigo" to cod,
                "nombre" to nom,
                "descripcion" to des,
                "categoria" to cat,
                "cantidad" to cantidadStock,
                "ubicacion" to ub,
                "imagenBase64" to base64Image,
                "timestamp" to Timestamp.now() // <--- CAMBIADO AQUÍ PARA QUE TU BASE DE DATOS VIEJA LO LEA PERFECTO
            )

            db.collection("productos").document(cod).set(nuevoProducto)
                .addOnSuccessListener {
                    Toast.makeText(context, "¡Guardado exitosamente!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(context, "Error BD: ${e.message}", Toast.LENGTH_LONG).show()
                    btnGuardar.isEnabled = true
                }
        }
        btnCancelar.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun activarDictadoPorVoz() {
        try { lanzarSpeechToText.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)) }
        catch (e: Exception) { Toast.makeText(context, "No disponible", Toast.LENGTH_SHORT).show() }
    }
}