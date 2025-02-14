package com.example.ciscog

import android.content.Context
import android.content.Intent
<<<<<<< HEAD
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
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthMultiFactorException
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.MultiFactorAssertion
import com.google.firebase.auth.MultiFactorResolver
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import com.google.firebase.auth.PhoneMultiFactorGenerator
import com.google.firebase.auth.PhoneMultiFactorInfo
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    // Maneja la autenticación de Firebase
    private lateinit var autorizar: FirebaseAuth
    private lateinit var multiFactorResolver: MultiFactorResolver
    private lateinit var verificationId: String
    private var forceResendingToken: PhoneAuthProvider.ForceResendingToken? = null

    private var btnResp: ImageButton? = null

=======
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    // Maneja la autenticación de Firebase
    lateinit var autorizar: FirebaseAuth
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa la instancia de FirebaseAuth
        autorizar = FirebaseAuth.getInstance()

        // Asocia los elementos de la interfaz con sus respectivas vistas en el layout
        val email = findViewById<EditText>(R.id.editEmail) // Campo de texto para el email
        val pass = findViewById<EditText>(R.id.editPassword) // Campo de texto para la contraseña
        val btnIniciar = findViewById<Button>(R.id.btnIniciarSesion) // Botón para iniciar sesión
        val btnRecuperarContra = findViewById<Button>(R.id.btnRecuperarContra) // Botón para recuperar contraseña

<<<<<<< HEAD
        btnResp = findViewById(R.id.btnResp)

        //Prueba

        pruebaConexion()

=======
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
        // Define la acción al hacer clic en el botón de iniciar sesión
        btnIniciar.setOnClickListener {
            val emailText = email.text.toString()
            val passText = pass.text.toString()

            // Valida que los campos no estén vacíos
            if (emailText.isBlank() || passText.isBlank()) {
                mostrarAdvertencia("Por favor, ingresa el correo electrónico y la contraseña.")
            } else {
                iniciarsesion(emailText, passText)
            }
        }

<<<<<<< HEAD
        btnResp?.setOnClickListener {
            android.app.AlertDialog.Builder(this)
                .setTitle("Restauración de la Información")
                .setIcon(R.drawable.config_resp)
                .setMessage("¿Deseas realizar la restauración de la base de datos en la aplicación?. Esto modificará los datos existentes en la aplicación")

                .setPositiveButton("Si") { dialog, _ ->
                    // Realizar la restaruación de la base de datos
                    openFileLauncher.launch(arrayOf("*/*"))
                    dialog.dismiss()
                }

                .setNegativeButton("No") { dialog, _ ->
                    Toast.makeText(this, "Restauración cancelada", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }

                .create()
                .show()
        }

=======
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
        // Define la acción al hacer clic en el botón de recuperar contraseña
        btnRecuperarContra?.setOnClickListener {
            if (email?.text.toString().isNotEmpty()){
                autorizar.setLanguageCode("es")
                autorizar.sendPasswordResetEmail(email?.text.toString()).addOnCompleteListener{ task ->
                    if (task.isSuccessful){
                        Toast.makeText(this,"Se envio un correo para restablecer su contraseña",Toast.LENGTH_SHORT).show()
                    }else{
                        Toast.makeText(this,"Error al enviar el correo",Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(this,"Ingrese un correo válido para enviar las instrucciones",Toast.LENGTH_SHORT).show()
            }
        }
    }

<<<<<<< HEAD
    // Registrador de resultado para manejar la selección de archivos
    private val openFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            // Restaurar la base de datos desde el archivo seleccionado
            restoreDatabase(it)
        } ?: run {
            Toast.makeText(this, "No se seleccionó ningún archivo", Toast.LENGTH_SHORT).show()
        }
    }

    // Función que realiza la restauración de la base de datos desde el archivo seleccionado
    private fun restoreDatabase(fileUri: Uri) {
        val db = DataBaseLITE(this)
        try {
            db.restoreDatabaseFromSQLFile(this, fileUri)
            Snackbar.make(findViewById(android.R.id.content), "Restauración Exitosa", Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Snackbar.make(findViewById(android.R.id.content), "Error al restaurar base de datos", Snackbar.LENGTH_LONG).show()
        }
    }

    private fun pruebaConexion() {
        val host = "192.168.56.102" // Dirección IP del router virtual en GNS3
        val port = 5022        // Puerto Telnet del router

        try {
            // Crear conexión al router
            val socket = Socket(host, port)
            //Toast.makeText(baseContext, "Conexión establecida con $host:$port", Toast.LENGTH_SHORT).show()
            println("Conexión establecida con $host:$port")

            // Streams para enviar y recibir datos
            val input = BufferedReader(InputStreamReader(socket.getInputStream()))
            val output = PrintWriter(socket.getOutputStream(), true)

            // Ejemplo: enviar un comando al router
            output.println("show running-config") // Comando Telnet
            output.flush()

            // Leer la respuesta del router
            var line: String?
            while (input.readLine().also { line = it } != null) {
                println(line) // Imprimir la respuesta en consola
            }

            // Cerrar conexión
            input.close()
            output.close()
            socket.close()
            println("Conexión cerrada")

        } catch (e: Exception) {
            println("Error: ${e.message}")
        }
    }

=======
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
    /*
    Función para manejar el inicio de sesión
    Recibe dos parámetros: email y contraseña
    La función utiliza FirebaseAuth para autenticar al usuario
    */
    private fun iniciarsesion(email: String, pass: String) {
        // Inicia el proceso de inicio de sesión con el email y contraseña proporcionados
        autorizar.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                // Si el inicio de sesión es exitoso, obtiene el usuario actual y muestra un mensaje de bienvenida
                val usuario = autorizar.currentUser
<<<<<<< HEAD
                manejarInicioSesionExitoso(usuario)
            } else {
                if (task.exception is FirebaseAuthMultiFactorException) {
                    // El usuario tiene habilitado MFA y se requiere un segundo factor
                    val multiFactorException = task.exception as FirebaseAuthMultiFactorException
                    multiFactorResolver = multiFactorException.resolver
                    solicitarSegundoFactor()
                } else {
                    // Si hay un error, muestra un mensaje de error
                    mostrarError()
                    Toast.makeText(baseContext, "Error de Contraseña o Email", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Función para manejar el inicio de sesión exitoso
    private fun manejarInicioSesionExitoso(usuario: FirebaseUser?) {
        val emailUsuario = usuario?.email

        // Realiza una consulta a la base de datos local para obtener el usuario
        val db = DataBaseLITE(this)
        val usuarioLocal = db.getUsuario(emailUsuario ?: "")

        if (usuarioLocal != null) {
            val nombre = usuarioLocal.nombreUs
            val apellido = usuarioLocal.apellidoUs
            val rol = usuarioLocal.rolUs

            Toast.makeText(baseContext, "Bienvenido", Toast.LENGTH_SHORT).show()

            // Almacenar el correo electrónico del usuario en las preferencias compartidas
            val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            editor.putString("userEmail", usuarioLocal.emailUs)  // Guarda el correo
            editor.putString("userRole", usuarioLocal.rolUs)     // Guarda el rol
            editor.apply()

            // Redirige a AdminActivity si es Administrador o UsuarioActivity si es Usuario
            if (rol == "Administrador") {
                mostrarInicioAdmin(emailUsuario, nombre, apellido)
            } else {
                mostrarInicioUsuario(emailUsuario, nombre, apellido)
            }
        } else {
            Toast.makeText(baseContext,"Error: Usuario no encontrado en la base de datos local",Toast.LENGTH_SHORT).show()
        }
    }

    // Función para solicitar el segundo factor al usuario
    private fun solicitarSegundoFactor() {
        // Obtener la lista de factores registrados
        val hints = multiFactorResolver.hints

        // En este ejemplo, asumimos que el usuario solo tiene un segundo factor registrado
        val selectedHint = hints[0] as PhoneMultiFactorInfo

        // Enviar el código de verificación SMS
        val options = PhoneAuthOptions.newBuilder(autorizar)
            .setActivity(this)
            .setMultiFactorSession(multiFactorResolver.session)
            .setMultiFactorHint(selectedHint)
            .setCallbacks(callbacks)
            .setTimeout(30L, TimeUnit.SECONDS)
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    // Callbacks para el proceso de verificación del segundo factor
    private val callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        override fun onVerificationCompleted(credential: PhoneAuthCredential) {
            // Verificación automática (instantánea)
            val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
            resolverSignInWithAssertion(multiFactorAssertion)
        }

        override fun onVerificationFailed(e: FirebaseException) {
            // Manejar errores de verificación
            if (e is FirebaseAuthInvalidCredentialsException) {
                // Solicitud inválida
                Toast.makeText(this@MainActivity, "Solicitud inválida.", Toast.LENGTH_SHORT).show()
            } else if (e is FirebaseTooManyRequestsException) {
                // Exceso de solicitudes
                Toast.makeText(this@MainActivity,"Se excedió el límite de solicitudes.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(
                    this@MainActivity,"Error de verificación: ${e.message}",Toast.LENGTH_SHORT).show()
            }
        }

        override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
            // El código ha sido enviado al usuario
            this@MainActivity.verificationId = verificationId
            this@MainActivity.forceResendingToken = token
            mostrarDialogoCodigoVerificacion()
        }
    }

    // Función para mostrar un diálogo solicitando el código de verificación
    private fun mostrarDialogoCodigoVerificacion() {
        val v = LayoutInflater.from(this).inflate(R.layout.verify_phone_dialog, null)

        val editTextPhoneDialog: EditText = v.findViewById(R.id.edittext_phone_dialog)

        android.app.AlertDialog.Builder(this)
            .setView(v)
            .setPositiveButton("Verificar") { dialog, _ ->
                val code = editTextPhoneDialog.text.toString()
                if (code.isNotEmpty()) {
                    val credential = PhoneAuthProvider.getCredential(verificationId, code)
                    val multiFactorAssertion = PhoneMultiFactorGenerator.getAssertion(credential)
                    resolverSignInWithAssertion(multiFactorAssertion)
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

    // Función para completar el inicio de sesión con el segundo factor
    private fun resolverSignInWithAssertion(multiFactorAssertion: MultiFactorAssertion) {
        multiFactorResolver.resolveSignIn(multiFactorAssertion)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Inicio de sesión exitoso
                    val usuario = autorizar.currentUser
                    manejarInicioSesionExitoso(usuario)
                } else {
                    // Error al completar el inicio de sesión
                    Toast.makeText(this,"Error al completar el inicio de sesión con MFA.",Toast.LENGTH_SHORT).show()
                }
            }
=======
                val emailUsuario = usuario?.email

                // Realiza una consulta a la base de datos local para obtener el usuario
                val db = DataBaseLITE(this)
                val usuarioLocal = db.getUsuario(emailUsuario ?: "")

                if (usuarioLocal != null) {
                    val nombre = usuarioLocal.nombreUs
                    val apellido = usuarioLocal.apellidoUs
                    val rol = usuarioLocal.rolUs

                    Toast.makeText(baseContext, "Bienvenido", Toast.LENGTH_SHORT).show()

                    //Almacenar el correo electrónico del usuario en las preferencias compartidas
                    val sharedPreferences = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                    val editor = sharedPreferences.edit()

                    // Suponiendo que 'usuario' es el objeto de la clase Usuario que contiene todos los detalles
                    editor.putString("userEmail", usuarioLocal.emailUs)  // Guarda el correo
                    editor.putString("userRole", usuarioLocal.rolUs)     // Guarda el rol
                    editor.apply()


                    // Redirige a AdminActivity si es Administrador o UsuarioActivity si es Usuario
                    if (rol == "Administrador") {
                        mostrarInicioAdmin(emailUsuario, nombre, apellido)
                    } else {
                        mostrarInicioUsuario(emailUsuario, nombre, apellido)
                    }

                } else {
                    Toast.makeText(baseContext, "Error: Usuario no encontrado en la base de datos local", Toast.LENGTH_SHORT).show()
                }

            } else {
                // Si hay un error, muestra un mensaje de error
                mostrarError()
                Toast.makeText(baseContext, "Error de Contraseña o Email", Toast.LENGTH_SHORT)
                    .show()
            }
        }
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
    }

    // Función para mostrar una advertencia al usuario
    private fun mostrarAdvertencia(mensaje: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Advertencia") // Título del diálogo
        builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
        builder.setMessage(mensaje) // Mensaje del diálogo
        builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
        val dialogo: AlertDialog = builder.create() // Crea el diálogo
        dialogo.show() // Muestra el diálogo
    }

    // Función para mostrar un diálogo de error
    private fun mostrarError() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Existe un error") // Título del diálogo
        builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
        builder.setMessage("Inténtalo nuevamente") // Mensaje del diálogo
        builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
        val dialogo: AlertDialog = builder.create() // Crea el diálogo
        dialogo.show() // Muestra el diálogo
    }

    // Redirige al dashboard de Admin
    private fun mostrarInicioAdmin(email: String?, nombre: String?, apellido: String?) {
        val intent = Intent(this, AdminActivity::class.java).apply {
            putExtra("email", email)
            putExtra("nombre", nombre)
            putExtra("apellido", apellido)
        }
        startActivity(intent)
    }

    // Redirige al dashboard de Usuario
    private fun mostrarInicioUsuario(email: String?, nombre: String?, apellido: String?) {
        val intent = Intent(this, UsuarioActivity::class.java).apply {
            putExtra("email", email)
            putExtra("nombre", nombre)
            putExtra("apellido", apellido)
        }
        startActivity(intent)
    }

}
