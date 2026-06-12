package com.example.proyectoinnovacionpdm2026_gt3_grupo8

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import com.google.android.material.textfield.TextInputEditText

class PinActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pin)

        val sharedPreferences = getSharedPreferences("AppPref", Context.MODE_PRIVATE)

        val esFallback = sharedPreferences.getBoolean("ES_FALLBACK", false)

        val pinGuardado = if (esFallback) {
            "3475"
        } else {
            sharedPreferences.getString("USER_PIN", null)
        }

        val ivLogo = findViewById<ImageView>(R.id.iv_pin_logo)
        val tvTitulo = findViewById<TextView>(R.id.tv_pin_titulo)
        val tvSubtitulo = findViewById<TextView>(R.id.tv_pin_subtitulo)
        val etPin = findViewById<TextInputEditText>(R.id.et_pin)
        val btnContinuar = findViewById<Button>(R.id.btn_continuar_pin)
        ivLogo.setImageResource(R.drawable.centinela1)

        // Adaptamos los textos según el caso
        if (pinGuardado != null) {
            if (esFallback) {
                tvTitulo.text = "Acceso Alternativo"
                tvSubtitulo.text = "Ingresa el PIN de respaldo"
            } else {
                tvTitulo.text = "Iniciar Sesión"
                tvSubtitulo.text = "Ingresa tu PIN para continuar"
            }
            btnContinuar.text = "Ingresar"
        } else {
            tvTitulo.text = "Crear PIN de Acceso"
            tvSubtitulo.text = "Configura un PIN de 4 dígitos"
            btnContinuar.text = "Guardar PIN"
        }

        etPin.addTextChangedListener { text ->
            if (text?.length == 4) {
                btnContinuar.performClick()
            }
        }

        btnContinuar.setOnClickListener {
            val ingresado = etPin.text.toString().trim()

            if (ingresado.length < 4) {
                Toast.makeText(this, "El PIN debe ser de 4 dígitos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (esFallback) {
                if (ingresado == "3475") {
                    Toast.makeText(this, "Acceso alternativo concedido", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "PIN de respaldo incorrecto", Toast.LENGTH_SHORT).show()
                    etPin.setText("")
                }
            } else {

                if (pinGuardado != null) {
                    if (ingresado == pinGuardado) {
                        Toast.makeText(this, "Acceso concedido", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "PIN incorrecto", Toast.LENGTH_SHORT).show()
                        etPin.setText("")
                    }
                } else {

                    sharedPreferences.edit().putString("USER_PIN", ingresado).apply()
                    Toast.makeText(this, "PIN guardado correctamente", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {

                sharedPreferences.edit().putBoolean("ES_FALLBACK", false).apply()

                val intent = Intent(this@PinActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
    }
}