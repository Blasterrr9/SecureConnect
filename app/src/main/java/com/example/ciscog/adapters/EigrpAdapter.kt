package com.example.ciscog.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ciscog.ChatActivity
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Eigrp

class EigrpAdapter(private val context: Activity, private var eigrp: ArrayList<Eigrp>): RecyclerView.Adapter<EigrpAdapter.eigrpViewHolder>()  {

    private var db = DataBaseLITE(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): eigrpViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_eigrp, parent, false)
        return eigrpViewHolder(view)
    }

    override fun getItemCount(): Int {

        return eigrp.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: eigrpViewHolder, position: Int) {

        val listaEigrp = eigrp[position]

        //Asignamos los datos de la lista al Cardview
        holder.textViewId.text = listaEigrp.id.toString()
        holder.textViewAsNumber.text = listaEigrp.asnumber.toString()
        holder.textViewRed.text = listaEigrp.red
        holder.textViewWildcard.text = listaEigrp.wildcard
        holder.textViewDisp.text = listaEigrp.dispositivo

        // Redirigir a ChatActivity al hacer clic en el botón de chat
        holder.chatMenu.setOnClickListener {
            val mensajeRuta = """
                Para una topología de red en la cual se tiene aplicada una configuración de EIGRP a un dispositivo Router, explica la configuración siguiente (ID: ${listaEigrp.id}):
                
                AsNumber: ${listaEigrp.asnumber}
                Red: ${listaEigrp.red}
                Wildcard: ${listaEigrp.wildcard}
                Dispositivo: ${listaEigrp.dispositivo}
            """.trimIndent()

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("config_info", mensajeRuta)
            context.startActivity(intent)

        }

        holder.imageViewMenu.setOnClickListener { popupMenu(it, listaEigrp, position) } //Menu emergente

        // Verificar el rol del usuario
        val usuarioRol = getUserRole(context) // Esta función debería obtener el rol del usuario desde la base de datos o de una variable global.

        if (usuarioRol == "Usuario") {
            holder.imageViewMenu.visibility = View.GONE // Ocultar el menú desplegable para el rol 'Usuario'
        } else {
            holder.imageViewMenu.visibility = View.VISIBLE // Mostrar el menú para 'Administrador' o 'Empleado'
        }
    }

    //Menú emergente que se despliega en cada CardView del RecyclerView
    @SuppressLint("DiscouragedPrivateApi", "NotifyDataSetChanged")
    private fun popupMenu(view: View, listaEigrp: Eigrp, position: Int){

        val dispositivoEigrp = arrayOf("LS1-R01", "LS1-R02", "LS2-R01", "LS2-R02", "LS2-R03")
        val dispositivoAdapter = ArrayAdapter(context, R.layout.spinner_item, dispositivoEigrp)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        //Inicia el menú emergente
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.show_menu)

        popupMenu.setOnMenuItemClickListener {

            when(it.itemId){

                R.id.editText ->
                {

                    //Infla AlertDialog
                    val v = LayoutInflater.from(context).inflate(R.layout.edit_eigrp_dialog, null)

                    //Inicializa items de AlertDialog
                    val textViewIdDialog: TextView = v.findViewById(R.id.textview_id_dialog)
                    val editTextAsNumberDialog: EditText = v.findViewById(R.id.edittext_asnumber_dialog)
                    val editTextRedDialog: EditText = v.findViewById(R.id.edittext_red_dialog)
                    val editTextWildcardDialog: EditText = v.findViewById(R.id.edittext_wildcard_dialog)
                    val spinnerDispoDialog: Spinner = v.findViewById(R.id.spinner_dispositivo_dialog)

                    //Inicializa el adaptador de los spinner
                    spinnerDispoDialog.adapter = dispositivoAdapter

                    //Asigna los datos de la lista al AlertDialog-------------
                    textViewIdDialog.text = listaEigrp.id.toString()
                    editTextAsNumberDialog.text = Editable.Factory.getInstance().newEditable(listaEigrp.asnumber.toString())
                    editTextRedDialog.text = Editable.Factory.getInstance().newEditable(listaEigrp.red)
                    editTextWildcardDialog.text = Editable.Factory.getInstance().newEditable(listaEigrp.wildcard)

                    if (dispositivoEigrp.contains(listaEigrp.dispositivo)){

                        val posicionDispo = dispositivoEigrp.indexOf(listaEigrp.dispositivo)
                        spinnerDispoDialog.setSelection(posicionDispo)
                    }

                    //Construye el AlertDialog
                    AlertDialog.Builder(context)
                        .setView(v)
                        .setPositiveButton("Ok"){

                            //Sintaxis que se utiliza para desestructurar parámetros en lambdas.
                            //Indica que solo estás utilizando el primer parámetro (dialog)
                            // y que estás ignorando el segundo parámetro (_).
                            dialog,_->

                            //Asignamos los datos del AlertDialog a la lista
                            listaEigrp.id = textViewIdDialog.text.toString().toInt()
                            listaEigrp.asnumber = editTextAsNumberDialog.text.toString().toInt()
                            listaEigrp.red = editTextRedDialog.text.toString()
                            listaEigrp.wildcard = editTextWildcardDialog.text.toString()
                            listaEigrp.dispositivo = spinnerDispoDialog.selectedItem.toString()

                            if  (db.isEigrpExist(listaEigrp.asnumber, listaEigrp.red, listaEigrp.wildcard, listaEigrp.dispositivo)) {
                                Toast.makeText(context, "La Configuración de EIGRP ya existe", Toast.LENGTH_SHORT).show()
                            } else {
                                // Actualizar el registro en la base de datos
                                if (db.updateDataEigrp(listaEigrp)) {
                                    notifyItemChanged(position) // Notifica al adaptador que el ítem ha cambiado
                                    Toast.makeText(context, "Los datos de la configuración de EIGRP se actualizaron correctamente", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al actualizar los datos de la configuración de EIGRP", Toast.LENGTH_SHORT).show()
                                }
                            }
                            dialog.dismiss() // Cierra el cuadro de diálogo
                        }
                        .setNegativeButton("Cancelar") { dialog, _ ->
                            dialog.dismiss()
                        }
                        .create()
                        .show()

                    true
                }
                R.id.delete ->
                {
                    AlertDialog.Builder(context)
                        .setTitle("Eliminar")
                        .setIcon(R.drawable.ic_warning)
                        .setMessage("¿Estas seguro de eliminar esta información?")
                        .setPositiveButton("Si"){ dialog,_->

                            deleteItem(position)
                            Toast.makeText(context, "Los datos se eliminaron correctamente", Toast.LENGTH_SHORT).show()
                            dialog.dismiss()

                        }.setNegativeButton("No"){ dialog,_->

                            dialog.dismiss()
                        }
                        .create()
                        .show()

                    true
                }
                else -> true
            }
        }

        popupMenu.show()

        //CONFIGURACIÓN PARA MOSTRAR ICONOS (Editar, Eliminar) EN EL MENÚ EMERGENTE (All vesions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            try {

                // 1. Obtiene el campo "mPopup" de la clase PopupMenu mediante reflexión.
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")

                // 2. Hace que el campo "mPopup" sea accesible, incluso si es privado.
                popup.isAccessible = true

                // 3. Obtiene el valor del campo "mPopup" para el objeto PopupMenu proporcionado.
                val menu = popup.get(popupMenu)

                // 4. Utilizando reflexión, obtiene el método "setForceShowIcon" de la clase del menú.
                //    Este método, si está presente, permite mostrar iconos en el menú incluso si no se hace de forma predeterminada.
                //    Invoca el método "setForceShowIcon" y establece su argumento en true para forzar la visualización de iconos.
                menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)

            } catch (e: Exception) {

                e.printStackTrace()
            }
        }
    }

    private fun deleteItem(position: Int) {
        val registro = eigrp[position]

        // Eliminar el ítem de la base de datos
        db.deleteDataEigrp(registro.id)

        // Eliminar el ítem de la lista
        eigrp.removeAt(position)

        // Notificar al adaptador de los cambios
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, eigrp.size)
    }

    //Lista de elemntos encontrados con el SearchView
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: ArrayList<Eigrp>){

        this.eigrp = mList
        notifyDataSetChanged()
    }

    fun getUserRole(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userRole", null)  // Retorna el rol o null si no está guardado
    }

    class eigrpViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textViewId: TextView = view.findViewById(R.id.textview_id)
        val textViewAsNumber: TextView = view.findViewById(R.id.textview_asNumber)
        val textViewRed: TextView = view.findViewById(R.id.textview_red)
        val textViewWildcard: TextView = view.findViewById(R.id.textview_wildcard)
        val textViewDisp: TextView = view.findViewById(R.id.textview_dispositivo)
        val imageViewMenu: ImageView = view.findViewById(R.id.imageview_menu)
        val chatMenu: ImageButton = view.findViewById(R.id.chat_btn)
    }
}