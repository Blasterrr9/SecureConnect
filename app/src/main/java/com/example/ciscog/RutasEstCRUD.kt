package com.example.ciscog

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Spinner
<<<<<<< HEAD
import android.widget.TextView
=======
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.ciscog.models.RutasEst

class RutasEstCRUD : AppCompatActivity() {
    // Declaración de variables para los elementos de la interfaz
    private var editTextIpDestino: EditText? = null
    private var editTextMascara: EditText? = null
    private var editTextIpSalto: EditText? = null
    private var spinnerDispositivo: Spinner? = null
    private var buttonRegistrar: Button? = null
    private var btnRegresar: ImageButton? = null

    // Instancia de la base de datos local
    private var db = DataBaseLITE(this)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rutas_est_crud)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        // Inicializa los componentes de la interfaz
        init()

        // Configura los listeners para las acciones del usuario
        listeners(email.toString())
    }

    // Función para inicializar los elementos de la interfaz
    private fun init() {
        editTextIpDestino = findViewById(R.id.editText_IpDestino) // Campo de texto para la IP destino
        editTextMascara = findViewById(R.id.editText_Mascara) // Campo de texto para la máscara de red
        editTextIpSalto = findViewById(R.id.editText_IpSalto) // Campo de texto para la IP de salto
        spinnerDispositivo = findViewById(R.id.spinner_dispositivo) // Selector para el dispositivo
        buttonRegistrar = findViewById(R.id.btn_anadir) // Botón para registrar la ruta estática
        btnRegresar = findViewById(R.id.return_btn) // Botón para regresar a la actividad anterior


        // Configuración del adaptador para el spinner (selector de dispositivo)
        val dispositivoNat = arrayOf("LS1-R01", "LS1-R02", "LS2-R01", "LS2-R02", "LS2-R03")
        val dispositivoAdapter = ArrayAdapter(this, R.layout.spinner_item, dispositivoNat)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerDispositivo?.adapter = dispositivoAdapter // Asigna el adaptador al spinner
    }

    // Función para definir los listeners (acciones al hacer clic)
    private fun listeners(email: String) {
        // Acción al hacer clic en el botón de registrar
        buttonRegistrar?.setOnClickListener {
            register(email) // Llama a la función para registrar la ruta
            resetForm() // Resetea el formulario después de registrar
        }

        // Listener para el botón de regresar, que regresa a la actividad anterior
        btnRegresar?.setOnClickListener {
            val intent = Intent(this, ListaRutasEstReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
        }
    }

    // Función para registrar una nueva ruta estática
    private fun register(email: String) {
        // Obtiene los valores ingresados por el usuario
        val ipDestino = editTextIpDestino?.text.toString()
        val mascara = editTextMascara?.text.toString()
        val ipSalto = editTextIpSalto?.text.toString()
        val dispositivo = spinnerDispositivo?.selectedItem.toString()

        // Verifica si el formulario es válido antes de registrar
        if (isValidForm(mascara = mascara, ipDestino = ipDestino, dispositivo = dispositivo, ipSalto = ipSalto)) {

            //Verifica si la ruta ya existe en la base de datos local
            if (db.isRutaExist(ipDestino, mascara, ipSalto, dispositivo)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Advertencia") // Título del diálogo
                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                builder.setMessage("Esa ruta ya se encuentra registrada en el sistema") // Mensaje del diálogo
                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                dialogo.show() // Muestra el diálogo
            } else {
                // Crea un objeto de tipo RutasEst con los datos ingresados
                val rutasEst = RutasEst(ipDestino, mascara, ipSalto, dispositivo)

                // Inserta los datos en la base de datos
<<<<<<< HEAD
                db.insertDataRutaEst(rutasEst, email)

                val idConfiguracion = db.getLastInsertedConfigId()
                val configuracion = db.getConfiguracionById(idConfiguracion)

                if (configuracion != null) {
                    val dialogView = layoutInflater.inflate(R.layout.cardview_config, null)

                    val Usuario = db.getUsuarioById(configuracion.idUsuario)
                    val Dispositivo = db.getDispositivoById(configuracion.idDispositivo)


                    // Llenar los TextViews con los datos obtenidos
                    dialogView.findViewById<TextView>(R.id.textview_id).text = idConfiguracion.toString()
                    dialogView.findViewById<TextView>(R.id.textview_nombre_config).text = configuracion.nombreConfig
                    dialogView.findViewById<TextView>(R.id.textview_descripcion_config).text = configuracion.descripcionConfig
                    dialogView.findViewById<TextView>(R.id.textview_tipo_config).text = configuracion.tipoConfig
                    dialogView.findViewById<TextView>(R.id.textview_fecha_config).text = configuracion.fechaConfig
                    dialogView.findViewById<TextView>(R.id.textview_id_usuario).text = configuracion.idUsuario.toString() + " (" + Usuario!!.nombreUs + " " + Usuario.apellidoUs + ")"
                    dialogView.findViewById<TextView>(R.id.textview_id_dispositivo).text = configuracion.idDispositivo.toString() + " (" + Dispositivo!!.nombreDisp + ")"

                    val builder = AlertDialog.Builder(this)
                    builder.setView(dialogView)
                    builder.setPositiveButton("Aceptar") { dialog, _ ->
                        dialog.dismiss()
                        val intent = Intent(this, ListaRutasEstReg::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(intent)
                        finish()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
=======
                db.insertDataRutaEst(rutasEst)

                // Muestra un mensaje confirmando que los datos se registraron correctamente
                Toast.makeText(this, "Los datos se registraron correctamente", Toast.LENGTH_SHORT).show()

                // Redirige a la actividad que lista las rutas registradas
                val intent = Intent(this, ListaRutasEstReg::class.java).apply {
                    putExtra("email", email)
                }
                startActivity(intent)
                finish() // Finaliza la actividad actual
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
            }

        }
    }

    // Función para validar que los campos del formulario no estén vacíos
    private fun isValidForm(mascara: String, ipDestino: String, dispositivo: String, ipSalto: String): Boolean {
        if (mascara.isBlank()) {
            Toast.makeText(this, "Debes ingresar la mascara", Toast.LENGTH_SHORT).show()
            return false
        }

        if (ipDestino.isBlank()) {
            Toast.makeText(this, "Debes ingresar la IP Destino", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dispositivo.isBlank()) {
            Toast.makeText(this, "Debes ingresar el dispositivo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (ipSalto.isBlank()) {
            Toast.makeText(this, "Debes ingresar la IP Salto", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    // Función para limpiar los campos del formulario después de registrar
    private fun resetForm() {
        editTextMascara?.setText("")
        editTextIpSalto?.setText("")
        editTextIpDestino?.setText("")
        spinnerDispositivo?.setSelection(0)
    }
}
