package com.example.ciscog

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ciscog.models.Usuario
import com.google.firebase.auth.FirebaseAuth

class UsuarioCRUD : AppCompatActivity() {
    // Declaración de variables para los elementos de la interfaz
    private var editTextNombre: EditText? = null
    private var editTextApellido: EditText? = null
    private var editTextEmail: EditText? = null
    private var editTextContrasena: EditText? = null
    private var spinnerRol: Spinner? = null
    private var buttonRegistrar: Button? = null
    private var btnRegresar: ImageButton? = null
    lateinit var autorizar: FirebaseAuth

    // Instancia de la base de datos local
    private var db = DataBaseLITE(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_usuario_crud)

        autorizar = FirebaseAuth.getInstance()

        val email = intent.getStringExtra("email")

        // Inicializa los componentes de la interfaz
        init()

        // Configura los listeners para las acciones del usuario
        listeners(email.toString())
    }

    // Función para inicializar los elementos de la interfaz
    private fun init() {
        editTextNombre = findViewById(R.id.editText_Nombre) // Campo de texto para el Nombre
        editTextApellido = findViewById(R.id.editText_Apellido) // Campo de texto para el Apellido
        editTextEmail = findViewById(R.id.editText_Email) // Campo de texto para el Email
        editTextContrasena = findViewById(R.id.editText_Contrasena) // Campo de texto para la Contraseña
        spinnerRol = findViewById(R.id.spinner_rol) // Selector para el Rol
        buttonRegistrar = findViewById(R.id.btn_anadir) // Botón para registrar el usuario
        btnRegresar = findViewById(R.id.return_btn) // Botón para regresar a la actividad anterior


        // Configuración del adaptador para el spinner (selector de rol)
        val rolUsuario = arrayOf("Administrador", "Empleado", "Usuario")
        val rolAdapter = ArrayAdapter(this, R.layout.spinner_item, rolUsuario)
        rolAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerRol?.adapter = rolAdapter // Asigna el adaptador al spinner
    }

    // Función para definir los listeners (acciones al hacer clic)
    private fun listeners(email: String) {
        // Acción al hacer clic en el botón de registrar
        buttonRegistrar?.setOnClickListener {
            register(email) // Llama a la función para registrar al usuario
            resetForm() // Resetea el formulario después de registrar
        }

        // Listener para el botón de regresar, que regresa a la actividad anterior
        btnRegresar?.setOnClickListener {
            val intent = Intent(this, ListaUsuarioReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
        }
    }

    // Función para registrar un nuevo usuario
    private fun register(email: String) {
        // Obtiene los valores ingresados por el usuario
        val name = editTextNombre?.text.toString()
        val lastname = editTextApellido?.text.toString()
        val correo = editTextEmail?.text.toString()
        val contrasena = editTextContrasena?.text.toString()
        val rol = spinnerRol?.selectedItem.toString()

        // Verifica si el formulario es válido antes de registrar
        if (isValidForm(name, lastname, correo, contrasena)) {

            //Verifica si el correo ya existe en la base de datos local
            if (db.isEmailExist(correo)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Advertencia") // Título del diálogo
                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                builder.setMessage("Ese correo ya se encuentra registrado en el sistema") // Mensaje del diálogo
                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                dialogo.show() // Muestra el diálogo
            } else {
                //Crea un objeto de tipo Usuario con los datos ingresados
                val usuario = Usuario(name, lastname, correo, contrasena, rol, "CURRENT_TIMESTAMP")
                // Inserta los datos en la base de datos
                db.insertDataUsuario(usuario)

                autorizar.createUserWithEmailAndPassword(correo, contrasena).addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        //val user = autorizar.currentUser
                        // Muestra un mensaje confirmando que los datos se registraron correctamente
                        Toast.makeText(this, "Los datos se registraron correctamente", Toast.LENGTH_SHORT).show()

                        // Redirige a la actividad que lista los usuarios registradas
                        val intent = Intent(this, ListaUsuarioReg::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(intent)
                        finish() // Finaliza la actividad actual
                    } else {
                        val error = task.exception?.message ?: "Error en Firebase desconocido"
                        Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Función para validar que los campos del formulario no estén vacíos
    private fun isValidForm(nombre: String, apellido: String, email: String, contrasena: String): Boolean {
        if (nombre.isBlank()) {
            Toast.makeText(this, "Debes ingresar el nombre", Toast.LENGTH_SHORT).show()
            return false
        }
        if (apellido.isBlank()) {
            Toast.makeText(this, "Debes ingresar el apellido", Toast.LENGTH_SHORT).show()
            return false
        }
        if (email.isBlank()) {
            Toast.makeText(this, "Debes ingresar el correo", Toast.LENGTH_SHORT).show()
            return false
        }
        if (contrasena.isBlank()) {
            Toast.makeText(this, "Debes ingresar la contraseña", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // Función para limpiar los campos del formulario después de registrar
    private fun resetForm() {
        editTextNombre?.setText("")
        editTextApellido?.setText("")
        editTextEmail?.setText("")
        editTextContrasena?.setText("")
        spinnerRol?.setSelection(0)
    }
}
