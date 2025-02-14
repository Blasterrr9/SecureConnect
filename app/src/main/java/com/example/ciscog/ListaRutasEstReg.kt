package com.example.ciscog

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciscog.adapters.RutasEstAdapter
import com.example.ciscog.models.RutasEst
import kotlin.collections.ArrayList

class ListaRutasEstReg : AppCompatActivity() {
    // Variables para los componentes de la interfaz
    private var recyclerViewRutasE: RecyclerView? = null
    private var adapter: RutasEstAdapter? = null
    private var listaRutasE = ArrayList<RutasEst>()
    private var db = DataBaseLITE(this) // Instancia de la base de datos local
    private var btnRegistrar: Button? = null
    private var searchView: SearchView? = null
    private var btnRegresar: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_rutas_est_reg)

        val bundle = intent.extras
        val email = bundle?.getString("email")

        // Inicializa los componentes de la interfaz
        init()

        // Configura los listeners para las acciones del usuario
        listeners(email.toString())
    }

    // Función para inicializar los elementos de la interfaz
    private fun init() {
        recyclerViewRutasE = findViewById(R.id.recyclerview_rutas_est) // RecyclerView para mostrar la lista de rutas estáticas
        searchView = findViewById(R.id.searchview_rutas_est) // SearchView para buscar en la lista
        btnRegistrar = findViewById(R.id.registrar_btn) // Botón para registrar una nueva ruta estática
        btnRegresar = findViewById(R.id.return_btn) // Botón para regresar a la actividad anterior

        // Configura el layout manager para el RecyclerView
        recyclerViewRutasE?.layoutManager = LinearLayoutManager(this)

        // Verificar el rol del usuario
        val usuarioRol = getUserRole(this) // Esta función debe obtener el rol del usuario

        btnRegistrar?.let { button ->
            // Si el rol es "Usuario", oculta el botón de registro
            if (usuarioRol.equals("Usuario", ignoreCase = true)) {
                button.visibility = View.GONE
                // Ajusta el margen inferior del RecyclerView para dejar espacio para el FrameLayout
                val params = recyclerViewRutasE?.layoutParams as ViewGroup.MarginLayoutParams
                params.bottomMargin = 470 // 180dp en píxeles
                recyclerViewRutasE?.layoutParams = params
            } else {
                button.visibility = View.VISIBLE
            }
        }

        // Carga los datos desde la base de datos y los muestra en el RecyclerView
        leer()
    }

    // Función para definir los listeners (acciones al hacer clic)
    private fun listeners(email: String) {
        // Listener para manejar la búsqueda de rutas
        searchView?.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false // No es necesario manejar el evento de submit
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                // Filtra la lista según el texto ingresado por el usuario
                filterList(newText)
                return true
            }
        })

        // Listener para el botón de registrar, que inicia la actividad de registro de rutas
        btnRegistrar?.setOnClickListener {
            val intent = Intent(this, RutasEstCRUD::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish() // Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
        }

        // Listener para el botón de regresar, que regresa a la actividad anterior
        btnRegresar?.setOnClickListener {
            finish()// Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
        }
    }

    // Función para leer los datos de la base de datos y mostrarlos en el RecyclerView
    private fun leer() {
        val data = db.readDataRutaEst() // Lee los datos de rutas estáticas desde la base de datos
        if (data.isNotEmpty()) {
            listaRutasE.clear() // Limpia la lista antes de agregar nuevos datos
            listaRutasE.addAll(data) // Agrega los datos leídos a la lista

            // Asigna el adaptador al RecyclerView
            adapter = RutasEstAdapter(this, listaRutasE)
            recyclerViewRutasE?.adapter = adapter

            // Desplaza el RecyclerView a la última posición
            recyclerViewRutasE?.layoutManager?.scrollToPosition(listaRutasE.size - 1)
        }
    }

    /*
    Función para filtrar la lista de rutas según el texto ingresado en la SearchView
    Recibe el texto de búsqueda ingresado por el usuario
    Muestra un mensaje si no se encuentra ninguna coincidencia
    */
    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<RutasEst>() // Lista temporal para almacenar las rutas filtradas

            // Recorre la lista de rutas y agrega las que coinciden con la búsqueda
            for (ruta in listaRutasE) {
                if (ruta.id.toString().contains(query)) {
                    filteredList.add(ruta)
                }
            }

            // Si no se encuentra ninguna coincidencia, muestra un mensaje
            if (filteredList.isEmpty()) {
                Toast.makeText(this, "Ruta no encontrada", Toast.LENGTH_SHORT).show()
            } else {
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
