package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportarBottomSheet : BottomSheetDialogFragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet_exportar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 1. Vincular los botones
        val btnVerListado = view.findViewById<MaterialButton>(R.id.btnVerListado)
        val btnEnviarReporte = view.findViewById<MaterialButton>(R.id.btnEnviarReporte)

        // 2. Vincular los campos de texto de las fechas
        val etFechaInicio = view.findViewById<TextInputEditText>(R.id.etFechaInicio)
        val etFechaFin = view.findViewById<TextInputEditText>(R.id.etFechaFin)

        // 3. Configurar los clics para abrir el calendario
        etFechaInicio.setOnClickListener {
            abrirCalendario(etFechaInicio, "Selecciona la fecha de inicio")
        }

        etFechaFin.setOnClickListener {
            abrirCalendario(etFechaFin, "Selecciona la fecha de fin")
        }

        // 4. Configurar botones de acción
        btnVerListado.setOnClickListener {
            Toast.makeText(context, "Botón Ver Listado presionado", Toast.LENGTH_SHORT).show()
        }

        btnEnviarReporte.setOnClickListener {
            Toast.makeText(context, "Botón Enviar presionado", Toast.LENGTH_SHORT).show()
        }
    }

    // Función auxiliar para crear y mostrar el calendario de Material Design
    private fun abrirCalendario(campoTexto: TextInputEditText, titulo: String) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(titulo)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds()) // Selecciona hoy por defecto
            .build()

        // Cuando el usuario le da a "Aceptar" en el calendario
        datePicker.addOnPositiveButtonClickListener { selection ->
            // Convertimos los milisegundos a un formato de fecha legible
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val fechaFormateada = formatoFecha.format(Date(selection))

            // Colocamos la fecha formateada en el campo de texto
            campoTexto.setText(fechaFormateada)
        }

        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
    }
}