package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import com.google.firebase.Timestamp

data class ReporteSeguridad(
    var idReporte: String = "",
    var titulo: String = "",
    var descripcion: String = "",
    var ubicacion: String = "",
    var fechaHora: Timestamp? = null,
    // Aquí sucederá la magia: la foto de la cámara guardada como texto puro
    var fotoBase64: String = ""
)