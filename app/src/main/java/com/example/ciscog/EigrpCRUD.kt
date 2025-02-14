package com.example.ciscog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Eigrp

class EigrpCRUD : AppCompatActivity() {
    private var editTextAsNumber: EditText? = null
    private var editTextRed: EditText? = null
    private var editTextWildcard: EditText? = null
    private var spinnerDispositivo: Spinner? = null
    private var buttonRegistrar: Button? = null
    private var db = DataBaseLITE(this)
    private var btnRegresar: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eigrp_crud)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        init()
        listeners(email.toString())
    }

    private fun init(){
        editTextAsNumber = findViewById(R.id.editText_AsNumber)
        editTextRed = findViewById(R.id.editText_Red)
        editTextWildcard = findViewById(R.id.editText_Wildcard)
        spinnerDispositivo = findViewById(R.id.spinner_dispositivo)
        buttonRegistrar = findViewById(R.id.btn_anadir)
        btnRegresar = findViewById(R.id.return_btn)

        //CONFIGURACIÓN DE LOS ADAPTADORES DEL SPINNER
        val dispositivoEigrp = arrayOf("LS1-R01", "LS1-R02", "LS2-R01", "LS2-R02", "LS2-R03")
        val dispositivoAdapter = ArrayAdapter(this, R.layout.spinner_item, dispositivoEigrp)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerDispositivo?.adapter = dispositivoAdapter

    }

    private fun listeners(email: String){
        buttonRegistrar?.setOnClickListener {
            register(email)
            resetForm()
        }

        btnRegresar?.setOnClickListener {
            val intent = Intent(this, ListaEigrpReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun register(email: String){
        val asNumber = editTextAsNumber?.text.toString()
        val red = editTextRed?.text.toString()
        val wildcard = editTextWildcard?.text.toString()
        val dispositivo = spinnerDispositivo?.selectedItem.toString()

        if (isValidForm(asNumber = asNumber, red = red, wildcard = wildcard, dispositivo = dispositivo)) {


            //Verifica si la configuración de EIGRP ya existe en la base de datos local
            if (db.isEigrpExist(asNumber.toInt(), red, wildcard, dispositivo)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Advertencia") // Título del diálogo
                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                builder.setMessage("Esa configuración de EIGRP ya se encuentra registrada en el sistema") // Mensaje del diálogo
                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                dialogo.show() // Muestra el diálogo
            } else {
                // Crea un objeto de tipo Eigrp con los datos ingresados
                val eigrp = Eigrp(asNumber.toInt(),red, wildcard,dispositivo)

                // Inserta los datos en la base de datos
                db.insertDataEigrp(eigrp, email)

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
                        val intent = Intent(this, ListaEigrpReg::class.java).apply {
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

    private fun isValidForm(asNumber: String, red: String, wildcard: String, dispositivo: String): Boolean {
        if (asNumber.isBlank()){

            Toast.makeText(this, "Debes ingresar el As Number", Toast.LENGTH_SHORT).show()
            return false
        }

        if (red.isBlank()){

            Toast.makeText(this, "Debes ingresar la red", Toast.LENGTH_SHORT).show()
            return false
        }

        if (wildcard.isBlank()){

            Toast.makeText(this, "Debes ingresar la wildcard", Toast.LENGTH_SHORT).show()
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
        editTextAsNumber?.setText("")
        editTextRed?.setText("")
        editTextWildcard?.setText("")
        spinnerDispositivo?.setSelection(0)
    }
}