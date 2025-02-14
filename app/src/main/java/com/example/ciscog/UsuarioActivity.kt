package com.example.ciscog

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
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

class UsuarioActivity : AppCompatActivity() {
    private var btnRutasEst: Button? = null
    private var btnNat: Button? = null
    private var btnEigrp: Button? = null
    private var btnOspf: Button? = null
    private var btnVlan: Button? = null
    private var btnStp: Button? = null
    private var btnAcl: Button? = null
    private var btnUsuario: Button? = null
    private var btnAutenticacion: AppCompatImageButton? = null
    private var btnCerrarSesion: Button? = null
    private var tvNombreUsuario: TextView? = null
    private var tvCorreoUsuario: TextView? = null

    private lateinit var auth: FirebaseAuth
    private lateinit var verificationId: String
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null
    private lateinit var multiFactorSession: MultiFactorSession

    private lateinit var phone: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.usuario)

        auth = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")
        val nombre = intent.getStringExtra("nombre")
        val apellido = intent.getStringExtra("apellido")

        init(email.toString(), nombre.toString(), apellido.toString())
        listeners(email.toString(), nombre.toString(), apellido.toString())
    }

    private fun init(email: String, nombre: String, apellido: String) {
        btnNat = findViewById(R.id.btnNat)
        btnRutasEst = findViewById(R.id.btnRutasEst)
        btnEigrp = findViewById(R.id.btnEigrp)
        btnOspf = findViewById(R.id.btnOspf)
        btnVlan = findViewById(R.id.btnVlans)
        btnStp = findViewById(R.id.btnStp)
        btnAcl = findViewById(R.id.btnAcls)
        btnUsuario = findViewById(R.id.btnUsuarios)
        btnAutenticacion = findViewById(R.id.btnMFA)
        btnCerrarSesion = findViewById(R.id.cerrarSesion)
        tvCorreoUsuario = findViewById(R.id.correo_usuario)
        tvCorreoUsuario?.text = email
        tvNombreUsuario = findViewById(R.id.nombre_usuario)
        tvNombreUsuario?.text = "$nombre $apellido"
    }

    private fun listeners(email: String, nombre: String, apellido: String) {
        btnRutasEst?.setOnClickListener {
            val intent = Intent(this, ListaRutasEstReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
        }

        btnCerrarSesion?.setOnClickListener {
            auth.signOut()
            Toast.makeText(baseContext, "Sesión Finalizada Exitosamente", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        btnNat?.setOnClickListener {
            val intent = Intent(this, ListaNatReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
        }

        btnEigrp?.setOnClickListener {
            val intent = Intent(this, ListaEigrpReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
        }

        btnOspf?.setOnClickListener {
            val intent = Intent(this, ListaOspfReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
        }

        btnVlan?.setOnClickListener {
            val intent = Intent(this, ListaVlanReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
        }

        btnStp?.setOnClickListener {
            val intent = Intent(this, ListaStpReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
        }

        btnAcl?.setOnClickListener {
            val intent = Intent(this, ListaAclReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
        }

        btnUsuario?.setOnClickListener {
            val intent = Intent(this, ListaUsuarioReg::class.java).apply {
                putExtra("email", email)
                putExtra("nombre", nombre)
                putExtra("apellido", apellido)
            }
            startActivity(intent)
            finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
        }

        btnAutenticacion?.setOnClickListener {
            // Obtiene el usuario actual
            val user = auth.currentUser
            if (user != null) {
                // Verifica si el usuario tiene MFA habilitado
                val enrolledFactors = user.multiFactor.enrolledFactors
                if (enrolledFactors.isNotEmpty()) {
                    // El usuario tiene MFA habilitado, muestra el diálogo de gestión de MFA
                    showMfaManagementDialog(user, enrolledFactors[0])
                } else {
                    // El usuario no tiene MFA habilitado, muestra el diálogo de registro de MFA pero primero le pide que verifique su correo

                    if (user.isEmailVerified) {
                        showEnrollMfaDialog(user)
                    } else {
                        Toast.makeText(this, "Por favor, verifica tu correo antes de continuar.", Toast.LENGTH_SHORT).show()
                        user.sendEmailVerification().addOnCompleteListener { sendTask ->
                            if (sendTask.isSuccessful) {
                                Toast.makeText(this, "Correo de verificación enviado a ${user.email}", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this, "Error al enviar el correo de verificación.", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            } else {
                Toast.makeText(this, "No se pudo obtener el usuario actual.", Toast.LENGTH_SHORT).show()
            }
        }

    }

    // Funcion para mostrar el diálogo de gestión de MFA (Desactivar MFA)
    private fun showMfaManagementDialog(user: FirebaseUser, phoneFactor: MultiFactorInfo) {


        AlertDialog.Builder(this)
            .setTitle("Autenticación MFA")
            .setIcon(R.drawable.identificacion)
            .setMessage("Ya se encuentra habilitada la autenticación multifactor, ¿Qué deseas realizar?")

            .setPositiveButton("Cambiar número") { dialog, _ ->
                // Desenrollar el factor actual y reenrollar con un nuevo número de teléfono

                android.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar")
                    .setIcon(R.drawable.ic_warning)
                    .setMessage("¿Estas seguro de cambiar el número de teléfono?")
                    .setPositiveButton("Si"){ dialog,_->

                        user.multiFactor.unenroll(phoneFactor)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    if (user.isEmailVerified) {
                                        showEnrollMfaDialog(user)
                                    } else {
                                        Toast.makeText(this, "Por favor, verifica tu correo antes de continuar.", Toast.LENGTH_SHORT).show()
                                        user.sendEmailVerification().addOnCompleteListener { sendTask ->
                                            if (sendTask.isSuccessful) {
                                                Toast.makeText(this, "Correo de verificación enviado a ${user.email}", Toast.LENGTH_SHORT).show()
                                            } else {
                                                Toast.makeText(this, "Error al enviar el correo de verificación.", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    }
                                } else {
                                    Toast.makeText(this, "Error al cambiar el número de teléfono.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        dialog.dismiss()

                    }.setNegativeButton("No"){ dialog,_->
                        Toast.makeText(this, "Cambio de número de teléfono cancelado", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }

            .setNegativeButton("Desactivar") { dialog, _ ->
                // Desenrollar el factor MFA
                android.app.AlertDialog.Builder(this)
                    .setTitle("Eliminar")
                    .setIcon(R.drawable.ic_warning)
                    .setMessage("¿Estas seguro de desactivar la autenticación multifactor?")
                    .setPositiveButton("Si"){ dialog,_->
                        user.multiFactor.unenroll(phoneFactor)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(this, "MFA desactivado exitosamente.", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Error al desactivar MFA.", Toast.LENGTH_SHORT).show()
                                }
                            }
                        dialog.dismiss()
                    }.setNegativeButton("No"){ dialog,_->
                        Toast.makeText(this, "Desactivación de MFA cancelada", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                    }
                    .create()
                    .show()
            }
            .setNeutralButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            .create()
            .show()
    }

    // Funcion para mostrar el diálogo de registro de MFA
    private fun showEnrollMfaDialog(user: FirebaseUser) {
        val v = LayoutInflater.from(this).inflate(R.layout.edit_phone_dialog, null)

        val textViewEmailDialog: TextView = v.findViewById(R.id.textview_email_dialog)
        val editTextPhoneDialog: EditText = v.findViewById(R.id.edittext_phone_dialog)

        textViewEmailDialog.text = user.email

        android.app.AlertDialog.Builder(this)
            .setView(v)
            .setPositiveButton("Activar") { dialog, _ ->
                val phoneNumber = "+" + editTextPhoneDialog.text.toString()
                if (phoneNumber.isNotEmpty()) {
                    phone = phoneNumber
                    startMfaEnrollment(user, phoneNumber)
                } else {
                    Toast.makeText(this, "Por favor, ingrese un número de teléfono válido.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    // Funcion para iniciar el proceso de registro de MFA
    private fun startMfaEnrollment(user: FirebaseUser, phoneNumber: String) {
        user.multiFactor.session
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    multiFactorSession = task.result

                    val options = PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)
                        .setTimeout(60L, TimeUnit.SECONDS)
                        .setActivity(this)
                        .setMultiFactorSession(multiFactorSession)
                        .setCallbacks(callbacks)
                        .build()

                    PhoneAuthProvider.verifyPhoneNumber(options)
                } else {
                    Toast.makeText(this, "Error al iniciar sesión MFA.", Toast.LENGTH_SHORT).show()
                }
            }
    }

    // PhoneAuthProvider callbacks
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Verificación automática o completada
            enrollMfa(credential)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            if (e is FirebaseAuthInvalidCredentialsException) {
                Toast.makeText(this@UsuarioActivity, "Solicitud inválida.", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                Toast.makeText(this@UsuarioActivity, "Se excedió el límite de solicitudes.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@UsuarioActivity, "Error de verificación: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCodeSent(
            verificationId: String,
            token: PhoneAuthProvider.ForceResendingToken
        ) {
            this@UsuarioActivity.verificationId = verificationId
            this@UsuarioActivity.forceResendingToken = token
            showVerificationCodeDialog()
        }
    }

    // Function to show dialog to enter verification code
    @SuppressLint("MissingInflatedId")
    private fun showVerificationCodeDialog() {
        val v = LayoutInflater.from(this).inflate(R.layout.verify_phone_dialog, null)

        val textViewPhoneDialog: TextView = v.findViewById(R.id.textview_phone_dialog)
        val editTextPhoneDialog: EditText = v.findViewById(R.id.edittext_phone_dialog)

        textViewPhoneDialog.text = phone

        android.app.AlertDialog.Builder(this)
            .setView(v)
            .setPositiveButton("Verificar") {dialog, _ ->
                val code = editTextPhoneDialog.text.toString()
                if (code.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(verificationId, code)
                    enrollMfa(credential)
                } else {
                    Toast.makeText(this, "Ingrese el código de verificación.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }

            .setNegativeButton("Cancelar") { dialog, _ ->
                dialog.dismiss()
            }

            .show()
    }

    // Function to enroll MFA
    private fun enrollMfa(credential: PhoneAuthCredential) {
        val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
        val user = auth.currentUser

        user?.multiFactor?.enroll(multiFactorAssertion, "Número de teléfono")
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "MFA activado exitosamente.", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Error al activar MFA.", Toast.LENGTH_SHORT).show()
                }
            }
    }
}
