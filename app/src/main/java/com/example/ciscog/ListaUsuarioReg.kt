package com.example.ciscog

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.ciscog.adapters.UsuarioAdapter
import com.example.ciscog.models.Usuario
import kotlin.collections.ArrayList

class ListaUsuarioReg : AppCompatActivity() {
    // Variables para los componentes de la interfaz
    private var recyclerViewUsuario: RecyclerView? = null
    private var adapter: UsuarioAdapter? = null
    private var listaUsuario = ArrayList<Usuario>()
    private var db = DataBaseLITE(this) // Instancia de la base de datos local
    private var btnRegistrar: Button? = null
    private var searchView: SearchView? = null
    private var btnRegresar: ImageButton? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_lista_usuario_reg)

        val email = intent.getStringExtra("email")

        // Inicializa los componentes de la interfaz
        init(email.toString())

        // Configura los listeners para las acciones del usuario
        listeners(email.toString())
    }

    // Función para inicializar los elementos de la interfaz
    private fun init(email: String) {
        recyclerViewUsuario = findViewById(R.id.recyclerview_usuario) // RecyclerView para mostrar la lista de usuarios
        searchView = findViewById(R.id.searchview_usuario) // SearchView para buscar en la lista
        btnRegistrar = findViewById(R.id.registrar_btn) // Botón para registrar un nuevo usuarios
        btnRegresar = findViewById(R.id.return_btn) // Botón para regresar a la actividad anterior
        // Configura el layout manager para el RecyclerView
        recyclerViewUsuario?.layoutManager = LinearLayoutManager(this)

        // Carga los datos desde la base de datos y los muestra en el RecyclerView
        leer(email)
    }

    // Función para definir los listeners (acciones al hacer clic)
    private fun listeners(email: String) {
        // Listener para manejar la búsqueda de usuarios
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

        // Listener para el botón de registrar, que inicia la actividad de registro de usuarios
        btnRegistrar?.setOnClickListener {
            val intent = Intent(this, UsuarioCRUD::class.java).apply {
                putExtra("email", email)
            }
            startActivity(intent)
            finish() // Finaliza la actividad actual para evitar volver a ella con el botón de retroceso
        }

        // Listener para el botón de regresar, que regresa a la actividad anterior
        btnRegresar?.setOnClickListener {
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
        }
    }

    // Función para leer los datos de la base de datos y mostrarlos en el RecyclerView
    private fun leer(email: String) {
        val data = db.readDataUsuario() // Lee los datos de rutas estáticas desde la base de datos
        if (data.isNotEmpty()) {
            listaUsuario.clear() // Limpia la lista antes de agregar nuevos datos
            listaUsuario.addAll(data) // Agrega los datos leídos a la lista

            // Asigna el adaptador al RecyclerView
            adapter = UsuarioAdapter(this, listaUsuario, email)
            recyclerViewUsuario?.adapter = adapter

            // Desplaza el RecyclerView a la última posición
            recyclerViewUsuario?.layoutManager?.scrollToPosition(listaUsuario.size - 1)
        }
    }

    /*
    Función para filtrar la lista de usuarios según el texto ingresado en la SearchView
    Recibe el texto de búsqueda ingresado por el usuario
    Muestra un mensaje si no se encuentra ninguna coincidencia
    */
    private fun filterList(query: String?) {
        if (query != null) {
            val filteredList = ArrayList<Usuario>() // Lista temporal para almacenar los usuarios filtrados

            // Recorre la lista de usuarios y agrega las que coinciden con la búsqueda
            for (ruta in listaUsuario) {
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
}
