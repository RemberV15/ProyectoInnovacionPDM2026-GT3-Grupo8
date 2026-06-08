package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import com.google.firebase.Timestamp

data class Producto(
    val codigo: String = "",
    val nombre: String = "",
    val categoria: String = "",
    val cantidad: Int = 0,
    val ubicacion: String = "",
    val imagen_url: String = "",
    val timestamp: Timestamp? = null
)