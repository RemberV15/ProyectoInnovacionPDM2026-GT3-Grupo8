package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.FileProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.button.MaterialButton
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ExportarBottomSheet : BottomSheetDialogFragment() {

    // 1. Instancia de base de datos
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.layout_bottom_sheet_exportar, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val btnVerListado = view.findViewById<MaterialButton>(R.id.btnVerListado)
        val btnEnviarReporte = view.findViewById<MaterialButton>(R.id.btnEnviarReporte)

        val etFechaInicio = view.findViewById<TextInputEditText>(R.id.etFechaInicio)
        val etFechaFin = view.findViewById<TextInputEditText>(R.id.etFechaFin)
        val etNombreArchivo = view.findViewById<TextInputEditText>(R.id.etNombreArchivo)
        val etCorreoDestino = view.findViewById<TextInputEditText>(R.id.etCorreoDestino)

        etFechaInicio.setOnClickListener {
            abrirCalendario(etFechaInicio, "Selecciona la fecha de inicio")
        }

        etFechaFin.setOnClickListener {
            abrirCalendario(etFechaFin, "Selecciona la fecha de fin")
        }

        btnVerListado.setOnClickListener {
            val strInicio = etFechaInicio.text.toString().trim()
            val strFin = etFechaFin.text.toString().trim()

            // Confirmación visual de lo que la app está a punto de mandar a Firebase
            Toast.makeText(context, "Filtro: [$strInicio] al [$strFin]", Toast.LENGTH_SHORT).show()

            var query: Query = db.collection("productos")
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            try {
                if (strInicio.isNotEmpty()) {
                    val dateInicio = formatoFecha.parse(strInicio)
                    if (dateInicio != null) {
                        // Conversión explícita a Timestamp de Firebase
                        query = query.whereGreaterThanOrEqualTo("timestamp", com.google.firebase.Timestamp(dateInicio))
                    }
                }

                if (strFin.isNotEmpty()) {
                    val dateFin = formatoFecha.parse(strFin)
                    if (dateFin != null) {
                        // Uso de Calendar para sumar el día exacto de forma segura
                        val cal = java.util.Calendar.getInstance()
                        cal.time = dateFin
                        cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
                        cal.add(java.util.Calendar.MILLISECOND, -1) // Hasta las 23:59:59

                        query = query.whereLessThanOrEqualTo("timestamp", com.google.firebase.Timestamp(cal.time))
                    }
                }

                query.get()
                    .addOnSuccessListener { snapshot ->
                        val listaProductos = snapshot.toObjects(Producto::class.java)

                        if (listaProductos.isNotEmpty()) {

                            val scrollView = android.widget.ScrollView(requireContext())
                            val tableLayout = android.widget.TableLayout(requireContext()).apply {
                                setPadding(60, 30, 60, 20)
                                isStretchAllColumns = true
                            }

                            val headerRow = android.widget.TableRow(requireContext())
                            headerRow.addView(android.widget.TextView(requireContext()).apply {
                                text = "Producto"
                                setTypeface(null, android.graphics.Typeface.BOLD)
                                setPadding(0, 0, 30, 20)
                            })
                            headerRow.addView(android.widget.TextView(requireContext()).apply {
                                text = "Cantidad"
                                setTypeface(null, android.graphics.Typeface.BOLD)
                                setPadding(0, 0, 0, 20)
                            })
                            tableLayout.addView(headerRow)

                            for (producto in listaProductos) {
                                val row = android.widget.TableRow(requireContext())

                                row.addView(android.widget.TextView(requireContext()).apply {
                                    text = producto.nombre ?: "Sin nombre"
                                    setPadding(0, 10, 30, 10)
                                })

                                row.addView(android.widget.TextView(requireContext()).apply {
                                    text = producto.cantidad.toString()
                                    setPadding(0, 10, 0, 10)
                                })

                                tableLayout.addView(row)
                            }

                            scrollView.addView(tableLayout)

                            android.app.AlertDialog.Builder(requireContext())
                                .setTitle("Vista Previa (${listaProductos.size} resultados)")
                                .setView(scrollView)
                                .setPositiveButton("Entendido", null)
                                .show()

                        } else {
                            android.app.AlertDialog.Builder(requireContext())
                                .setTitle("Sin resultados")
                                .setMessage("No hay productos guardados en este rango de fechas.")
                                .setPositiveButton("Cerrar", null)
                                .show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al obtener datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            } catch (e: Exception) {
                Toast.makeText(context, "Error con el formato de fechas", Toast.LENGTH_SHORT).show()
            }
        }

        btnEnviarReporte.setOnClickListener {
            val nombre = etNombreArchivo.text.toString().trim()
            val correo = etCorreoDestino.text.toString().trim()
            val strInicio = etFechaInicio.text.toString().trim()
            val strFin = etFechaFin.text.toString().trim()

            if (nombre.isEmpty() || correo.isEmpty()) {
                Toast.makeText(context, "El nombre del archivo y el correo son obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Toast.makeText(context, "Consultando base de datos...", Toast.LENGTH_SHORT).show()

            // 2. Prepara la consulta base
            var query: Query = db.collection("productos")

            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            try {
                // 3. Aplica filtros de fecha si el usuario las seleccionó
                if (strInicio.isNotEmpty()) {
                    val fechaInicio = formatoFecha.parse(strInicio)
                    if (fechaInicio != null) {
                        query = query.whereGreaterThanOrEqualTo("timestamp", fechaInicio)
                    }
                }

                if (strFin.isNotEmpty()) {
                    var fechaFin = formatoFecha.parse(strFin)
                    if (fechaFin != null) {
                        // Se agrega un día entero (menos un milisegundo) para abarcar hasta las 23:59:59 del día seleccionado
                        fechaFin = Date(fechaFin.time + (1000 * 60 * 60 * 24) - 1)
                        query = query.whereLessThanOrEqualTo("timestamp", fechaFin)
                    }
                }

                // 4. Ejecuta la consulta en Firebase
                query.get()
                    .addOnSuccessListener { snapshot ->
                        val listaProductos = snapshot.toObjects(Producto::class.java)

                        if (listaProductos.isNotEmpty()) {
                            // Si encontró datos, genera el Excel dinámicamente
                            generarYEnviarExcel(nombre, correo, listaProductos)
                        } else {
                            Toast.makeText(context, "No se encontraron productos en ese rango de fechas", Toast.LENGTH_SHORT).show()
                        }
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(context, "Error al obtener datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    }

            } catch (e: Exception) {
                Toast.makeText(context, "Error con el formato de fechas", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun abrirCalendario(campoTexto: TextInputEditText, titulo: String) {
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(titulo)
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val formatoFecha = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

            // Definir zona horaria para evitar desfase
            formatoFecha.timeZone = java.util.TimeZone.getTimeZone("UTC")

            val fechaFormateada = formatoFecha.format(Date(selection))
            campoTexto.setText(fechaFormateada)
        }

        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")
    }

    // 5. La función ahora recibe la lista filtrada de la base de datos
    private fun generarYEnviarExcel(nombreArchivo: String, correoDestino: String, listaProductos: List<Producto>) {
        try {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Reporte de Inventario")

            // Encabezados
            val headerRow = sheet.createRow(0)
            headerRow.createCell(0).setCellValue("Código/SKU")
            headerRow.createCell(1).setCellValue("Nombre del Producto")
            headerRow.createCell(2).setCellValue("Cantidad")
            headerRow.createCell(3).setCellValue("Ubicación")

            // Llenado dinámico leyendo el modelo Producto
            for ((index, producto) in listaProductos.withIndex()) {
                val row = sheet.createRow(index + 1)

                // Mapeando las propiedades exactas de  Producto.kt
                row.createCell(0).setCellValue(producto.codigo)
                row.createCell(1).setCellValue(producto.nombre)
                row.createCell(2).setCellValue(producto.cantidad.toString())
                row.createCell(3).setCellValue(producto.ubicacion)
            }

            val docsFolder = File(requireContext().cacheDir, "docs")
            if (!docsFolder.exists()) docsFolder.mkdirs()

            val archivoExcel = File(docsFolder, "$nombreArchivo.xlsx")
            val fileOut = FileOutputStream(archivoExcel)
            workbook.write(fileOut)
            fileOut.close()
            workbook.close()

            val uriSegura = FileProvider.getUriForFile(
                requireContext(),
                "${requireContext().packageName}.provider",
                archivoExcel
            )

            // 5. Crear el Intent para enviar el correo
            val intentCorreo = Intent(Intent.ACTION_SEND).apply {
                // Este tipo ("message/rfc822") fuerza a Android a mostrar SOLO apps de correo
                type = "message/rfc822"
                putExtra(Intent.EXTRA_EMAIL, arrayOf(correoDestino))
                putExtra(Intent.EXTRA_SUBJECT, "Reporte de Inventario: $nombreArchivo")
                putExtra(Intent.EXTRA_TEXT, "Adjunto encontrarás el reporte de inventario generado correctamente desde la aplicación.")
                putExtra(Intent.EXTRA_STREAM, uriSegura)

                // Mantiene el permiso de lectura del archivo
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                // Mantiene tu app y el correo separados para que se envíe de inmediato
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(intentCorreo, "Enviar reporte vía...")
            startActivity(chooser)
            dismiss() // Cierra el panel inferior automáticamente


        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Error al generar Excel: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }
}