package com.example.ciscog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Vlan

class VlanCRUD : AppCompatActivity() {
    private var editTextVlan_Id: EditText? = null
    private var editTextNombre_Vlan: EditText? = null
    private var spinnerDispositivo: Spinner? = null
    private var buttonRegistrar: Button? = null
    private var db = DataBaseLITE(this)
    private var btnRegresar: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vlan_crud)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        init()
        listeners(email.toString())
    }

    private fun init(){
        editTextVlan_Id = findViewById(R.id.editText_Vlan_Id)
        editTextNombre_Vlan = findViewById(R.id.editText_Nombre_Vlan)
        spinnerDispositivo = findViewById(R.id.spinner_dispositivo)
        buttonRegistrar = findViewById(R.id.btn_anadir)
        btnRegresar = findViewById(R.id.return_btn)

        //CONFIGURACIÓN DE LOS ADAPTADORES DEL SPINNER
        val dispositivoVlan = arrayOf("LS1-SW1", "LS1-SW2", "LS2-SW1", "LS2-SW2", "LS2-SW3")
        val dispositivoAdapter = ArrayAdapter(this, R.layout.spinner_item, dispositivoVlan)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerDispositivo?.adapter = dispositivoAdapter

    }

    private fun listeners(email: String){
        buttonRegistrar?.setOnClickListener {
            register(email)
            resetForm()
        }

        btnRegresar?.setOnClickListener {
            val intent = Intent(this, ListaVlanReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun register(email: String){
        val vlan_id = editTextVlan_Id?.text.toString()
        val nombre_vlan = editTextNombre_Vlan?.text.toString()
        val dispositivo = spinnerDispositivo?.selectedItem.toString()

        if (isValidForm(vlan_id = vlan_id, nombre_vlan = nombre_vlan, dispositivo = dispositivo)) {

            //Verifica si la configuración de VLAN ya existe en la base de datos local
            if (db.isVLANExist(vlan_id.toInt())) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Advertencia") // Título del diálogo
                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                builder.setMessage("Esa VLAN ya se encuentra registrada en el sistema") // Mensaje del diálogo
                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                dialogo.show() // Muestra el diálogo
            } else {
                // Crea un objeto de tipo Vlan con los datos ingresados
                val vlan = Vlan(vlan_id.toInt(),nombre_vlan,dispositivo)

                // Inserta los datos en la base de datos
                db.insertDataVLAN(vlan, email)

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
                        val intent = Intent(this, ListaVlanReg::class.java).apply {
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

    private fun isValidForm(vlan_id: String, nombre_vlan: String, dispositivo: String): Boolean {
        if (vlan_id.isBlank()){

            Toast.makeText(this, "Debes ingresar el VLAN ID", Toast.LENGTH_SHORT).show()
            return false
        }

        if (nombre_vlan.isBlank()){

            Toast.makeText(this, "Debes ingresar el nombre de la VLAN", Toast.LENGTH_SHORT).show()
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
        editTextVlan_Id?.setText("")
        editTextNombre_Vlan?.setText("")
        spinnerDispositivo?.setSelection(0)
    }
}