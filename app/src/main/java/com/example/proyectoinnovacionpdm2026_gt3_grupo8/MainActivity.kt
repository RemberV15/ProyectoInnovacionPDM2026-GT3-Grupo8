package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.graphics.Color
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.core.view.WindowCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnInicio: LinearLayout
    private lateinit var btnStock: LinearLayout
    private lateinit var btnEscanear: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {

        //Activar Edge-to-Edge ANTES de setContentView
        WindowCompat.setDecorFitsSystemWindows(window, false)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btnInicio = findViewById(R.id.nav_inicio)
        btnStock = findViewById(R.id.nav_stock)
        btnEscanear = findViewById(R.id.nav_escanear)

        if (savedInstanceState == null) {
            navegar(InicioFragment(), R.id.nav_inicio)
        }

        btnInicio.setOnClickListener { navegar(InicioFragment(), R.id.nav_inicio) }
        btnStock.setOnClickListener { navegar(StockFragment(), R.id.nav_stock) }
        btnEscanear.setOnClickListener { navegar(EscanerFragment(), R.id.nav_escanear) }
    }

    private fun navegar(fragmento: Fragment, idBoton: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.content_container, fragmento)
            .commit()
        actualizarVisual(idBoton)
    }

    private fun actualizarVisual(idSeleccionado: Int) {
        // Asegúrate de que este orden sea EXACTAMENTE el mismo que en tu XML
        val stock = Triple(btnStock, findViewById<ImageView>(R.id.iv_stock), findViewById<TextView>(R.id.tv_stock))
        val inicio = Triple(btnInicio, findViewById<ImageView>(R.id.iv_inicio), findViewById<TextView>(R.id.tv_inicio))
        val escanear = Triple(btnEscanear, findViewById<ImageView>(R.id.iv_escanear), findViewById<TextView>(R.id.tv_escanear))

        val botones = listOf(stock, inicio, escanear)

        val colorActivo = getColor(R.color.primary_blue)
        val colorInactivo = Color.parseColor("#757575")

        botones.forEach { (boton, icono, texto) ->
            val seleccionado = (boton.id == idSeleccionado)

            if (seleccionado) {
                boton.setBackgroundResource(R.drawable.bg_nav_selected)
                icono.setColorFilter(Color.WHITE)
                texto.setTextColor(Color.WHITE)
            } else {
                boton.setBackgroundResource(0) // 0 significa sin fondo
                icono.setColorFilter(colorInactivo)
                texto.setTextColor(colorInactivo)
            }
        }
    }
}