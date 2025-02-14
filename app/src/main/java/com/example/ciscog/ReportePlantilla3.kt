package com.example.ciscog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.MultiFactorInfo
import com.google.firebase.auth.MultiFactorSession
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorGenerator
import java.util.concurrent.TimeUnit

class ReportePlantilla3 : AppCompatActivity() {
    private var btnRegresar: ImageButton? = null
    private var btnDescarga: ImageButton? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_plantilla_3)

        init()
        listeners()
    }

    private fun init() {
        btnRegresar= findViewById(R.id.return_btn)
        btnDescarga = findViewById(R.id.descarga_btn)
    }

    private fun listeners() {
        // Listener para el botón de regresar, que regresa a la actividad anterior
        btnRegresar?.setOnClickListener {
            finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
        }

        // Listener para el botón de descarga, que muestra un mensaje de descarga
        btnDescarga?.setOnClickListener {
            Toast.makeText(this, "Reporte Descargado en Formato PDF", Toast.LENGTH_SHORT).show()
        }
    }
}
