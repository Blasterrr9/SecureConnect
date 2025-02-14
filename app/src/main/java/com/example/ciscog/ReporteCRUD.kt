package com.example.ciscog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Reporte

class ReporteCRUD : AppCompatActivity() {
    /*private var editTextVlan_Id: EditText? = null
    private var editTextNombre_Vlan: EditText? = null*/
    private var spinnerTipo: Spinner? = null
    private var buttonRegistrar: Button? = null
    private var db = DataBaseLITE(this)
    private var btnRegresar: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reporte_crud)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        init()
        listeners(email.toString())
    }

    private fun init(){
        /*editTextVlan_Id = findViewById(R.id.editText_Vlan_Id)
        editTextNombre_Vlan = findViewById(R.id.editText_Nombre_Vlan)*/
        spinnerTipo= findViewById(R.id.spinner_tipo_reporte)
        buttonRegistrar = findViewById(R.id.btn_anadir)
        btnRegresar = findViewById(R.id.return_btn)

        //CONFIGURACIÓN DE LOS ADAPTADORES DEL SPINNER
        val tipoReporte = arrayOf("Porcentaje de Dispositivos", "Redes en la Topología", "Configuraciones por Usuario", "Tipos de Configuraciones", "Protocolos de Enrutamiento", "Reglas de Control de Acceso")
        val reporteAdapter = ArrayAdapter(this, R.layout.spinner_item, tipoReporte)
        reporteAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)
        spinnerTipo?.adapter = reporteAdapter

    }

    private fun listeners(email: String){
        buttonRegistrar?.setOnClickListener {
            register(email)
            resetForm()
        }

        btnRegresar?.setOnClickListener {
            val intent = Intent(this, ListaReporteReg::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()
        }

    }

    private fun register(email: String){

        val tipo = spinnerTipo?.selectedItem.toString()

        if (tipo == "Configuraciones por Usuario"){
            val reporte = Reporte("Reporte Tipo 3","Usuario 1", "Alejandro Morales", "Configuraciones Usuario 1", "ACL: 2 (10%) - EIGRP: 0 (0%) - NAT: 3 (15%) - OSPF: 2 (10%) - Rutas: 4 (20%) - STP: 1 (5%) - VLAN: 2 (10%)", "Usuario 2", "José Luis Vargas", "Configuraciones Usuario 2", "ACL: 0 (0%) - EIGRP: 0 (0%) - NAT: 1 (5%) - OSPF: 1 (5%) - Rutas: 0 (0%) - STP: 1 (5%) - VLAN: 3 (15%)")

            db.insertDataReporte(reporte)

                val intent = Intent(this, ReportePlantilla3::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()

        }
    }


    //Limpia los campos del formulario
    private fun resetForm(){
        spinnerTipo?.setSelection(0)
    }
}