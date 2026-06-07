package com.example.proyectoinnovacionpdm2026_gt3_grupo8 // Verifica tu paquete exacto

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.button.MaterialButton
import com.journeyapps.barcodescanner.DecoratedBarcodeView

class EscanerFragment : Fragment() {

    private var barcodeScannerView: DecoratedBarcodeView? = null
    private var isFlashOn = false
    private var camaraActivada = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_escanear, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        barcodeScannerView = view.findViewById(R.id.barcode_scanner)
        val btnFlash = view.findViewById<View>(R.id.btnFlass)
        val btnConfig = view.findViewById<View>(R.id.btnConfig)
        val btnConfirmar = view.findViewById<MaterialButton>(R.id.btnConfirmarProducto)
        val btnManual = view.findViewById<MaterialButton>(R.id.btnEntradaManual)

        btnFlash.setOnClickListener {
            if (camaraActivada) {
                if (isFlashOn) {
                    barcodeScannerView?.setTorchOff()
                    isFlashOn = false
                    Toast.makeText(context, "Linterna apagada", Toast.LENGTH_SHORT).show()
                } else {
                    barcodeScannerView?.setTorchOn()
                    isFlashOn = true
                    Toast.makeText(context, "Linterna encendida", Toast.LENGTH_SHORT).show()
                }
            }
        }

        btnConfig.setOnClickListener {
            Toast.makeText(context, "Filtros / Configuración", Toast.LENGTH_SHORT).show()
        }

        btnConfirmar.setOnClickListener {
            Toast.makeText(context, "Producto confirmado", Toast.LENGTH_SHORT).show()
        }

        btnManual.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()?.apply {
                replace(R.id.content_container, AgregarProductoFragment())
                addToBackStack(null)
                commit()
            }
        }
    }

    private fun iniciarEscaneoSeguro() {
        if (camaraActivada) return

        // Configuramos el detector continuo de códigos de barra
        barcodeScannerView?.decodeContinuous { result ->
            result.text?.let { codigoEscaneado ->
                barcodeScannerView?.pause() // Pausa inmediata para procesar la lectura sin duplicados

                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Código detectado: $codigoEscaneado", Toast.LENGTH_LONG).show()

                    //Aquí implementamos la búsqueda en Supabase con el 'codigoEscaneado'

                }
            }
        }

        // Despierta el hardware de la cámara de forma segura
        barcodeScannerView?.resume()
        camaraActivada = true
    }

    override fun onResume() {
        super.onResume()
        view?.postDelayed({
            if (isAdded) {
                iniciarEscaneoSeguro()
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