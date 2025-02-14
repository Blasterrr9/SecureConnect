package com.example.ciscog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Nat

class NatCRUD : AppCompatActivity() {
    private var spinnerTipo: Spinner? = null
    private var editTextIpPriv: EditText? = null
    private var editTextIpPub: EditText? = null
    private var spinnerDispositivo: Spinner? = null
    private var buttonRegistrar: Button? = null
    private var db = DataBaseLITE(this)
    private var btnRegresar: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nat_crud)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        init()
        listeners(email.toString())
    }

    private fun init(){
        spinnerTipo = findViewById(R.id.spinner_tipo)
        editTextIpPriv = findViewById(R.id.editText_IpPriv)
        editTextIpPub = findViewById(R.id.editText_IpPub)
        spinnerDispositivo = findViewById(R.id.spinner_dispositivo)
        buttonRegistrar = findViewById(R.id.btn_anadir)
        btnRegresar = findViewById(R.id.return_btn)

        //CONFIGURACIÓN DE LOS ADAPTADORES DEL SPINNER
        val tipoNat = arrayOf("Estática", "Dinámica")
        val tipoAdapter = ArrayAdapter(this, R.layout.spinner_item, tipoNat)
        tipoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerTipo?.adapter = tipoAdapter

        val dispositivoNat = arrayOf("LS1-R01", "LS1-R02", "LS2-R01", "LS2-R02", "LS2-R03")
        val dispositivoAdapter = ArrayAdapter(this, R.layout.spinner_item, dispositivoNat)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerDispositivo?.adapter = dispositivoAdapter

    }

    private fun listeners(email: String){
        buttonRegistrar?.setOnClickListener {
            register(email)
            resetForm()
        }

        btnRegresar?.setOnClickListener {
            val intent = Intent(this, ListaNatReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun register(email: String){
        val tipo = spinnerTipo?.selectedItem.toString()
        val ipPriv = editTextIpPriv?.text.toString()
        val ipPub = editTextIpPub?.text.toString()
        val dispositivo = spinnerDispositivo?.selectedItem.toString()

        if (isValidForm(ipPriv = ipPriv, tipo = tipo, dispositivo = dispositivo, ipPub = ipPub)) {


            //Verifica si la NAT ya existe en la base de datos local
            if (db.isNATExist(tipo, ipPriv, ipPub, dispositivo)) {
                val builder = AlertDialog.Builder(this)
                builder.setTitle("Advertencia") // Título del diálogo
                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                builder.setMessage("Esa configuración de NAT ya se encuentra registrada en el sistema") // Mensaje del diálogo
                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                dialogo.show() // Muestra el diálogo
            } else {
                // Crea un objeto de tipo NAT con los datos ingresados
                val nats = Nat(tipo,ipPriv,ipPub,dispositivo)

                // Inserta los datos en la base de datos
<<<<<<< HEAD
                db.insertDataNAT(nats, email)

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
                        val intent = Intent(this, ListaNatReg::class.java).apply {
                            putExtra("email", email)
                        }
                        startActivity(intent)
                        finish()
                    }
                    val dialog = builder.create()
                    dialog.show()
                }

=======
                db.insertDataNAT(nats)

                // Muestra un mensaje confirmando que los datos se registraron correctamente
                Toast.makeText(this, "Los datos se registraron correctamente", Toast.LENGTH_SHORT).show()

                // Redirige a la actividad que lista las configuraciones de NAT registradas
                val intent = Intent(this, ListaNatReg::class.java).apply {
                    putExtra("email", email)
                }
                startActivity(intent)
                finish() // Finaliza la actividad actual
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
            }
        }
    }

    private fun isValidForm(ipPriv: String, tipo: String, dispositivo: String, ipPub: String): Boolean {
        if (ipPriv.isBlank()){

            Toast.makeText(this, "Debes ingresar la IP Privada", Toast.LENGTH_SHORT).show()
            return false
        }

        if (tipo.isBlank()){

            Toast.makeText(this, "Debes ingresar el tipo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (dispositivo.isBlank()){

            Toast.makeText(this, "Debes ingresar el dispositivo", Toast.LENGTH_SHORT).show()
            return false
        }

        if (ipPub.isBlank()){

            Toast.makeText(this, "Debes ingresar la IP Publica", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    //Limpia los campos del formulario
    private fun resetForm(){
        editTextIpPriv?.setText("")
        editTextIpPub?.setText("")
        spinnerTipo?.setSelection(0)
        spinnerDispositivo?.setSelection(0)
    }
}