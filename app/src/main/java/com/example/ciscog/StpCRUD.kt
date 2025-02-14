package com.example.ciscog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Stp

class StpCRUD : AppCompatActivity() {
    private var editTextPrioridad: EditText? = null
    private var editTextVlanId: EditText? = null
    private var spinnerModo: Spinner? = null
    private var spinnerDispositivo: Spinner? = null
    private var buttonRegistrar: Button? = null
    private var db = DataBaseLITE(this)
    private var btnRegresar: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stp_crud)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        init()
        listeners(email.toString())
    }

    private fun init(){
        editTextPrioridad = findViewById(R.id.editText_Prioridad)
        editTextVlanId = findViewById(R.id.editText_VLAN_ID)
        spinnerModo = findViewById(R.id.spinner_modo)
        spinnerDispositivo = findViewById(R.id.spinner_dispositivo)
        buttonRegistrar = findViewById(R.id.btn_anadir)
        btnRegresar = findViewById(R.id.return_btn)

        val modoStp = arrayOf("PVST", "MSTP", "Rapid-PVST")
        val modoAdapter = ArrayAdapter(this, R.layout.spinner_item, modoStp)
        modoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerModo?.adapter = modoAdapter

        val dispositivoStp = arrayOf("LS1-SW1", "LS1-SW2", "LS2-SW1", "LS2-SW2", "LS2-SW3")
        val dispositivoAdapter = ArrayAdapter(this, R.layout.spinner_item, dispositivoStp)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerDispositivo?.adapter = dispositivoAdapter

    }

    private fun listeners(email: String){
        buttonRegistrar?.setOnClickListener {
            register(email)
            resetForm()
        }

        btnRegresar?.setOnClickListener {
            val intent = Intent(this, ListaStpReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun register(email: String) {
        val prioridad = editTextPrioridad?.text.toString()
        val vlan_id = editTextVlanId?.text.toString()
        val modo = spinnerModo?.selectedItem.toString()
        val dispositivo = spinnerDispositivo?.selectedItem.toString()

        if (isValidForm(prioridad = prioridad, vlan_id = vlan_id, modo = modo, dispositivo = dispositivo)) {

            // Verifica si la configuración de STP ya existe en la base de datos local
            if (modo == "MSTP") {
                // Para MSTP, se permite múltiples instancias, así que no es necesario verificar duplicados
            } else if (db.isSTPExist(modo, dispositivo)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Advertencia") // Título del diálogo
                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                builder.setMessage("La configuración de ${modo} ya se encuentra registrada para el dispositivo ${dispositivo}") // Mensaje del diálogo
                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                dialogo.show() // Muestra el diálogo
                return
            }

            // Crea un objeto de tipo Stp con los datos ingresados
            val stp = Stp(prioridad.toInt(), vlan_id.toInt(), modo, dispositivo)

            // Inserta los datos en la base de datos
            db.insertDataSTP(stp, email)

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
                    val intent = Intent(this, ListaStpReg::class.java).apply {
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


    private fun isValidForm(prioridad: String, vlan_id: String, modo: String, dispositivo: String): Boolean {
        if (prioridad.isBlank()){

            Toast.makeText(this, "Debes ingresar la Prioridad", Toast.LENGTH_SHORT).show()
            return false
        }

        if (vlan_id.isBlank()){

            Toast.makeText(this, "Debes ingresar la VLAN", Toast.LENGTH_SHORT).show()
            return false
        }

        if (modo.isBlank()){

            Toast.makeText(this, "Debes ingresar el Modo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dispositivo.isBlank()){

            Toast.makeText(this, "Debes ingresar el dispositivo", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //Limpia los campos del formulario
    private fun resetForm(){
        editTextPrioridad?.setText("")
        editTextVlanId?.setText("")
        spinnerModo?.setSelection(0)
        spinnerDispositivo?.setSelection(0)
    }
}