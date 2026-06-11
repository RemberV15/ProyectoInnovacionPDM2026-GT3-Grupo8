package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
    private lateinit var etNombreProducto: TextInputEditText
    private lateinit var etDescripcionProducto: TextInputEditText
    private lateinit var etSKU: TextInputEditText
    private lateinit var etUbicacion: TextInputEditText
    private lateinit var etProveedor: TextInputEditText
    private lateinit var autoCompleteCategoria: AutoCompleteTextView
    private lateinit var tvQuantityStock: TextView
    private lateinit var ivProductoPreview: ImageView
    private var imageBitmap: Bitmap? = null
    private lateinit var cardTomarFoto: MaterialCardView

    private var codigoAEditar: String? = null

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
            imageBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(ImageDecoder.createSource(requireContext().contentResolver, uri))
            } else {
                @Suppress("DEPRECATION")
                MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
            }
            ivProductoPreview.setImageBitmap(imageBitmap)
        }
    }

    private val lanzarEscanerSKU = registerForActivityResult(ScanContract()) { result ->
        if (result.contents != null) {
            etSKU.setText(result.contents)
            Toast.makeText(context, "Código escaneado", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_agregar_producto, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tvTituloFormulario = view.findViewById<TextView>(R.id.tvTituloFormulario)
        cardTomarFoto = view.findViewById(R.id.cardTomarFoto)
        ivProductoPreview = view.findViewById(R.id.ivProductoPreview)
        etNombreProducto = view.findViewById(R.id.etNombreProducto)
        etDescripcionProducto = view.findViewById(R.id.etDescripcionProducto)
        val tilDescripcion = etDescripcionProducto.parent.parent as? TextInputLayout
        etSKU = view.findViewById(R.id.etSKU)
        val tilSKU = view.findViewById<TextInputLayout>(R.id.tilSKU)
        etUbicacion = view.findViewById(R.id.etUbicacion)
        etProveedor = view.findViewById(R.id.etProveedor)
        autoCompleteCategoria = view.findViewById(R.id.autoCompleteCategoria)
        val btnMenosStock = view.findViewById<TextView>(R.id.btnMenosStock)
        val btnMasStock = view.findViewById<TextView>(R.id.btnMasStock)
        tvQuantityStock = view.findViewById(R.id.tvCantidadStock)
        val btnGuardar = view.findViewById<MaterialButton>(R.id.btnGuardarProducto)
        val btnCancelar = view.findViewById<MaterialButton>(R.id.btnCancelar)

        val adapter = object : ArrayAdapter<String>(requireContext(), android.R.layout.simple_dropdown_item_1line, categoriasPreestablecidas) {
            override fun getFilter(): Filter {
                return object : Filter() {
                    override fun performFiltering(constraint: CharSequence?): FilterResults {
                        return FilterResults().apply { values = categoriasPreestablecidas; count = categoriasPreestablecidas.size }
                    }
                    override fun publishResults(constraint: CharSequence?, results: FilterResults?) { notifyDataSetChanged() }
                }
            }
        }
        autoCompleteCategoria.setAdapter(adapter)

        codigoAEditar = arguments?.getString("ARG_CODIGO_EDITAR")
        if (codigoAEditar != null) {
            tvTituloFormulario.setText(R.string.titulo_editar_producto)
            btnGuardar.setText(R.string.btn_actualizar)
            tilSKU.isEnabled = false
            tilSKU.setEndIconOnClickListener(null)
            cargarDatosDesdeFirebase(codigoAEditar!!)
        } else {
            autoCompleteCategoria.setText(categoriasPreestablecidas[0], false)
            val skuRecibido = arguments?.getString("sku_enviado_escaner")
            if (!skuRecibido.isNullOrEmpty()) etSKU.setText(skuRecibido)
            tilSKU.setEndIconOnClickListener {
                val opciones = ScanOptions().apply { setPrompt("Apunta la cámara al código de barras"); setBeepEnabled(true); setOrientationLocked(false) }
                lanzarEscanerSKU.launch(opciones)
            }
        }

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
            val prov = etProveedor.text.toString().trim()
            val cat = autoCompleteCategoria.text.toString().trim()

            if (nom.isEmpty() || des.isEmpty() || cod.isEmpty() || ub.isEmpty() || prov.isEmpty() || cat.isEmpty()) {
                Toast.makeText(context, "⚠️ Debes rellenar TODOS los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (cod.contains("/")) {
                Toast.makeText(context, "⚠️ SKU inválido.", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            btnGuardar.isEnabled = false
            Toast.makeText(context, if (codigoAEditar != null) getString(R.string.msg_actualizando) else getString(R.string.msg_guardando), Toast.LENGTH_SHORT).show()

            val base64Image = imageBitmap?.let { bitmapToBase64(it) } ?: ""

            val productoData = hashMapOf<String, Any?>(
                "codigo" to cod,
                "nombre" to nom,
                "descripcion" to des,
                "categoria" to cat,
                "cantidad" to cantidadStock,
                "ubicacion" to ub,
                "proveedor" to prov,
                "imagenBase64" to base64Image,
                "timestamp" to Timestamp.now()
            )

            if (codigoAEditar == null) {
                db.collection("productos").document(cod).get().addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        Toast.makeText(context, "❌ Ya existe un producto con SKU $cod", Toast.LENGTH_LONG).show()
                        btnGuardar.isEnabled = true
                    } else {
                        guardarEnFirebase(cod, productoData)
                    }
                }
            } else {
                guardarEnFirebase(cod, productoData)
            }
        }
        btnCancelar.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun guardarEnFirebase(codigo: String, datos: HashMap<String, Any?>) {
        db.collection("productos").document(codigo).set(datos)
            .addOnSuccessListener {
                Toast.makeText(context, "¡Operación exitosa!", Toast.LENGTH_SHORT).show()
                parentFragmentManager.popBackStack()
            }
            .addOnFailureListener { _ ->
                Toast.makeText(context, "Error al guardar los datos", Toast.LENGTH_LONG).show()
                view?.findViewById<MaterialButton>(R.id.btnGuardarProducto)?.isEnabled = true
            }
    }

    private fun cargarDatosDesdeFirebase(idProducto: String) {
        db.collection("productos").document(idProducto).get().addOnSuccessListener { doc ->
            if (doc.exists()) {
                etNombreProducto.setText(doc.getString("nombre"))
                etDescripcionProducto.setText(doc.getString("descripcion"))
                etSKU.setText(doc.getString("codigo"))
                autoCompleteCategoria.setText(doc.getString("categoria"), false)
                etUbicacion.setText(doc.getString("ubicacion"))
                etProveedor.setText(doc.getString("proveedor") ?: "")
                cantidadStock = doc.getLong("cantidad")?.toInt() ?: 0
                tvQuantityStock.text = cantidadStock.toString()
                val img64 = doc.getString("imagenBase64")
                if (!img64.isNullOrEmpty()) {
                    val bytes = Base64.decode(img64, Base64.DEFAULT)
                    imageBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                    ivProductoPreview.setImageBitmap(imageBitmap)
                }
            }
        }.addOnFailureListener { _ ->
            Toast.makeText(context, "Error al descargar datos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun activarDictadoPorVoz() {
        try { lanzarSpeechToText.launch(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)) }
        catch (_: Exception) { Toast.makeText(context, "No disponible", Toast.LENGTH_SHORT).show() }
    }
}