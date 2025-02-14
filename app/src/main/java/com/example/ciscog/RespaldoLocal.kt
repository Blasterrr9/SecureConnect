package com.example.ciscog

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import java.io.File

class RespaldoLocal : AppCompatActivity() {
    private var btnRegresar: ImageButton? = null
    private var btnRespaldar: Button? = null
    private var btnRestaurar: Button? = null

    // Registrador de resultado para manejar la selección de archivos
    private val openFileLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            // Restaurar la base de datos desde el archivo seleccionado
            restoreDatabase(it)
        } ?: run {
            Toast.makeText(this, "No se seleccionó ningún archivo", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_respaldo_local)

        init()
        listeners()
    }

    private fun init() {
        btnRegresar = findViewById(R.id.return_btn)
        btnRespaldar = findViewById(R.id.btnRespaldar)
        btnRestaurar = findViewById(R.id.btnRestaurar)
    }

    private fun listeners() {
        // Listener para el botón de regresar, que regresa a la actividad anterior
        btnRegresar?.setOnClickListener {
            finish() // Finaliza la actividad actual
        }

        // Listener para el botón de respaldar, que genera el respaldo de la base de datos
        btnRespaldar?.setOnClickListener {
            val backupFilePath = backupDatabase()
            Toast.makeText(this, "Respaldo Generado Exitosamente en $backupFilePath", Toast.LENGTH_LONG).show()
        }

        // Listener para el botón de restaurar, que permite seleccionar un archivo para restaurar
        btnRestaurar?.setOnClickListener {
            openFileLauncher.launch(arrayOf("*/*")) // Lanza el selector de archivos
        }
    }

    // Función que realiza el respaldo de la base de datos
    private fun backupDatabase(): String {
        val db = DataBaseLITE(this)
        return db.backupDatabaseToSQLFile(this)
    }

    // Función que realiza la restauración de la base de datos desde el archivo seleccionado
    private fun restoreDatabase(fileUri: Uri) {
        val db = DataBaseLITE(this)
        try {
            db.restoreDatabaseFromSQLFile(this, fileUri)
            Snackbar.make(findViewById(android.R.id.content), "Restauración Exitosa", Snackbar.LENGTH_LONG).show()
        } catch (e: Exception) {
            Snackbar.make(findViewById(android.R.id.content), "Error al restaurar base de datos", Snackbar.LENGTH_LONG).show()
        }
    }
}
