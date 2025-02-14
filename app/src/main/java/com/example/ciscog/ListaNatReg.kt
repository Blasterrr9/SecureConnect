package com.example.ciscog

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
<<<<<<< HEAD
import android.view.ViewGroup
=======
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciscog.adapters.NatAdapter
import com.example.ciscog.models.Nat
import kotlin.collections.ArrayList

class ListaNatReg : AppCompatActivity(){
    // Variables para los componentes de la interfaz
    private var recyclerViewNats: RecyclerView? = null
    private var adapter: NatAdapter? = null
    private var listaNats = ArrayList<Nat>()
    private var db = DataBaseLITE(this)
    private var btnRegistrar: Button? = null
    private var searchView: SearchView? = null
    private var btnRegresar: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_nat_reg)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        // Inicializa los componentes de la interfaz
        init()
        // Configura los listeners para las acciones del usuario
        listeners(email.toString())
    }

    // Función para inicializar los elementos de la interfaz
    private fun init(){
        recyclerViewNats = findViewById(R.id.recyclerview_nat)
        searchView = findViewById(R.id.searchview_nat)
        btnRegistrar = findViewById(R.id.registrar_btn)
        btnRegresar = findViewById(R.id.return_btn)

        // Configura el layout manager para el RecyclerView
        recyclerViewNats?.layoutManager = LinearLayoutManager(this)

        // Verificar el rol del usuario
        val usuarioRol = getUserRole(this) // Esta función debe obtener el rol del usuario

        btnRegistrar?.let { button ->
<<<<<<< HEAD
            // Si el rol es "Usuario", oculta el botón de registro
            if (usuarioRol.equals("Usuario", ignoreCase = true)) {
                button.visibility = View.GONE
                // Ajusta el margen inferior del RecyclerView para dejar espacio para el FrameLayout
                val params = recyclerViewNats?.layoutParams as ViewGroup.MarginLayoutParams
                params.bottomMargin = 470 // 180dp en píxeles
                recyclerViewNats?.layoutParams = params
            } else {
                button.visibility = View.VISIBLE
            }
=======
            button.visibility= if (usuarioRol.equals("Usuario", ignoreCase = true)) View.GONE else View.VISIBLE
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
        }

        // Carga los datos desde la base de datos y los muestra en el RecyclerView
        leer()
    }

    // Función para definir los listeners (acciones al hacer clic)
    private fun listeners(email: String) {
        // Listener para manejar la búsqueda de rutas
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // No se usa en este caso
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filtra la lista según el texto ingresado por el usuario
                filterList(newText)
                return true
            }
        })

        // Listener para el botón de registrar, que inicia la actividad de registro de NAT
        btnRegistrar?.setOnClickListener {
            val intent = Intent(this, NatCRUD::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
        }

        // Listener para el botón de regresar, que regresa a la actividad anterior
        btnRegresar?.setOnClickListener {
<<<<<<< HEAD
            finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
=======
            // Realiza una consulta a la base de datos local para obtener el usuario
            val db = DataBaseLITE(this)
            val usuarioLocal = db.getUsuario(email ?: "")

            val nombre = usuarioLocal?.nombreUs
            val apellido = usuarioLocal?.apellidoUs
            val rol = usuarioLocal?.rolUs

            // Redirige a AdminActivity si es Administrador o UsuarioActivity si es Usuario
            if (rol == "Administrador") {
                val intent = Intent(this, AdminActivity::class.java).apply {
                    putExtra("email", email)
                    putExtra("nombre", nombre)
                    putExtra("apellido", apellido)
                }
                startActivity(intent)
                finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
            } else {
                val intent = Intent(this, UsuarioActivity::class.java).apply {
                    putExtra("email", email)
                    putExtra("nombre", nombre)
                    putExtra("apellido", apellido)
                }
                startActivity(intent)
                finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
            }
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
        }
    }
    // Función para leer los datos de la base de datos y mostrarlos en el RecyclerView
    private fun leer() {
        val data = db.readDataNAT()// Lee los datos de NAT desde la base de datos
        if (data.isNotEmpty()) {
            listaNats.clear()// Limpia la lista antes de agregar nuevos datos
            listaNats.addAll(data) // Agrega los datos leídos a la lista

            // Asigna el adaptador al RecyclerView
            adapter = NatAdapter(this, listaNats)
            recyclerViewNats?.adapter = adapter
            // Desplaza el RecyclerView a la última posición
            recyclerViewNats?.layoutManager?.scrollToPosition(listaNats.size - 1)
        }
    }

    // Función para filtrar la lista de NAT según el texto ingresado en la SearchView
    private fun filterList(query: String?){
        if (query != null){
            val filteredList = ArrayList<Nat>()

            // Recorre la lista de NAT y agrega las que coinciden con la búsqueda
            for (i in listaNats){
                if (i.id.toString().contains(query)){

                    filteredList.add(i)
                }
            }

            // Si no se encuentra ninguna coincidencia, muestra un mensaje
            if (filteredList.isEmpty()){
                Toast.makeText(this, "Configuración NAT no encontrada", Toast.LENGTH_SHORT).show()
            }
            else{
                // Si hay coincidencias, actualiza la lista en el adaptador
                adapter?.setFilteredList(filteredList)
            }
        }
    }

    fun getUserRole(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userRole", null)  // Retorna el rol o null si no está guardado
    }
}
