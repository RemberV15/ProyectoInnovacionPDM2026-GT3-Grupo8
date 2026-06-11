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
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.BarcodeView

class EscanerFragment : Fragment() {

    private var barcodeScannerView: BarcodeView? = null
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
            Toast.makeText(context, "Permiso de cámara denegado.", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_escanear, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcodeScannerView = view.findViewById(R.id.barcode_scanner)
        val btnFlash = view.findViewById<ImageButton>(R.id.btnFlass)

        btnFlash.setOnClickListener {
            if (camaraActivada) {
                isFlashOn = !isFlashOn
                barcodeScannerView?.setTorch(isFlashOn)
                btnFlash.setImageResource(if (isFlashOn) R.drawable.ic_linterna_on else R.drawable.ic_linterna_off)
            }
        }
    }

    private fun iniciarEscaneoSeguro() {
        if (camaraActivada) return
        barcodeScannerView?.decodeContinuous { result ->
            result.text?.let { codigo ->
                if (codigo != codigoDetectadoActual) {
                    codigoDetectadoActual = codigo
                    barcodeScannerView?.pause()
                    requireActivity().runOnUiThread { verificarYProcederConProducto(codigo) }
                }
            }
        }
        barcodeScannerView?.resume()
        camaraActivada = true
    }

    private fun verificarYProcederConProducto(codigo: String) {
        if (isFlashOn) {
            barcodeScannerView?.setTorch(false)
            isFlashOn = false
        }
        barcodeScannerView?.pause()

        db.collection("productos").document(codigo).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val producto = document.toObject(Producto::class.java)
                    if (producto != null) {
                        val dialog = DetalleProductoDialog.newInstance(producto)

                        dialog.actionListener = object : DetalleProductoDialog.OnContextActionListener {
                            override fun onEditarSelected(codigoProducto: String) {
                                val fragmentoEdicion = AgregarProductoFragment()
                                val argumentos = Bundle()
                                argumentos.putString("ARG_CODIGO_EDITAR", codigoProducto)
                                fragmentoEdicion.arguments = argumentos

                                parentFragmentManager.beginTransaction()
                                    .replace(R.id.content_container, fragmentoEdicion)
                                    .addToBackStack(null)
                                    .commit()
                            }
                        }

                        dialog.dismissListener = object : DetalleProductoDialog.OnDismissListener {
                            override fun onDialogDismissed() {
                                codigoDetectadoActual = ""
                                barcodeScannerView?.resume()
                            }
                        }

                        dialog.show(parentFragmentManager, DetalleProductoDialog.TAG)
                    }
                } else {
                    Toast.makeText(context, "Producto no encontrado", Toast.LENGTH_SHORT).show()
                    codigoDetectadoActual = ""
                    barcodeScannerView?.resume()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                codigoDetectadoActual = ""
                barcodeScannerView?.resume()
            }
    }

    override fun onResume() {
        super.onResume()
        view?.postDelayed({
            if (isAdded) verificarPermisosYEncenderCamara()
        }, 250)
    }

    override fun onPause() {
        super.onPause()
        barcodeScannerView?.pause()
        camaraActivada = false
        codigoDetectadoActual = ""
    }

    private fun verificarPermisosYEncenderCamara() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            iniciarEscaneoSeguro()
        } else {
            solicitarPermisoCamaraLauncher.launch(Manifest.permission.CAMERA)
        }
    }
}