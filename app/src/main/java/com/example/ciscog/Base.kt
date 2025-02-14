package com.example.ciscog

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.widget.Toast
import com.example.ciscog.models.Acl
import com.example.ciscog.models.Configuracion
import com.example.ciscog.models.Dispositivo
import com.example.ciscog.models.Eigrp
import com.example.ciscog.models.Nat
import com.example.ciscog.models.Ospf
import com.example.ciscog.models.Reporte
import com.example.ciscog.models.RutasEst
import com.example.ciscog.models.Usuario
import com.example.ciscog.models.Vlan
import com.example.ciscog.models.Stp
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.ZoneId

const val DATABASE_NAME ="appCisco1"
const val COL_ID = "id"
const val COL_DISPOSITIVO = "dispositivo"

//Variables usuario
const val TABLE_USUARIO="Usuario"
const val COL_NOMBRE = "nombre_usuario"
const val COL_APELLIDO = "apellido_usuario"
const val COL_EMAIL = "email_usuario"
const val COL_CONTRASENA = "contrasena_usuario"
const val COL_ROL = "rol_usuario"
const val COL_FECHA = "fecha_usuario"
//Variables nat
const val TABLE_NAT="NAT"
const val COL_TIPO = "tipoNat"
const val COL_IPPRIVADA = "ip_privada"
const val COL_IPPUBLICA = "ip_publica"
//Variables rutas estáticas
const val TABLE_RUTASEST = "RutaEstatica"
const val COL_IPDESTINO = "ip_destino"
const val COL_MASCARA = "mascara" 
const val COL_IPSALTO = "ip_salto"
//Variables eigrp
const val TABLE_EIGRP = "EIGRP"
const val COL_ASNUMBER = "as_number"
const val COL_RED = "red"
const val COL_WILDCARD = "wildcard"
//Variables ospf
const val TABLE_OSPF = "OSPF"
const val COL_AREA = "area"
//Variables vlan
const val TABLE_VLAN = "VLAN"
const val COL_VLAN_ID = "vlan_id"
const val COL_NOMBRE_VLAN = "nombre_vlan"
//Variables stp
const val TABLE_STP = "STP"
const val COL_PRIORIDAD = "prioridad"
const val COL_MODO = "modo"
//Variables acl
const val TABLE_ACL = "ACL"
const val COL_TIPO_ACL = "tipo_acl"
const val COL_REGLA = "regla"
//Variables configuracion
const val TABLE_CONFIGURACION = "Configuracion"
const val COL_ID_CONFIGURACION = "idConfiguracion"
const val COL_NOMBRE_CONFIGURACION = "nombre_configuracion"
const val COL_DESCRIPCION_CONFIGURACION = "descripcion_configuracion"
const val COL_TIPO_CONFIGURACION = "tipo_configuracion"
const val COL_FECHA_CONFIGURACION = "fecha_configuracion"
const val COL_ID_USUARIO = "id_usuario"
const val COL_ID_DISPOSITIVO = "id_dispositivo"
//Variables dispositivo
const val TABLE_DISPOSITIVO = "Dispositivo"
const val COL_NOMBRE_DISPOSITIVO = "nombre_dispositivo"
const val COL_TIPO_DISPOSITIVO = "tipo_dispositivo"
const val COL_IP_DISPOSITIVO = "ip_dispositivo"
//Variables reporte
const val TABLE_REPORTE = "Reporte"
const val COL_TIPO_REPORTE = "tipo_reporte"
const val COL_ATRIBUTO1_TITULO = "atributo1_titulo"
const val COL_ATRIBUTO1_VALOR = "atributo1_valor"
const val COL_ATRIBUTO2_TITULO = "atributo2_titulo"
const val COL_ATRIBUTO2_VALOR = "atributo2_valor"
const val COL_ATRIBUTO3_TITULO = "atributo3_titulo"
const val COL_ATRIBUTO3_VALOR = "atributo3_valor"
const val COL_ATRIBUTO4_TITULO = "atributo4_titulo"
const val COL_ATRIBUTO4_VALOR = "atributo4_valor"

class DataBaseLITE(var context: Context) : SQLiteOpenHelper(context,DATABASE_NAME,null,16) {
    override fun onCreate(db: SQLiteDatabase?) {

        val createTableUsuario = "CREATE TABLE $TABLE_USUARIO (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_NOMBRE VARCHAR(40)," +
                "$COL_APELLIDO VARCHAR(40)," +
                "$COL_EMAIL VARCHAR(100)," +
                "$COL_CONTRASENA VARCHAR(255)," +
                "$COL_ROL VARCHAR(40)," +
                "$COL_FECHA TIMESTAMP DEFAULT CURRENT_TIMESTAMP);"

        val createTableDispositivo = "CREATE TABLE $TABLE_DISPOSITIVO (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_NOMBRE_DISPOSITIVO VARCHAR(100)," +
                "$COL_TIPO_DISPOSITIVO VARCHAR(20)," +
                "$COL_IP_DISPOSITIVO VARCHAR(40));"

        val createTableConfiguracion = "CREATE TABLE $TABLE_CONFIGURACION (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_NOMBRE_CONFIGURACION VARCHAR(100)," +
                "$COL_DESCRIPCION_CONFIGURACION TEXT," +
                "$COL_TIPO_CONFIGURACION VARCHAR(10)," +
                "$COL_FECHA_CONFIGURACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "$COL_ID_USUARIO INTEGER," +
                "$COL_ID_DISPOSITIVO INTEGER," +
                "FOREIGN KEY($COL_ID_USUARIO) REFERENCES $TABLE_USUARIO($COL_ID)," +
                "FOREIGN KEY($COL_ID_DISPOSITIVO) REFERENCES $TABLE_DISPOSITIVO($COL_ID));"

        val createTableNAT = "CREATE TABLE $TABLE_NAT (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TIPO VARCHAR(40)," +
                "$COL_IPPRIVADA VARCHAR(40)," +
                "$COL_IPPUBLICA VARCHAR(40)," +
                "$COL_DISPOSITIVO VARCHAR(40)," +
                "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

        val createTableRutaEst = "CREATE TABLE $TABLE_RUTASEST (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_IPDESTINO VARCHAR(40)," +
                "$COL_IPSALTO VARCHAR(40)," +
                "$COL_MASCARA VARCHAR(40)," +
                "$COL_DISPOSITIVO VARCHAR(40)," +
                "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

        val createTableEigrp = "CREATE TABLE $TABLE_EIGRP (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_ASNUMBER INTEGER," +
                "$COL_RED VARCHAR(40)," +
                "$COL_WILDCARD VARCHAR(40)," +
                "$COL_DISPOSITIVO VARCHAR(40)," +
                "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

        val createTableOspf = "CREATE TABLE $TABLE_OSPF (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_AREA INTEGER," +
                "$COL_RED VARCHAR(40)," +
                "$COL_WILDCARD VARCHAR(40)," +
                "$COL_DISPOSITIVO VARCHAR(40)," +
                "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

        val createTableVlan = "CREATE TABLE $TABLE_VLAN (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_VLAN_ID INTEGER," +
                "$COL_NOMBRE_VLAN VARCHAR(40)," +
                "$COL_DISPOSITIVO VARCHAR(40)," +
                "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

        val createTableSTP = "CREATE TABLE $TABLE_STP (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_PRIORIDAD INTEGER," +
                "$COL_VLAN_ID INTEGER," +
                "$COL_MODO VARCHAR(40)," +
                "$COL_DISPOSITIVO VARCHAR(40)," +
                "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

        val createTableACL = "CREATE TABLE $TABLE_ACL (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TIPO_ACL VARCHAR(40)," +
                "$COL_REGLA TEXT," +
                "$COL_DISPOSITIVO VARCHAR(40)," +
                "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

        val createTableReporte = "CREATE TABLE $TABLE_REPORTE (" +
                "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "$COL_TIPO_REPORTE VARCHAR(40)," +
                "$COL_ATRIBUTO1_TITULO VARCHAR(40)," +
                "$COL_ATRIBUTO1_VALOR TEXT," +
                "$COL_ATRIBUTO2_TITULO VARCHAR(40)," +
                "$COL_ATRIBUTO2_VALOR TEXT, " +
                "$COL_ATRIBUTO3_TITULO VARCHAR(40)," +
                "$COL_ATRIBUTO3_VALOR TEXT, " +
                "$COL_ATRIBUTO4_TITULO VARCHAR(40)," +
                "$COL_ATRIBUTO4_VALOR TEXT);"

        db?.execSQL(createTableUsuario)
        db?.execSQL(createTableDispositivo)
        db?.execSQL(createTableConfiguracion)
        db?.execSQL(createTableEigrp)
        db?.execSQL(createTableOspf)
        db?.execSQL(createTableVlan)
        db?.execSQL(createTableSTP)
        db?.execSQL(createTableACL)
        db?.execSQL(createTableRutaEst)
        db?.execSQL(createTableNAT)
        db?.execSQL(createTableReporte)
    }
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion < newVersion) {
            db?.execSQL("DELETE FROM $TABLE_USUARIO WHERE $COL_ID = 10;")
            /*db?.execSQL("DROP TABLE IF EXISTS $TABLE_EIGRP;")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_OSPF;")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_VLAN;")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_STP;")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_ACL;")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_NAT;")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_RUTASEST;")
            db?.execSQL("DROP TABLE IF EXISTS $TABLE_CONFIGURACION;")

            val createTableConfiguracion = "CREATE TABLE $TABLE_CONFIGURACION (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_NOMBRE_CONFIGURACION VARCHAR(100)," +
                    "$COL_DESCRIPCION_CONFIGURACION TEXT," +
                    "$COL_TIPO_CONFIGURACION VARCHAR(10)," +
                    "$COL_FECHA_CONFIGURACION TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    "$COL_ID_USUARIO INTEGER," +
                    "$COL_ID_DISPOSITIVO INTEGER," +
                    "FOREIGN KEY($COL_ID_USUARIO) REFERENCES $TABLE_USUARIO($COL_ID)," +
                    "FOREIGN KEY($COL_ID_DISPOSITIVO) REFERENCES $TABLE_DISPOSITIVO($COL_ID));"

            val createTableNAT = "CREATE TABLE $TABLE_NAT (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_TIPO VARCHAR(40)," +
                    "$COL_IPPRIVADA VARCHAR(40)," +
                    "$COL_IPPUBLICA VARCHAR(40)," +
                    "$COL_DISPOSITIVO VARCHAR(40)," +
                    "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

            val createTableRutaEst = "CREATE TABLE $TABLE_RUTASEST (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_IPDESTINO VARCHAR(40)," +
                    "$COL_IPSALTO VARCHAR(40)," +
                    "$COL_MASCARA VARCHAR(40)," +
                    "$COL_DISPOSITIVO VARCHAR(40)," +
                    "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

            val createTableEigrp = "CREATE TABLE $TABLE_EIGRP (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_ASNUMBER INTEGER," +
                    "$COL_RED VARCHAR(40)," +
                    "$COL_WILDCARD VARCHAR(40)," +
                    "$COL_DISPOSITIVO VARCHAR(40)," +
                    "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

            val createTableOspf = "CREATE TABLE $TABLE_OSPF (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_AREA INTEGER," +
                    "$COL_RED VARCHAR(40)," +
                    "$COL_WILDCARD VARCHAR(40)," +
                    "$COL_DISPOSITIVO VARCHAR(40)," +
                    "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

            val createTableVlan = "CREATE TABLE $TABLE_VLAN (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_VLAN_ID INTEGER," +
                    "$COL_NOMBRE_VLAN VARCHAR(40)," +
                    "$COL_DISPOSITIVO VARCHAR(40)," +
                    "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

            val createTableSTP = "CREATE TABLE $TABLE_STP (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_PRIORIDAD INTEGER," +
                    "$COL_VLAN_ID INTEGER," +
                    "$COL_MODO VARCHAR(40)," +
                    "$COL_DISPOSITIVO VARCHAR(40)," +
                    "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"

            val createTableACL = "CREATE TABLE $TABLE_ACL (" +
                    "$COL_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                    "$COL_TIPO_ACL VARCHAR(40)," +
                    "$COL_REGLA TEXT," +
                    "$COL_DISPOSITIVO VARCHAR(40)," +
                    "$COL_ID_CONFIGURACION INTEGER REFERENCES $TABLE_CONFIGURACION($COL_ID));"


            db?.execSQL(createTableConfiguracion)
            db?.execSQL(createTableEigrp)
            db?.execSQL(createTableOspf)
            db?.execSQL(createTableVlan)
            db?.execSQL(createTableSTP)
            db?.execSQL(createTableACL)
            db?.execSQL(createTableRutaEst)
            db?.execSQL(createTableNAT)
*/
        }
    }
    
    //Funciones para NAT
    fun insertDataNAT(nat : Nat, email: String) {
        val db = this.writableDatabase
        val usuario = getUsuario(email)
        val dispositivo = getDispositivo(nat.dispositivo)
        val cv = ContentValues()
        cv.put(COL_TIPO, nat.tipo)
        cv.put(COL_IPPRIVADA, nat.ipPriv)
        cv.put(COL_IPPUBLICA, nat.ipPub)
        cv.put(COL_DISPOSITIVO, nat.dispositivo)

        db.beginTransaction()

        try {
            val result = db.insert(TABLE_NAT, null, cv)
            if (result == -1L) {
                Toast.makeText(context, "Fallo al Insertar NAT", Toast.LENGTH_SHORT).show()
                return // Terminar la función si falla la inserción de NAT
            } else {
                Toast.makeText(context, "NAT Insertada Correctamente", Toast.LENGTH_SHORT).show()

                // Preparar los valores para la tabla Configuracion
                val nombreConfiguracion = "NAT en ${nat.dispositivo}"
                val descripcionConfiguracion = "NAT de Tipo ${nat.tipo} de IP Privada ${nat.ipPriv} a IP Pública ${nat.ipPub} en el dispositivo ${nat.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

                // Insertar en la tabla Configuracion
                val cvConfig = ContentValues().apply {
                    put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                    put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                    put(COL_TIPO_CONFIGURACION, "NAT")
                    put(COL_ID_USUARIO, usuario.id)
                    put(COL_ID_DISPOSITIVO, dispositivo!!.id)
                }

                val resultConfig = db.insert(TABLE_CONFIGURACION, null, cvConfig)
                if (resultConfig == -1L) {
                    Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                } else {
                    val cvNat = ContentValues().apply {
                        put(COL_ID_CONFIGURACION, getLastInsertedConfigId())
                    }
                    val resultNat = db.update(TABLE_NAT, cvNat, "$COL_ID = ?", arrayOf(result.toString()))
                    if (resultNat != -1) {
                        Toast.makeText(context, "Registro en Configuración Insertado Correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }

    }
    
    fun readDataNAT() : MutableList<Nat>{
        val list : MutableList<Nat> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_NAT"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) do {
            val nat = Nat()
            nat.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
            nat.tipo = result.getString(result.getColumnIndexOrThrow(COL_TIPO))
            nat.ipPriv = result.getString(result.getColumnIndexOrThrow(COL_IPPRIVADA))
            nat.ipPub = result.getString(result.getColumnIndexOrThrow(COL_IPPUBLICA))
            nat.dispositivo = result.getString(result.getColumnIndexOrThrow(COL_DISPOSITIVO))
            list.add(nat)
        }while (result.moveToNext())

        result.close()
        db.close()
        return list
    }

    fun updateDataNAT(nat: Nat): Boolean {
        val db = this.writableDatabase
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        val usuario = getUsuario(email!!)
        var resultNAT: Int
        var resultConfig: Int

        db.beginTransaction() // Iniciar la transacción

        try {
            // Actualizar la tabla NAT
            val contentValuesNAT = ContentValues().apply {
                put(COL_TIPO, nat.tipo)
                put(COL_IPPRIVADA, nat.ipPriv)
                put(COL_IPPUBLICA, nat.ipPub)
                put(COL_DISPOSITIVO, nat.dispositivo)
            }
            resultNAT = db.update(TABLE_NAT, contentValuesNAT, "ID = ?", arrayOf(nat.id.toString()))

            if (resultNAT == -1) {
                throw Exception("Fallo al actualizar NAT")
            }

            // Preparar los valores para la tabla CONFIGURACION
            val nombreConfiguracion = "NAT en ${nat.dispositivo}"
            val descripcionConfiguracion = "NAT de Tipo ${nat.tipo} de IP Privada ${nat.ipPriv} a IP Pública ${nat.ipPub} en el dispositivo ${nat.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

            val contentValuesConfig = ContentValues().apply {
                put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                put(COL_TIPO_CONFIGURACION, "NAT")
                put(COL_FECHA_CONFIGURACION, dateTime.format(formatter))
                put(COL_ID_USUARIO, usuario.id)
                put(COL_ID_DISPOSITIVO, getDispositivo(nat.dispositivo)!!.id)
            }

            // Actualizar la tabla CONFIGURACION (usar un identificador adecuado)
            resultConfig = db.update(TABLE_CONFIGURACION, contentValuesConfig, "$COL_ID = ?", arrayOf(buscarIdConfiguracion(nat.id,"NAT").toString()))

            if (resultConfig == -1) {
                throw Exception("Fallo al actualizar Configuración")
            }

            db.setTransactionSuccessful() // Confirmar la transacción

        } catch (e: Exception) {
            e.printStackTrace()
            return false // Retornar false en caso de error

        } finally {
            db.endTransaction() // Finalizar la transacción
        }

        return true // Retornar true si ambas actualizaciones fueron exitosas
    }


    fun deleteDataNAT(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAT, "ID = ?", arrayOf(id.toString()))
    }

    fun isNATExist(tipo: String, ipPriv: String, ipPub: String, dispositivo: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_NAT WHERE $COL_TIPO = ? AND $COL_IPPRIVADA = ? AND $COL_IPPUBLICA = ? AND $COL_DISPOSITIVO = ?"
        val cursor = db.rawQuery(query, arrayOf(tipo, ipPriv, ipPub, dispositivo))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    //Funciones para Rutas Estáticas
    fun insertDataRutaEst(rutaEst : RutasEst, email: String) {
        val db = this.writableDatabase
        val usuario = getUsuario(email)
        val dispositivo = getDispositivo(rutaEst.dispositivo)
        val cv = ContentValues()
        cv.put(COL_IPDESTINO, rutaEst.ipDestino)
        cv.put(COL_IPSALTO, rutaEst.ipSalto)
        cv.put(COL_MASCARA, rutaEst.mascara)
        cv.put(COL_DISPOSITIVO, rutaEst.dispositivo)

        db.beginTransaction()

        try {
            val result = db.insert(TABLE_RUTASEST, null, cv)
            if (result == -1L) {
                Toast.makeText(context, "Fallo al Insertar Ruta Estática", Toast.LENGTH_SHORT).show()
                return // Terminar la función si falla la inserción de la Ruta Estática
            } else {
                Toast.makeText(context, "Ruta Estática Insertada Correctamente", Toast.LENGTH_SHORT).show()

                // Preparar los valores para la tabla Configuracion
                val nombreConfiguracion = "Ruta Estática en ${rutaEst.dispositivo}"
                val descripcionConfiguracion = "Ruta Estática para permitir el tráfico desde ${rutaEst.ipDestino} con Máscara ${rutaEst.mascara} hacia ${rutaEst.ipSalto} en el dispositivo ${rutaEst.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

                // Insertar en la tabla Configuracion
                val cvConfig = ContentValues().apply {
                    put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                    put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                    put(COL_TIPO_CONFIGURACION, "RutaEst")
                    put(COL_ID_USUARIO, usuario.id)
                    put(COL_ID_DISPOSITIVO, dispositivo!!.id)
                }

                val resultConfig = db.insert(TABLE_CONFIGURACION, null, cvConfig)
                if (resultConfig == -1L) {
                    Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                } else {
                    val cvRutasEst = ContentValues().apply {
                        put(COL_ID_CONFIGURACION, getLastInsertedConfigId())
                    }
                    val resultNat = db.update(TABLE_RUTASEST, cvRutasEst, "$COL_ID = ?", arrayOf(result.toString()))
                    if (resultNat != -1) {
                        Toast.makeText(context, "Registro en Configuración Insertado Correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun readDataRutaEst() : MutableList<RutasEst>{
        val list : MutableList<RutasEst> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_RUTASEST"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) do {
            val rutaEst = RutasEst()
            rutaEst.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
            rutaEst.ipDestino = result.getString(result.getColumnIndexOrThrow(COL_IPDESTINO))
            rutaEst.ipSalto = result.getString(result.getColumnIndexOrThrow(COL_IPSALTO))
            rutaEst.mascara = result.getString(result.getColumnIndexOrThrow(COL_MASCARA))
            rutaEst.dispositivo = result.getString(result.getColumnIndexOrThrow(COL_DISPOSITIVO))
            list.add(rutaEst)
        }while (result.moveToNext())

        result.close()
        db.close()
        return list
    }

    fun updateDataRutaEst(rutaEst: RutasEst): Boolean {
        val db = this.writableDatabase
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        val usuario = getUsuario(email!!)
        var resultRutasEst: Int
        var resultConfig: Int

        db.beginTransaction() // Iniciar la transacción

        try {
            // Actualizar la tabla NAT
            val contentValuesRutasEst = ContentValues().apply {
                put(COL_IPDESTINO, rutaEst.ipDestino)
                put(COL_IPSALTO, rutaEst.ipSalto)
                put(COL_MASCARA, rutaEst.mascara)
                put(COL_DISPOSITIVO, rutaEst.dispositivo)
            }
            resultRutasEst = db.update(TABLE_RUTASEST, contentValuesRutasEst, "ID = ?", arrayOf(rutaEst.id.toString()))

            if (resultRutasEst == -1) {
                throw Exception("Fallo al actualizar la Ruta Estática")
            }

            // Preparar los valores para la tabla CONFIGURACION
            val nombreConfiguracion = "Ruta Estática en ${rutaEst.dispositivo}"
            val descripcionConfiguracion = "Ruta Estática para permitir el tráfico desde ${rutaEst.ipDestino} con Máscara ${rutaEst.mascara} hacia ${rutaEst.ipSalto} en el dispositivo ${rutaEst.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

            val contentValuesConfig = ContentValues().apply {
                put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                put(COL_TIPO_CONFIGURACION, "RutaEst")
                put(COL_FECHA_CONFIGURACION, dateTime.format(formatter))
                put(COL_ID_USUARIO, usuario.id)
                put(COL_ID_DISPOSITIVO, getDispositivo(rutaEst.dispositivo)!!.id)
            }

            // Actualizar la tabla CONFIGURACION (usar un identificador adecuado)
            resultConfig = db.update(TABLE_CONFIGURACION, contentValuesConfig, "$COL_ID = ?", arrayOf(buscarIdConfiguracion(rutaEst.id,"RutaEst").toString()))

            if (resultConfig == -1) {
                throw Exception("Fallo al actualizar Configuración")
            }

            db.setTransactionSuccessful() // Confirmar la transacción

        } catch (e: Exception) {
            e.printStackTrace()
            return false // Retornar false en caso de error

        } finally {
            db.endTransaction() // Finalizar la transacción
        }

        return true // Retornar true si ambas actualizaciones fueron exitosas
    }

    fun deleteDataRutaEst(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_RUTASEST, "ID = ?", arrayOf(id.toString()))
    }

    fun isRutaExist(ipDestino: String, mascara: String, ipSalto: String, dispositivo: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_RUTASEST WHERE $COL_IPDESTINO = ? AND $COL_MASCARA = ? AND $COL_IPSALTO = ? AND $COL_DISPOSITIVO = ?"
        val cursor = db.rawQuery(query, arrayOf(ipDestino, mascara, ipSalto, dispositivo))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    //Funciones para EIGRP
    fun insertDataEigrp(eigrp : Eigrp, email: String) {
        val db = this.writableDatabase
        val usuario = getUsuario(email)
        val dispositivo = getDispositivo(eigrp.dispositivo)
        val cv = ContentValues()
        cv.put(COL_ASNUMBER, eigrp.asnumber)
        cv.put(COL_RED, eigrp.red)
        cv.put(COL_WILDCARD, eigrp.wildcard)
        cv.put(COL_DISPOSITIVO, eigrp.dispositivo)

        db.beginTransaction()

        try {
            val result = db.insert(TABLE_EIGRP, null, cv)
            if (result == -1L) {
                Toast.makeText(context, "Fallo al Insertar Configuración de EIGRP", Toast.LENGTH_SHORT).show()
                return // Terminar la función si falla la inserción de EIGRP
            } else {
                Toast.makeText(context, "Configuración de EIGRP Insertada Correctamente", Toast.LENGTH_SHORT).show()

                // Preparar los valores para la tabla Configuracion
                val nombreConfiguracion = "Configuración de EIGRP en ${eigrp.dispositivo}"
                val descripcionConfiguracion = "Protocolo de EIGRP con As Number ${eigrp.asnumber} para la red ${eigrp.red} con la Wildcard ${eigrp.wildcard} en el dispositivo ${eigrp.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

                // Insertar en la tabla Configuracion
                val cvConfig = ContentValues().apply {
                    put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                    put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                    put(COL_TIPO_CONFIGURACION, "EIGRP")
                    put(COL_ID_USUARIO, usuario.id)
                    put(COL_ID_DISPOSITIVO, dispositivo!!.id)
                }

                val resultConfig = db.insert(TABLE_CONFIGURACION, null, cvConfig)
                if (resultConfig == -1L) {
                    Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                } else {
                    val cvEIGRP = ContentValues().apply {
                        put(COL_ID_CONFIGURACION, getLastInsertedConfigId())
                    }
                    val resultEIGRP = db.update(TABLE_EIGRP, cvEIGRP, "$COL_ID = ?", arrayOf(result.toString()))
                    if (resultEIGRP != -1) {
                        Toast.makeText(context, "Registro en Configuración Insertado Correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun readDataEigrp() : MutableList<Eigrp>{
        val list : MutableList<Eigrp> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_EIGRP"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) do {
            val eigrp = Eigrp()
            eigrp.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
            eigrp.asnumber = result.getString(result.getColumnIndexOrThrow(COL_ASNUMBER)).toInt()
            eigrp.red = result.getString(result.getColumnIndexOrThrow(COL_RED))
            eigrp.wildcard = result.getString(result.getColumnIndexOrThrow(COL_WILDCARD))
            eigrp.dispositivo = result.getString(result.getColumnIndexOrThrow(COL_DISPOSITIVO))
            list.add(eigrp)
        }while (result.moveToNext())

        result.close()
        db.close()
        return list
    }

    fun updateDataEigrp(eigrp: Eigrp): Boolean {
        val db = this.writableDatabase
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        val usuario = getUsuario(email!!)
        var resultEIGRP: Int
        var resultConfig: Int

        db.beginTransaction() // Iniciar la transacción

        try {
            // Actualizar la tabla EIGRP
            val contentValuesEIGRP = ContentValues().apply {
                put(COL_ASNUMBER, eigrp.asnumber)
                put(COL_RED, eigrp.red)
                put(COL_WILDCARD, eigrp.wildcard)
                put(COL_DISPOSITIVO, eigrp.dispositivo)
            }
            resultEIGRP = db.update(TABLE_EIGRP, contentValuesEIGRP, "ID = ?", arrayOf(eigrp.id.toString()))

            if (resultEIGRP == -1) {
                throw Exception("Fallo al actualizar la configuración de EIGRP")
            }

            // Preparar los valores para la tabla CONFIGURACION
            val nombreConfiguracion = "Configuración de EIGRP en ${eigrp.dispositivo}"
            val descripcionConfiguracion = "Protocolo de EIGRP con As Number ${eigrp.asnumber} para la red ${eigrp.red} con la Wildcard ${eigrp.wildcard} en el dispositivo ${eigrp.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

            val contentValuesConfig = ContentValues().apply {
                put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                put(COL_TIPO_CONFIGURACION, "EIGRP")
                put(COL_FECHA_CONFIGURACION, dateTime.format(formatter))
                put(COL_ID_USUARIO, usuario.id)
                put(COL_ID_DISPOSITIVO, getDispositivo(eigrp.dispositivo)!!.id)
            }

            // Actualizar la tabla CONFIGURACION (usar un identificador adecuado)
            resultConfig = db.update(TABLE_CONFIGURACION, contentValuesConfig, "$COL_ID = ?", arrayOf(buscarIdConfiguracion(eigrp.id,"EIGRP").toString()))

            if (resultConfig == -1) {
                throw Exception("Fallo al actualizar Configuración")
            }

            db.setTransactionSuccessful() // Confirmar la transacción

        } catch (e: Exception) {
            e.printStackTrace()
            return false // Retornar false en caso de error

        } finally {
            db.endTransaction() // Finalizar la transacción
        }

        return true // Retornar true si ambas actualizaciones fueron exitosas
    }

        fun deleteDataEigrp(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_EIGRP, "ID = ?", arrayOf(id.toString()))
    }

    fun isEigrpExist(asNumber: Int, red: String, wildcard: String, dispositivo: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_EIGRP WHERE $COL_ASNUMBER = ? AND $COL_RED = ? AND $COL_WILDCARD = ? AND $COL_DISPOSITIVO = ?"
        val cursor = db.rawQuery(query, arrayOf(asNumber.toString(), red, wildcard, dispositivo))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    //Funciones para OSPF
    fun insertDataOspf(ospf : Ospf, email: String) {
        val db = this.writableDatabase
        val usuario = getUsuario(email)
        val dispositivo = getDispositivo(ospf.dispositivo)
        val cv = ContentValues()
        cv.put(COL_AREA, ospf.area)
        cv.put(COL_RED, ospf.red)
        cv.put(COL_WILDCARD, ospf.wildcard)
        cv.put(COL_DISPOSITIVO, ospf.dispositivo)

        db.beginTransaction()

        try {
            val result = db.insert(TABLE_OSPF, null, cv)
            if (result == -1L) {
                Toast.makeText(context, "Fallo al Insertar Configuración de OSPF", Toast.LENGTH_SHORT).show()
                return // Terminar la función si falla la inserción de OSPF
            } else {
                Toast.makeText(context, "Configuración de OSPF Insertada Correctamente", Toast.LENGTH_SHORT).show()

                // Preparar los valores para la tabla Configuracion
                val nombreConfiguracion = "Configuración de OSPF en ${ospf.dispositivo}"
                val descripcionConfiguracion = "Protocolo de OSPF con Area ${ospf.area} para la red ${ospf.red} con la Wildcard ${ospf.wildcard} en el dispositivo ${ospf.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

                // Insertar en la tabla Configuracion
                val cvConfig = ContentValues().apply {
                    put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                    put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                    put(COL_TIPO_CONFIGURACION, "OSPF")
                    put(COL_ID_USUARIO, usuario.id)
                    put(COL_ID_DISPOSITIVO, dispositivo!!.id)
                }

                val resultConfig = db.insert(TABLE_CONFIGURACION, null, cvConfig)
                if (resultConfig == -1L) {
                    Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                } else {
                    val cvOSPF = ContentValues().apply {
                        put(COL_ID_CONFIGURACION, getLastInsertedConfigId())
                    }
                    val resultOSPF = db.update(TABLE_OSPF, cvOSPF, "$COL_ID = ?", arrayOf(result.toString()))
                    if (resultOSPF != -1) {
                        Toast.makeText(context, "Registro en Configuración Insertado Correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun readDataOspf() : MutableList<Ospf>{
        val list : MutableList<Ospf> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_OSPF"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) do {
            val ospf = Ospf()
            ospf.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
            ospf.area = result.getString(result.getColumnIndexOrThrow(COL_AREA)).toInt()
            ospf.red = result.getString(result.getColumnIndexOrThrow(COL_RED))
            ospf.wildcard = result.getString(result.getColumnIndexOrThrow(COL_WILDCARD))
            ospf.dispositivo = result.getString(result.getColumnIndexOrThrow(COL_DISPOSITIVO))
            list.add(ospf)
        }while (result.moveToNext())

        result.close()
        db.close()
        return list
    }

    fun updateDataOspf(ospf: Ospf): Boolean {
        val db = this.writableDatabase
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        val usuario = getUsuario(email!!)
        var resultOSPF: Int
        var resultConfig: Int

        db.beginTransaction() // Iniciar la transacción

        try {
            // Actualizar la tabla EIGRP
            val contentValuesOSPF = ContentValues().apply {
                put(COL_AREA, ospf.area)
                put(COL_RED, ospf.red)
                put(COL_WILDCARD, ospf.wildcard)
                put(COL_DISPOSITIVO, ospf.dispositivo)
            }
            resultOSPF = db.update(TABLE_OSPF, contentValuesOSPF, "ID = ?", arrayOf(ospf.id.toString()))

            if (resultOSPF == -1) {
                throw Exception("Fallo al actualizar la configuración de OSPF")
            }

            // Preparar los valores para la tabla Configuracion
            val nombreConfiguracion = "Configuración de OSPF en ${ospf.dispositivo}"
            val descripcionConfiguracion = "Protocolo de OSPF con Area ${ospf.area} para la red ${ospf.red} con la Wildcard ${ospf.wildcard} en el dispositivo ${ospf.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

            val contentValuesConfig = ContentValues().apply {
                put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                put(COL_TIPO_CONFIGURACION, "OSPF")
                put(COL_FECHA_CONFIGURACION, dateTime.format(formatter))
                put(COL_ID_USUARIO, usuario.id)
                put(COL_ID_DISPOSITIVO, getDispositivo(ospf.dispositivo)!!.id)
            }

            // Actualizar la tabla CONFIGURACION (usar un identificador adecuado)
            resultConfig = db.update(TABLE_CONFIGURACION, contentValuesConfig, "$COL_ID = ?", arrayOf(buscarIdConfiguracion(ospf.id,"OSPF").toString()))

            if (resultConfig == -1) {
                throw Exception("Fallo al actualizar Configuración")
            }

            db.setTransactionSuccessful() // Confirmar la transacción

        } catch (e: Exception) {
            e.printStackTrace()
            return false // Retornar false en caso de error

        } finally {
            db.endTransaction() // Finalizar la transacción
        }

        return true // Retornar true si ambas actualizaciones fueron exitosas
    }

    fun deleteDataOspf(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_OSPF, "ID = ?", arrayOf(id.toString()))
    }

    fun isOspfExist(area: Int, red: String, wildcard: String, dispositivo: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_OSPF WHERE $COL_AREA = ? AND $COL_RED = ? AND $COL_WILDCARD = ? AND $COL_DISPOSITIVO = ?"
        val cursor = db.rawQuery(query, arrayOf(area.toString(), red, wildcard, dispositivo))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    //Funciones para VLAN
    fun insertDataVLAN(vlan : Vlan, email: String) {
        val db = this.writableDatabase
        val usuario = getUsuario(email)
        val dispositivo = getDispositivo(vlan.dispositivo)
        val cv = ContentValues()
        cv.put(COL_VLAN_ID, vlan.vlan_id)
        cv.put(COL_NOMBRE_VLAN, vlan.nombre)
        cv.put(COL_DISPOSITIVO, vlan.dispositivo)

        db.beginTransaction()

        try {
            val result = db.insert(TABLE_VLAN, null, cv)
            if (result == -1L) {
                Toast.makeText(context, "Fallo al Insertar VLAN", Toast.LENGTH_SHORT).show()
                return // Terminar la función si falla la inserción de la VLAN
            } else {
                Toast.makeText(context, "VLAN Insertada Correctamente", Toast.LENGTH_SHORT).show()

                // Preparar los valores para la tabla Configuracion
                val nombreConfiguracion = "VLAN ${vlan.vlan_id} en ${vlan.dispositivo}"
                val descripcionConfiguracion = "VLAN con ID ${vlan.vlan_id} y Nombre: ${vlan.nombre} en el dispositivo ${vlan.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

                // Insertar en la tabla Configuracion
                val cvConfig = ContentValues().apply {
                    put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                    put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                    put(COL_TIPO_CONFIGURACION, "VLAN")
                    put(COL_ID_USUARIO, usuario.id)
                    put(COL_ID_DISPOSITIVO, dispositivo!!.id)
                }

                val resultConfig = db.insert(TABLE_CONFIGURACION, null, cvConfig)
                if (resultConfig == -1L) {
                    Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                } else {
                    val cvVLAN = ContentValues().apply {
                        put(COL_ID_CONFIGURACION, getLastInsertedConfigId())
                    }
                    val resultVLAN = db.update(TABLE_VLAN, cvVLAN, "$COL_ID = ?", arrayOf(result.toString()))
                    if (resultVLAN != -1) {
                        Toast.makeText(context, "Registro en Configuración Insertado Correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun readDataVLAN() : MutableList<Vlan>{
        val list : MutableList<Vlan> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_VLAN"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) do {
            val vlan = Vlan()
            vlan.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
            vlan.vlan_id = result.getString(result.getColumnIndexOrThrow(COL_VLAN_ID)).toInt()
            vlan.nombre = result.getString(result.getColumnIndexOrThrow(COL_NOMBRE_VLAN))
            vlan.dispositivo = result.getString(result.getColumnIndexOrThrow(COL_DISPOSITIVO))
            list.add(vlan)
        }while (result.moveToNext())

        result.close()
        db.close()
        return list
    }

    fun updateDataVLAN(vlan: Vlan): Boolean {
        val db = this.writableDatabase
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        val usuario = getUsuario(email!!)
        var resultVLAN: Int
        var resultConfig: Int

        db.beginTransaction() // Iniciar la transacción

        try {
            // Actualizar la tabla VLAN
            val contentValuesVLAN = ContentValues().apply {
                put(COL_VLAN_ID, vlan.vlan_id)
                put(COL_NOMBRE_VLAN, vlan.nombre)
                put(COL_DISPOSITIVO, vlan.dispositivo)
            }
            resultVLAN = db.update(TABLE_VLAN, contentValuesVLAN, "ID = ?", arrayOf(vlan.id.toString()))

            if (resultVLAN == -1) {
                throw Exception("Fallo al actualizar la VLAN")
            }

            // Preparar los valores para la tabla Configuracion
            val nombreConfiguracion = "VLAN ${vlan.vlan_id} en ${vlan.dispositivo}"
            val descripcionConfiguracion = "VLAN con ID ${vlan.vlan_id} y Nombre: ${vlan.nombre} en el dispositivo ${vlan.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

            val contentValuesConfig = ContentValues().apply {
                put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                put(COL_TIPO_CONFIGURACION, "VLAN")
                put(COL_FECHA_CONFIGURACION, dateTime.format(formatter))
                put(COL_ID_USUARIO, usuario.id)
                put(COL_ID_DISPOSITIVO, getDispositivo(vlan.dispositivo)!!.id)
            }

            // Actualizar la tabla CONFIGURACION (usar un identificador adecuado)
            resultConfig = db.update(TABLE_CONFIGURACION, contentValuesConfig, "$COL_ID = ?", arrayOf(buscarIdConfiguracion(vlan.id,"VLAN").toString()))

            if (resultConfig == -1) {
                throw Exception("Fallo al actualizar Configuración")
            }

            db.setTransactionSuccessful() // Confirmar la transacción

        } catch (e: Exception) {
            e.printStackTrace()
            return false // Retornar false en caso de error

        } finally {
            db.endTransaction() // Finalizar la transacción
        }

        return true // Retornar true si ambas actualizaciones fueron exitosas
    }

    fun deleteDataVLAN(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_VLAN, "ID = ?", arrayOf(id.toString()))
    }

    fun isVLANExist(vlanid: Int): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_VLAN WHERE $COL_VLAN_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(vlanid.toString()))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    //Funciones para STP
    fun insertDataSTP(stp : Stp, email: String) {
        val db = this.writableDatabase
        val usuario = getUsuario(email)
        val dispositivo = getDispositivo(stp.dispositivo)
        val cv = ContentValues()
        cv.put(COL_PRIORIDAD, stp.prioridad)
        cv.put(COL_VLAN_ID, stp.vlan_id)
        cv.put(COL_MODO, stp.modo)
        cv.put(COL_DISPOSITIVO, stp.dispositivo)

        db.beginTransaction()

        try {
            val result = db.insert(TABLE_STP, null, cv)
            if (result == -1L) {
                Toast.makeText(context, "Fallo al Insertar Configuración de STP", Toast.LENGTH_SHORT).show()
                return // Terminar la función si falla la inserción de STP
            } else {
                Toast.makeText(context, "Configuración de STP Insertada Correctamente", Toast.LENGTH_SHORT).show()

                // Preparar los valores para la tabla Configuracion
                val nombreConfiguracion = "Configuración de STP en ${stp.dispositivo}"
                val descripcionConfiguracion = "Protocolo de STP con Prioridad ${stp.prioridad} y Modo: ${stp.modo} para la VLAN ${stp.vlan_id} en el dispositivo ${stp.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

                // Insertar en la tabla Configuracion
                val cvConfig = ContentValues().apply {
                    put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                    put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                    put(COL_TIPO_CONFIGURACION, "STP")
                    put(COL_ID_USUARIO, usuario.id)
                    put(COL_ID_DISPOSITIVO, dispositivo!!.id)
                }

                val resultConfig = db.insert(TABLE_CONFIGURACION, null, cvConfig)
                if (resultConfig == -1L) {
                    Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                } else {
                    val cvSTP = ContentValues().apply {
                        put(COL_ID_CONFIGURACION, getLastInsertedConfigId())
                    }
                    val resultSTP = db.update(TABLE_STP, cvSTP, "$COL_ID = ?", arrayOf(result.toString()))
                    if (resultSTP != -1) {
                        Toast.makeText(context, "Registro en Configuración Insertado Correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun readDataSTP() : MutableList<Stp>{
        val list : MutableList<Stp> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_STP"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) do {
            val stp = Stp()
            stp.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
            stp.prioridad = result.getString(result.getColumnIndexOrThrow(COL_PRIORIDAD)).toInt()
            stp.vlan_id = result.getString(result.getColumnIndexOrThrow(COL_VLAN_ID)).toInt()
            stp.modo = result.getString(result.getColumnIndexOrThrow(COL_MODO))
            stp.dispositivo = result.getString(result.getColumnIndexOrThrow(COL_DISPOSITIVO))
            list.add(stp)
        }while (result.moveToNext())

        result.close()
        db.close()
        return list
    }

    fun updateDataSTP(stp: Stp): Boolean {
        val db = this.writableDatabase
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        val usuario = getUsuario(email!!)
        var resultSTP: Int
        var resultConfig: Int

        db.beginTransaction() // Iniciar la transacción

        try {
            // Actualizar la tabla STP
            val contentValuesSTP = ContentValues().apply {
                put(COL_PRIORIDAD, stp.prioridad)
                put(COL_VLAN_ID, stp.vlan_id)
                put(COL_MODO, stp.modo)
                put(COL_DISPOSITIVO, stp.dispositivo)
            }
            resultSTP = db.update(TABLE_STP, contentValuesSTP, "ID = ?", arrayOf(stp.id.toString()))

            if (resultSTP == -1) {
                throw Exception("Fallo al actualizar la configuración de STP")
            }

            // Preparar los valores para la tabla Configuracion
            val nombreConfiguracion = "Configuración de STP en ${stp.dispositivo}"
            val descripcionConfiguracion = "Protocolo de STP con Prioridad ${stp.prioridad} y Modo: ${stp.modo} para la VLAN ${stp.vlan_id} en el dispositivo ${stp.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

            val contentValuesConfig = ContentValues().apply {
                put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                put(COL_TIPO_CONFIGURACION, "VLAN")
                put(COL_FECHA_CONFIGURACION, dateTime.format(formatter))
                put(COL_ID_USUARIO, usuario.id)
                put(COL_ID_DISPOSITIVO, getDispositivo(stp.dispositivo)!!.id)
            }

            // Actualizar la tabla CONFIGURACION (usar un identificador adecuado)
            resultConfig = db.update(TABLE_CONFIGURACION, contentValuesConfig, "$COL_ID = ?", arrayOf(buscarIdConfiguracion(stp.id,"STP").toString()))

            if (resultConfig == -1) {
                throw Exception("Fallo al actualizar Configuración")
            }

            db.setTransactionSuccessful() // Confirmar la transacción

        } catch (e: Exception) {
            e.printStackTrace()
            return false // Retornar false en caso de error

        } finally {
            db.endTransaction() // Finalizar la transacción
        }

        return true // Retornar true si ambas actualizaciones fueron exitosas
    }

    fun deleteDataSTP(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_STP, "ID = ?", arrayOf(id.toString()))
    }

    fun isSTPExist(modo: String, dispositivo: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_STP WHERE $COL_MODO = ? AND $COL_DISPOSITIVO = ?"
        val cursor = db.rawQuery(query, arrayOf(modo, dispositivo))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    //Funciones para ACL
    fun insertDataACL(acl : Acl, email: String) {
        val db = this.writableDatabase
        val usuario = getUsuario(email)
        val dispositivo = getDispositivo(acl.dispositivo)
        val cv = ContentValues()
        cv.put(COL_TIPO_ACL, acl.tipo)
        cv.put(COL_REGLA, acl.regla)
        cv.put(COL_DISPOSITIVO, acl.dispositivo)

        db.beginTransaction()

        try {
            val result = db.insert(TABLE_ACL, null, cv)
            if (result == -1L) {
                Toast.makeText(context, "Fallo al Insertar la ACL", Toast.LENGTH_SHORT).show()
                return // Terminar la función si falla la inserción de la ACL
            } else {
                Toast.makeText(context, "ACL Insertada Correctamente", Toast.LENGTH_SHORT).show()

                // Preparar los valores para la tabla Configuracion
                val nombreConfiguracion = "Lista de Control de Acceso en ${acl.dispositivo}"
                val descripcionConfiguracion = "Lista de Control de Acceso ${acl.tipo} con la Regla: ${acl.regla} en el dispositivo ${acl.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

                // Insertar en la tabla Configuracion
                val cvConfig = ContentValues().apply {
                    put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                    put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                    put(COL_TIPO_CONFIGURACION, "ACL")
                    put(COL_ID_USUARIO, usuario.id)
                    put(COL_ID_DISPOSITIVO, dispositivo!!.id)
                }

                val resultConfig = db.insert(TABLE_CONFIGURACION, null, cvConfig)
                if (resultConfig == -1L) {
                    Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                } else {
                    val cvACL = ContentValues().apply {
                        put(COL_ID_CONFIGURACION, getLastInsertedConfigId())
                    }
                    val resultACL = db.update(TABLE_ACL, cvACL, "$COL_ID = ?", arrayOf(result.toString()))
                    if (resultACL != -1) {
                        Toast.makeText(context, "Registro en Configuración Insertado Correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Fallo al Insertar Registro en Configuración", Toast.LENGTH_SHORT).show()
                    }
                }
            }

            db.setTransactionSuccessful()
        } finally {
            db.endTransaction()
        }
    }

    fun readDataACL() : MutableList<Acl>{
        val list : MutableList<Acl> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_ACL"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) do {
            val acl = Acl()
            acl.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
            acl.tipo = result.getString(result.getColumnIndexOrThrow(COL_TIPO_ACL))
            acl.regla = result.getString(result.getColumnIndexOrThrow(COL_REGLA))
            acl.dispositivo = result.getString(result.getColumnIndexOrThrow(COL_DISPOSITIVO))
            list.add(acl)
        }while (result.moveToNext())

        result.close()
        db.close()
        return list
    }

    fun updateDataACL(acl: Acl): Boolean {
        val db = this.writableDatabase
        val auth = FirebaseAuth.getInstance()
        val email = auth.currentUser?.email
        val usuario = getUsuario(email!!)
        var resultACL: Int
        var resultConfig: Int

        db.beginTransaction() // Iniciar la transacción

        try {
            // Actualizar la tabla ACL
            val contentValuesACL = ContentValues().apply {
                put(COL_TIPO_ACL, acl.tipo)
                put(COL_REGLA, acl.regla)
                put(COL_DISPOSITIVO, acl.dispositivo)
            }
            resultACL = db.update(TABLE_ACL, contentValuesACL, "ID = ?", arrayOf(acl.id.toString()))

            if (resultACL == -1) {
                throw Exception("Fallo al actualizar la ACL")
            }

            // Preparar los valores para la tabla Configuracion
            val nombreConfiguracion = "Lista de Control de Acceso en ${acl.dispositivo}"
            val descripcionConfiguracion = "Lista de Control de Acceso ${acl.tipo} con la Regla: ${acl.regla} en el dispositivo ${acl.dispositivo} realizada por ${usuario!!.nombreUs} ${usuario.apellidoUs}"

            val dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(System.currentTimeMillis()), ZoneId.systemDefault())
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")

            val contentValuesConfig = ContentValues().apply {
                put(COL_NOMBRE_CONFIGURACION, nombreConfiguracion)
                put(COL_DESCRIPCION_CONFIGURACION, descripcionConfiguracion)
                put(COL_TIPO_CONFIGURACION, "VLAN")
                put(COL_FECHA_CONFIGURACION, dateTime.format(formatter))
                put(COL_ID_USUARIO, usuario.id)
                put(COL_ID_DISPOSITIVO, getDispositivo(acl.dispositivo)!!.id)
            }

            // Actualizar la tabla CONFIGURACION (usar un identificador adecuado)
            resultConfig = db.update(TABLE_CONFIGURACION, contentValuesConfig, "$COL_ID = ?", arrayOf(buscarIdConfiguracion(acl.id,"ACL").toString()))

            if (resultConfig == -1) {
                throw Exception("Fallo al actualizar Configuración")
            }

            db.setTransactionSuccessful() // Confirmar la transacción

        } catch (e: Exception) {
            e.printStackTrace()
            return false // Retornar false en caso de error

        } finally {
            db.endTransaction() // Finalizar la transacción
        }

        return true // Retornar true si ambas actualizaciones fueron exitosas
    }

    fun deleteDataACL(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_ACL, "ID = ?", arrayOf(id.toString()))
    }

    fun isACLExist(tipo: String, regla: String, dispositivo: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_ACL WHERE $COL_TIPO_ACL = ? AND $COL_REGLA = ? AND $COL_DISPOSITIVO = ?"
        val cursor = db.rawQuery(query, arrayOf(tipo, regla, dispositivo))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    //Funciones para Usuario
    fun insertDataUsuario(usuario: Usuario) {
        if (isEmailExist(usuario.emailUs)) {
            Toast.makeText(context, "El correo ya está registrado", Toast.LENGTH_SHORT).show()
            return
        }

        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_NOMBRE, usuario.nombreUs)
        cv.put(COL_APELLIDO, usuario.apellidoUs)
        cv.put(COL_EMAIL, usuario.emailUs)
        cv.put(COL_CONTRASENA, usuario.contrasenaUs)
        cv.put(COL_ROL, usuario.rolUs)
        // No es necesario insertar FechaCreacion, ya que TIMESTAMP se establece automáticamente
        val result = db.insert(TABLE_USUARIO, null, cv)
        if (result == -1L)
            Toast.makeText(context, "Fallo al Insertar Usuario", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "Usuario Insertado Correctamente", Toast.LENGTH_SHORT).show()
    }


    fun readDataUsuario(): MutableList<Usuario> {
        val list: MutableList<Usuario> = ArrayList()
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIO"
        val result = db.rawQuery(query, null)

        if (result.moveToFirst()) {
            do {
                val usuario = Usuario()
                usuario.id = result.getInt(result.getColumnIndexOrThrow(COL_ID))
                usuario.nombreUs = result.getString(result.getColumnIndexOrThrow(COL_NOMBRE))
                usuario.apellidoUs = result.getString(result.getColumnIndexOrThrow(COL_APELLIDO))
                usuario.emailUs = result.getString(result.getColumnIndexOrThrow(COL_EMAIL))
                usuario.contrasenaUs = result.getString(result.getColumnIndexOrThrow(COL_CONTRASENA))
                usuario.rolUs = result.getString(result.getColumnIndexOrThrow(COL_ROL))
                usuario.fechaUs = result.getString(result.getColumnIndexOrThrow(COL_FECHA))
                list.add(usuario)
            } while (result.moveToNext())
        }

        result.close()
        db.close()
        return list
    }


    fun updateDataUsuario(usuario: Usuario): Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put(COL_NOMBRE, usuario.nombreUs)
        contentValues.put(COL_APELLIDO, usuario.apellidoUs)
        contentValues.put(COL_EMAIL, usuario.emailUs)
        contentValues.put(COL_CONTRASENA, usuario.contrasenaUs)
        contentValues.put(COL_ROL, usuario.rolUs)

        val result = db.update(TABLE_USUARIO, contentValues, "ID = ?", arrayOf(usuario.id.toString()))
        return result != -1
    }


    fun deleteDataUsuario(id: Int): Int {
        val db = this.writableDatabase
        return db.delete(TABLE_USUARIO, "ID = ?", arrayOf(id.toString()))
    }

    fun getUsuario(email: String): Usuario? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIO WHERE $COL_EMAIL = ?"
        val result = db.rawQuery(query, arrayOf(email))

        var usuario: Usuario? = null

        if (result.moveToFirst()) {
            usuario = Usuario().apply {
                id = result.getInt(result.getColumnIndexOrThrow(COL_ID))
                nombreUs = result.getString(result.getColumnIndexOrThrow(COL_NOMBRE))
                apellidoUs = result.getString(result.getColumnIndexOrThrow(COL_APELLIDO))
                emailUs = result.getString(result.getColumnIndexOrThrow(COL_EMAIL))
                contrasenaUs = result.getString(result.getColumnIndexOrThrow(COL_CONTRASENA))
                rolUs = result.getString(result.getColumnIndexOrThrow(COL_ROL))
                fechaUs = result.getString(result.getColumnIndexOrThrow(COL_FECHA))
            }
        }

        result.close()
        return usuario
    }

    fun getUsuarioById(idUsuario: Int): Usuario? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIO WHERE $COL_ID = ?"
        val result = db.rawQuery(query, arrayOf(idUsuario.toString()))

        var usuario: Usuario? = null

        if (result.moveToFirst()) {
            usuario = Usuario().apply {
                id = result.getInt(result.getColumnIndexOrThrow(COL_ID))
                nombreUs = result.getString(result.getColumnIndexOrThrow(COL_NOMBRE))
                apellidoUs = result.getString(result.getColumnIndexOrThrow(COL_APELLIDO))
                emailUs = result.getString(result.getColumnIndexOrThrow(COL_EMAIL))
                contrasenaUs = result.getString(result.getColumnIndexOrThrow(COL_CONTRASENA))
                rolUs = result.getString(result.getColumnIndexOrThrow(COL_ROL))
                fechaUs = result.getString(result.getColumnIndexOrThrow(COL_FECHA))
            }
        }

        result.close()
        return usuario
    }

    fun isEmailExist(email: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_USUARIO WHERE $COL_EMAIL = ?"
        val cursor = db.rawQuery(query, arrayOf(email))
        val exists = cursor.moveToFirst()
        cursor.close()
        return exists
    }

    fun getDispositivo(nombre: String): Dispositivo? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_DISPOSITIVO WHERE $COL_NOMBRE_DISPOSITIVO = ?"
        val result = db.rawQuery(query, arrayOf(nombre))

        var dispositivo: Dispositivo? = null

        if (result.moveToFirst()) {
            dispositivo = Dispositivo().apply {
                id = result.getInt(result.getColumnIndexOrThrow(COL_ID))
                nombreDisp = result.getString(result.getColumnIndexOrThrow(COL_NOMBRE_DISPOSITIVO))
                tipoDisp = result.getString(result.getColumnIndexOrThrow(COL_TIPO_DISPOSITIVO))
                ipDisp = result.getString(result.getColumnIndexOrThrow(COL_IP_DISPOSITIVO))
            }
        }

        result.close()
        return dispositivo
    }

    fun getDispositivoById(idDispositivo: Int): Dispositivo? {
        val db = this.readableDatabase
        val query = "SELECT * FROM $TABLE_DISPOSITIVO WHERE $COL_ID = ?"
        val result = db.rawQuery(query, arrayOf(idDispositivo.toString()))

        var dispositivo: Dispositivo? = null

        if (result.moveToFirst()) {
            dispositivo = Dispositivo().apply {
                id = result.getInt(result.getColumnIndexOrThrow(COL_ID))
                nombreDisp = result.getString(result.getColumnIndexOrThrow(COL_NOMBRE_DISPOSITIVO))
                tipoDisp = result.getString(result.getColumnIndexOrThrow(COL_TIPO_DISPOSITIVO))
                ipDisp = result.getString(result.getColumnIndexOrThrow(COL_IP_DISPOSITIVO))
            }
        }

        result.close()
        return dispositivo
    }

    fun getConfiguracionById(idConfiguracion: Int): Configuracion? {
        val db = this.readableDatabase
        var configuracion: Configuracion? = null

        val cursor = db.rawQuery("SELECT * FROM $TABLE_CONFIGURACION WHERE $COL_ID = ?", arrayOf(idConfiguracion.toString()))
        if (cursor.moveToFirst()) {
            configuracion = Configuracion().apply {
                id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
                nombreConfig = cursor.getString(cursor.getColumnIndexOrThrow(COL_NOMBRE_CONFIGURACION))
                descripcionConfig = cursor.getString(cursor.getColumnIndexOrThrow(COL_DESCRIPCION_CONFIGURACION))
                tipoConfig = cursor.getString(cursor.getColumnIndexOrThrow(COL_TIPO_CONFIGURACION))
                fechaConfig = cursor.getString(cursor.getColumnIndexOrThrow(COL_FECHA_CONFIGURACION))
                idUsuario = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_USUARIO))
                idDispositivo = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID_DISPOSITIVO))
            }
        }
        cursor.close()
        return configuracion
    }


    fun getLastInsertedConfigId(): Int {
        val db = this.readableDatabase
        var lastId = -1
        val cursor = db.rawQuery("SELECT $COL_ID FROM $TABLE_CONFIGURACION ORDER BY $COL_ID DESC LIMIT 1", null)
        if (cursor.moveToFirst()) {
            lastId = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID))
        }
        cursor.close()
        return lastId
    }

    fun readDataConfig() : MutableList<Configuracion>{
        val list : MutableList<Configuracion> = ArrayList()

        val db = this.readableDatabase
        val query = "Select * from $TABLE_CONFIGURACION"
        val result = db.rawQuery(query,null)

        if(result.moveToFirst()) do {
            val configuracion = Configuracion()
            configuracion.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
            configuracion.nombreConfig = result.getString(result.getColumnIndexOrThrow(COL_NOMBRE_CONFIGURACION))
            configuracion.descripcionConfig = result.getString(result.getColumnIndexOrThrow(COL_DESCRIPCION_CONFIGURACION))
            configuracion.tipoConfig = result.getString(result.getColumnIndexOrThrow(COL_TIPO_CONFIGURACION))
            configuracion.fechaConfig = result.getString(result.getColumnIndexOrThrow(COL_FECHA_CONFIGURACION))
            configuracion.idUsuario = result.getString(result.getColumnIndexOrThrow(COL_ID_USUARIO)).toInt()
            configuracion.idDispositivo = result.getString(result.getColumnIndexOrThrow(COL_ID_DISPOSITIVO)).toInt()
            list.add(configuracion)
        }while (result.moveToNext())

        result.close()
        db.close()
        return list
    }

    fun insertDataReporte(reporte: Reporte) {
        val db = this.writableDatabase
        val cv = ContentValues()
        cv.put(COL_TIPO_REPORTE, reporte.tipo)
        cv.put(COL_ATRIBUTO1_TITULO, reporte.atributo1_titulo)
        cv.put(COL_ATRIBUTO1_VALOR, reporte.atributo1_valor)
        cv.put(COL_ATRIBUTO2_TITULO, reporte.atributo2_titulo)
        cv.put(COL_ATRIBUTO2_VALOR, reporte.atributo2_valor)
        cv.put(COL_ATRIBUTO3_TITULO, reporte.atributo3_titulo)
        cv.put(COL_ATRIBUTO3_VALOR, reporte.atributo3_valor)
        cv.put(COL_ATRIBUTO4_TITULO, reporte.atributo4_titulo)
        cv.put(COL_ATRIBUTO4_VALOR, reporte.atributo4_valor)
        val result = db.insert(TABLE_REPORTE, null, cv)
        if (result == -1L)
            Toast.makeText(context, "Fallo al Crear Reporte", Toast.LENGTH_SHORT).show()
        else
            Toast.makeText(context, "Reporte Creado Correctamente", Toast.LENGTH_SHORT).show()

    }

    fun readDataReporte() : MutableList<Reporte>{
        val list : MutableList<Reporte> = ArrayList()
        val db = this.readableDatabase
        val query = "Select * from $TABLE_REPORTE"
        val result = db.rawQuery(query,null)
        if(result.moveToFirst()) do {
            val reporte = Reporte()
            reporte.id = result.getString(result.getColumnIndexOrThrow(COL_ID)).toInt()
            reporte.tipo = result.getString(result.getColumnIndexOrThrow(COL_TIPO_REPORTE))
            reporte.atributo1_titulo = result.getString(result.getColumnIndexOrThrow(COL_ATRIBUTO1_TITULO))
            reporte.atributo1_valor = result.getString(result.getColumnIndexOrThrow(COL_ATRIBUTO1_VALOR))
            reporte.atributo2_titulo = result.getString(result.getColumnIndexOrThrow(COL_ATRIBUTO2_TITULO))
            reporte.atributo2_valor = result.getString(result.getColumnIndexOrThrow(COL_ATRIBUTO2_VALOR))
            reporte.atributo3_titulo = result.getString(result.getColumnIndexOrThrow(COL_ATRIBUTO3_TITULO))
            reporte.atributo3_valor = result.getString(result.getColumnIndexOrThrow(COL_ATRIBUTO3_VALOR))
            reporte.atributo4_titulo = result.getString(result.getColumnIndexOrThrow(COL_ATRIBUTO4_TITULO))
            reporte.atributo4_valor = result.getString(result.getColumnIndexOrThrow(COL_ATRIBUTO4_VALOR))
            list.add(reporte)
        }while (result.moveToNext())

        result.close()
        db.close()
        return list
    }

    fun isReporteExist(tipo: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_REPORTE WHERE $COL_TIPO_REPORTE = ?"
        val cursor = db.rawQuery(query, arrayOf(tipo))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    fun isDispositivoExist(nombre: String): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_DISPOSITIVO WHERE $COL_NOMBRE = ?"
        val cursor = db.rawQuery(query, arrayOf(nombre))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }

    fun isConfiguracionExist(id: Int): Boolean {
        val db = this.readableDatabase
        val query = "SELECT COUNT(*) FROM $TABLE_CONFIGURACION WHERE $COL_ID = ?"
        val cursor = db.rawQuery(query, arrayOf(id.toString()))
        cursor.moveToFirst()
        val exists = cursor.getInt(0) > 0
        cursor.close()
        return exists
    }



    fun buscarIdConfiguracion(id: Int, tipo: String): Int {
        val db = this.readableDatabase

        val query = when (tipo) {
            "NAT" -> "SELECT $COL_ID_CONFIGURACION FROM $TABLE_NAT WHERE $COL_ID = ?"
            "STP" -> "SELECT $COL_ID_CONFIGURACION FROM $TABLE_STP WHERE $COL_ID = ?"
            "ACL" -> "SELECT $COL_ID_CONFIGURACION FROM $TABLE_ACL WHERE $COL_ID = ?"
            "EIGRP" -> "SELECT $COL_ID_CONFIGURACION FROM $TABLE_EIGRP WHERE $COL_ID = ?"
            "VLAN" -> "SELECT $COL_ID_CONFIGURACION FROM $TABLE_VLAN WHERE $COL_ID = ?"
            "OSPF" -> "SELECT $COL_ID_CONFIGURACION FROM $TABLE_OSPF WHERE $COL_ID = ?"
            else -> "SELECT $COL_ID_CONFIGURACION FROM $TABLE_RUTASEST WHERE $COL_ID = ?"
        }


        val result = db.rawQuery(query, arrayOf(id.toString()))

        var idConfiguracion = -1

        if (result.moveToFirst()) {
            idConfiguracion = result.getInt(result.getColumnIndexOrThrow(COL_ID_CONFIGURACION))
        }

        result.close()

        return idConfiguracion
    }

    fun backupDatabaseToSQLFile(context: Context): String {
        val database = this.writableDatabase
        val backupFileName = "dump.sql"
        val backupPath = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), backupFileName)
        backupPath.bufferedWriter().use { writer ->
            val cursor = database.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null)
            while (cursor.moveToNext()) {
                val tableName = cursor.getString(0)
                if (tableName != "android_metadata" && tableName != "sqlite_sequence") {
                    writer.write("-- Backup table $tableName\n")
                    val dataCursor = database.rawQuery("SELECT * FROM $tableName", null)
                    while (dataCursor.moveToNext()) {
                        val values = Array(dataCursor.columnCount) { i ->
                            val value = dataCursor.getString(i)
                            if (value != null) "'$value'" else "NULL"
                        }
                        // Agregar INSERT IGNORE o lógica para evitar duplicados en restauración
                        writer.write("INSERT OR IGNORE INTO $tableName VALUES(${values.joinToString(",")});\n")
                    }
                    dataCursor.close()
                }
            }
            cursor.close()
        }
        return backupPath.absolutePath
    }


    fun restoreDatabaseFromSQLFile(context: Context, fileUri: Uri) {
        val database = this.writableDatabase
        val contentResolver = context.contentResolver
        contentResolver.openInputStream(fileUri)?.bufferedReader()?.use { reader ->
            database.beginTransaction()
            try {
                reader.forEachLine { line ->
                    if (line.isNotBlank()) {
                        // Analizar INSERT y realizar validaciones
                        if (line.startsWith("INSERT INTO")) {
                            try {
                                val tableName = line.substringAfter("INSERT INTO ").substringBefore(" VALUES")
                                val values = line.substringAfter("VALUES(").substringBefore(");").split(",")
                                    .map { it.trim().removeSurrounding("'") }

                                Log.d("Restore", "Insertando en la tabla: $tableName, valores: $values")
                                // Continuar con el análisis y la inserción

                                when (tableName) {
                                    TABLE_NAT -> {
                                        val (tipo, ipPriv, ipPub, dispositivo) = values
                                        if (!isNATExist(tipo, ipPriv, ipPub, dispositivo)) {
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_RUTASEST -> {
                                        val (ipDestino, mascara, ipSalto, dispositivo) = values
                                        if (!isRutaExist(ipDestino, mascara, ipSalto, dispositivo)) {
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_EIGRP -> {
                                        val (asNumber, red, wildcard, dispositivo) = values
                                        if (!isEigrpExist(asNumber.toInt(), red, wildcard, dispositivo)) {
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_OSPF -> {
                                        val (area, red, wildcard, dispositivo) = values
                                        if (!isOspfExist(area.toInt(), red, wildcard, dispositivo)) {
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_VLAN -> {
                                        val vlanId = values[0].toInt()
                                        if (!isVLANExist(vlanId)) {
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_STP -> {
                                        val (modo, dispositivo) = values
                                        if (!isSTPExist(modo, dispositivo)) {
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_ACL -> {
                                        val (tipo, regla, dispositivo) = values
                                        if (!isACLExist(tipo, regla, dispositivo)) {
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_USUARIO -> {
                                        val (nombre, apellido, email, contrasena, rol) = values
                                        if (!isEmailExist(email)) {
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_REPORTE -> {
                                        if (!isReporteExist(values[0])) { // El valor en `values[0]` corresponde a `tipo`
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_DISPOSITIVO -> {
                                        val nombre = values[0]
                                        if (!isDispositivoExist(nombre)) {
                                            database.execSQL(line)
                                        }
                                    }
                                    TABLE_CONFIGURACION -> {
                                        val clave = values[0]
                                        if (!isConfiguracionExist(clave.toInt())) {
                                            database.execSQL(line)
                                        }
                                    }
                                }

                            } catch (e: Exception) {
                                /*Log.e("RestoreError", "Error al analizar línea: $line", e)*/
                            }
                        } else {
                            try {
                                database.execSQL(line)
                            } catch (e: Exception) {
                                /*Log.e("RestoreError", "Error al ejecutar línea no INSERT: $line", e)*/
                            }
                        }
                    }
                }
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }
        }
    }


}