package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
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
import android.widget.Filter
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
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import java.io.ByteArrayOutputStream

class AgregarProductoFragment : Fragment() {

    private var cantidadStock = 0
    private val db = FirebaseFirestore.getInstance()
    private lateinit var etDescripcionProducto: TextInputEditText
    private lateinit var etSKU: TextInputEditText
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

    private val lanzarEscanerSKU = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            etSKU.setText(result.contents) // Rellena el número automáticamente con el código exacto
            Toast.makeText(context, "Código escaneado", Toast.LENGTH_SHORT).show()
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

        etSKU = view.findViewById(R.id.etSKU)
        val tilSKU = view.findViewById<TextInputLayout>(R.id.tilSKU)

        val etUbicacion = view.findViewById<TextInputEditText>(R.id.etUbicacion)
        val autoCompleteCategoria = view.findViewById<AutoCompleteTextView>(R.id.autoCompleteCategoria)
        val btnMenosStock = view.findViewById<TextView>(R.id.btnMenosStock)
        val btnMasStock = view.findViewById<TextView>(R.id.btnMasStock)
        val tvQuantityStock = view.findViewById<TextView>(R.id.tvCantidadStock)
        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardarProducto)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelar)

        val skuRecibido = arguments?.getString("sku_enviado_escaner")
        if (!skuRecibido.isNullOrEmpty()) etSKU.setText(skuRecibido)

        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriasPreestablecidas) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        return FilterResults().apply {
                            values = categoriasPreestablecidas
                            count = categoriasPreestablecidas.size
                        }
                    }
                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                        notifyDataSetChanged()
                    }
                }
            }
        }
        autoCompleteCategoria.setAdapter(adapter)
        autoCompleteCategoria.setText(categoriasPreestablecidas[0], false)

        tilDescripcion?.setEndIconOnClickListener { activarDictadoPorVoz() }

        tilSKU?.setEndIconOnClickListener {
            val opciones = ScanOptions()
            opciones.setPrompt("Apunta la cámara al código de barras del producto")
            opciones.setBeepEnabled(true)
            opciones.setOrientationLocked(false)
            lanzarEscanerSKU.launch(opciones)
        }

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
            val cod = etSKU.text.toString().trim() // Mantenemos el código exacto
            val ub = etUbicacion.text.toString().trim()
            val cat = autoCompleteCategoria.text.toString().trim()

            if (nom.isEmpty() || cod.isEmpty() || cat.isEmpty()) {
                Toast.makeText(context, "⚠️ Por favor rellena el nombre, SKU y categoría", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            if (cod.contains("/")) {
                Toast.makeText(context, "⚠️ El SKU no puede ser un enlace web (no debe contener '/'). Escanea un código de producto válido.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            btnGuardar.isEnabled = false
            Toast.makeText(context, "Guardando producto...", Toast.LENGTH_SHORT).show()

            val base64Image = imageBitmap?.let { bitmapToBase64(it) } ?: ""

            val nuevoProducto = hashMapOf(
                "codigo" to cod,
                "nombre" to nom,
                "descripcion" to des,
                "categoria" to cat,
                "cantidad" to cantidadStock,
                "ubicacion" to ub,
                "imagenBase64" to base64Image,
                "timestamp" to Timestamp.now()
            )

            db.collection("productos").document(cod).set(nuevoProducto)
                .addOnSuccessListener {
                    Toast.makeText(context, "¡Guardado exitosamente!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                }
                .addOnFailureListener { e ->
                    val msjError = if (e.message?.contains("Unable to resolve host") == true) {
                        "Sin conexión a Internet. Revisa tu red y vuelve a intentar."
                    } else {
                        "Error BD: ${e.message}"
                    }
                    Toast.makeText(context, msjError, Toast.LENGTH_LONG).show()
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