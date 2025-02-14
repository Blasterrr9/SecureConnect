package com.example.ciscog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Acl

class AclCRUD : AppCompatActivity() {
    private var spinnerTipo: Spinner? = null
    private var spinnerAccion: Spinner? = null
    private var editTextRegla: EditText? = null
    private var spinnerDispositivo: Spinner? = null
    private var buttonRegistrar: Button? = null
    private var db = DataBaseLITE(this)
    private var btnRegresar: ImageButton? = null
    private var btnChat: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_acl_crud)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        init()
        listeners(email.toString())
    }

    private fun init(){
        spinnerTipo = findViewById(R.id.spinner_tipo_acl)
        spinnerAccion = findViewById(R.id.spinner_accion)
        editTextRegla = findViewById(R.id.editText_Regla)
        spinnerDispositivo = findViewById(R.id.spinner_dispositivo)
        buttonRegistrar = findViewById(R.id.btn_anadir)
        btnRegresar = findViewById(R.id.return_btn)
        btnChat = findViewById(R.id.chat_btn)

        val tipoAcl = arrayOf("Estándar", "Extendida")
        val tipoAdapter = ArrayAdapter(this, R.layout.spinner_item, tipoAcl)
        tipoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerTipo?.adapter = tipoAdapter

        val accionAcl = arrayOf("permit", "deny")
        val accionAdapter = ArrayAdapter(this, R.layout.spinner_item, accionAcl)
        accionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerAccion?.adapter = accionAdapter

        val dispositivoAcl = arrayOf("LS1-R01", "LS1-R02", "LS2-R01", "LS2-R02", "LS2-R03")
        val dispositivoAdapter = ArrayAdapter(this, R.layout.spinner_item, dispositivoAcl)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerDispositivo?.adapter = dispositivoAdapter

    }

    private fun listeners(email: String){
        buttonRegistrar?.setOnClickListener {
            register(email)
            resetForm()
        }

        btnRegresar?.setOnClickListener {
            val intent = Intent(this, ListaAclReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
        }

        // Redirigir a ChatActivity al hacer clic en el botón de chat
        btnChat?.setOnClickListener {
            val mensajeRuta = """
                            A continuación, voy a realizar una Lista de Control de Acceso en un dispositivo Router, ayúdame a realizarla con los parámetros que te voy a dar a continuación:
                        """.trimIndent()

            val intent = Intent(this, ChatActivity::class.java)
            intent.putExtra("config_info", mensajeRuta)
            startActivity(intent)
        }

    }

    private fun register(email: String) {
        val tipo = spinnerTipo?.selectedItem.toString()
        val accion = spinnerAccion?.selectedItem.toString()
        val regla = editTextRegla?.text.toString()
        val dispositivo = spinnerDispositivo?.selectedItem.toString()
        val reglaCompleta = "$accion $regla"

        if (isValidForm(tipo = tipo, accion = accion, regla = regla, dispositivo = dispositivo)) {

            //Verifica si la ACL ya existe en la base de datos local
            if (db.isACLExist(tipo, reglaCompleta, dispositivo)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Advertencia") // Título del diálogo
                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                builder.setMessage("Esa ACL ya se encuentra registrada en el sistema") // Mensaje del diálogo
                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                dialogo.show() // Muestra el diálogo
            } else {
                // Crea un objeto de tipo Acl con los datos ingresados
                val acl = Acl(tipo, reglaCompleta, dispositivo)

                // Inserta los datos en la base de datos
                db.insertDataACL(acl, email)

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
                        val intent = Intent(this, ListaAclReg::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(intent)
                        finish()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }
            }
        }
    }


    private fun isValidForm(tipo: String, accion: String, regla: String, dispositivo: String): Boolean {
        if (tipo.isBlank()){

            Toast.makeText(this, "Debes ingresar el Tipo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (accion.isBlank()){

            Toast.makeText(this, "Debes ingresar la Acción", Toast.LENGTH_SHORT).show()
            return false
        }

        if (regla.isBlank()){

            Toast.makeText(this, "Debes ingresar la Regla de la ACL", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dispositivo.isBlank()){

            Toast.makeText(this, "Debes ingresar el dispositivo", Toast.LENGTH_SHORT).show()
            return false
        }

        // Validación para ACL Estándar
        if (tipo == "Estándar") {
            val regexEstandar = Regex("^(\\d{1,3}(\\.\\d{1,3}){3}\\s+\\d{1,3}(\\.\\d{1,3}){3}\$)")
            if (!regexEstandar.matches(regla)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Advertencia") // Título del diálogo
                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                builder.setMessage("Formato incorrecto para ACL Estándar. Use: 'IP y Wildcard'") // Mensaje del diálogo
                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                dialogo.show() // Muestra el diálogo
                return false
            }
        }

        if (tipo == "Extendida") {
            // Validación básica para ACL extendida
            val contieneProtocolo = regla.contains(Regex("\\b(TCP|UDP|ICMP)\\b", RegexOption.IGNORE_CASE))
            val contieneIp = regla.contains(Regex("\\d{1,3}(\\.\\d{1,3}){3}"))
            val contieneAny = regla.contains("any", ignoreCase = true)
            if (!contieneProtocolo && (contieneIp || contieneAny) && regla.length >= 15) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Advertencia") // Título del diálogo
                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                builder.setMessage("Formato incorrecto para ACL Extendida. Use: 'PROTOCOLO ORIGEN DESTINO [PUERTO OPCIONAL]") // Mensaje del diálogo
                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                dialogo.show() // Muestra el diálogo
                return false
            }
        }


        return true

    }


    //Limpia los campos del formulario
    private fun resetForm(){
        spinnerTipo?.setSelection(0)
        spinnerAccion?.setSelection(0)
        editTextRegla?.setText("")
        spinnerDispositivo?.setSelection(0)
    }
}