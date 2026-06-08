package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class EscanerFragment : Fragment() {

    private var barcodeScannerView: DecoratedBarcodeView? = null
    private var isFlashOn = false
    private var camaraActivada = false
    private val db = FirebaseFirestore.getInstance()
    private var codigoDetectadoActual: String = ""

    private val solicitarPermisoCamaraLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { esConcedido ->
        if (esConcedido) {
            iniciarEscaneoSeguro()
        } else {
            Toast.makeText(
                context,
                "Se requiere el permiso de la cámara para poder escanear códigos de barras.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_escanear, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeScannerView = view.findViewById(R.id.barcode_scanner)

        val btnFlash = view.findViewById<ImageButton>(R.id.btnFlass)
        val btnConfig = view.findViewById<View>(R.id.btnConfig)
        val btnConfirmar = view.findViewById<MaterialButton>(R.id.btnConfirmarProducto)

        btnFlash.setOnClickListener {
            if (camaraActivada) {
                if (isFlashOn) {
                    barcodeScannerView?.setTorchOff()
                    isFlashOn = false

                    btnFlash.setImageResource(R.drawable.ic_linterna_off)

                } else {
                    barcodeScannerView?.setTorchOn()
                    isFlashOn = true

                    btnFlash.setImageResource(R.drawable.ic_linterna_on)

                }
            }
        }

        btnConfig.setOnClickListener {
            Toast.makeText(context, "Filtros / Configuración", Toast.LENGTH_SHORT).show()
        }

        btnConfirmar.setOnClickListener {
            if (codigoDetectadoActual.isNotEmpty()) {
                verificarYProcederConProducto(codigoDetectadoActual)
            } else {
                Toast.makeText(context, "Por favor escanea un código primero", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun verificarPermisosYEncenderCamara() {
        context?.let { ctx ->
            val estadoPermiso = ContextCompat.checkSelfPermission(ctx, Manifest.permission.CAMERA)
            if (estadoPermiso == PackageManager.PERMISSION_GRANTED) {
                iniciarEscaneoSeguro()
            } else {
                solicitarPermisoCamaraLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun iniciarEscaneoSeguro() {
        if (camaraActivada) return

        barcodeScannerView?.decodeContinuous { result ->
            result.text?.let { codigoEscaneado ->
                codigoDetectadoActual = codigoEscaneado
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Código detectado: $codigoEscaneado. Presiona Confirmar.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        barcodeScannerView?.barcodeView?.framingRectSize = com.journeyapps.barcodescanner.Size(0, 0)
        barcodeScannerView?.setStatusText("")

        barcodeScannerView?.resume()
        camaraActivada = true
    }

    private fun verificarYProcederConProducto(codigo: String) {
        barcodeScannerView?.pause()

        db.collection("productos").document(codigo).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val producto = document.toObject(Producto::class.java)
                    if (producto != null) {
                        val dialog = DetalleProductoDialog.newInstance(producto)
                        dialog.show(requireActivity().supportFragmentManager, DetalleProductoDialog.TAG)
                    }
                    barcodeScannerView?.resume()
                } else {
                    Toast.makeText(context, "Código nuevo detectado. Abriendo gestor...", Toast.LENGTH_LONG).show()
                    navegarAAgregarProductoConSKU(codigo)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error de red: ${e.message}", Toast.LENGTH_SHORT).show()
                barcodeScannerView?.resume()
            }
    }

    private fun navegarAAgregarProductoConSKU(sku: String) {
        val fragmentoAgregar = AgregarProductoFragment().apply {
            arguments = Bundle().apply {
                putString("sku_enviado_escaner", sku)
            }
        }

        activity?.supportFragmentManager?.beginTransaction()?.apply {
            replace(R.id.content_container, fragmentoAgregar)
            addToBackStack(null)
            commit()
        }
    }

    override fun onResume() {
        super.onResume()
        view?.postDelayed({
            if (isAdded) {
                verificarPermisosYEncenderCamara()
            }
        }, 250)
    }

    override fun onPause() {
        super.onPause()
        if (camaraActivada) {
            barcodeScannerView?.pause()
            camaraActivada = false
        }
        isFlashOn = false
    }
}