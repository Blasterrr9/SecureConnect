package com.example.ciscog

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity


class RespaldoNube : AppCompatActivity() {
    private var btnRegresar: ImageButton? = null
    private var btnRespaldar: Button? = null
    private var textLink: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_respaldo_nube)

        init()
        listeners()
    }

    private fun init() {
        btnRegresar= findViewById(R.id.return_btn)
        btnRespaldar = findViewById(R.id.btnRespaldar)
        textLink = findViewById(R.id.textLink)
    }

    private fun listeners() {
        // Listener para el botón de regresar, que regresa a la actividad anterior
        btnRegresar?.setOnClickListener {
            finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
        }

        // Listener para el botón de respaldar, que muestra un mensaje de respaldo generado exitosamente
        btnRespaldar?.setOnClickListener {
            Toast.makeText(this, "Respaldo Subido a Firebase Exitosamente", Toast.LENGTH_SHORT).show()
            textLink?.setText("https://firebasestorage.googleapis.com/vO/b/firestoredemo97039xappspotcomLo/backupdb%2SD-15548vd-898d")
        }
    }
}
