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
import androidx.core.view.marginTop
import androidx.recyclerview.widget.RecyclerView
import com.example.ciscog.ChatActivity
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Stp

class StpAdapter(private val context: Activity, private var stp: ArrayList<Stp>): RecyclerView.Adapter<StpAdapter.stpViewHolder>()  {

    private var db = DataBaseLITE(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): stpViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_stp, parent, false)
        return stpViewHolder(view)
    }

    override fun getItemCount(): Int {

        return stp.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: stpViewHolder, position: Int) {

        val listaStp = stp[position]

        //Asignamos los datos de la lista al Cardview
        holder.textViewId.text = listaStp.id.toString()
        holder.textViewPrioridad.text = listaStp.prioridad.toString()
        holder.textViewVlan.text = listaStp.vlan_id.toString()
        holder.textViewModo.text = listaStp.modo
        holder.textViewDisp.text = listaStp.dispositivo

        // Redirigir a ChatActivity al hacer clic en el botón de chat
        holder.chatMenu.setOnClickListener {
            val mensajeRuta = """
                Para una topología de red en la cual se tiene aplicada una configuración de STP a un dispositivo Switch, explica la configuración siguiente (ID: ${listaStp.id}):
                
                Prioridad: ${listaStp.prioridad}
                VLAN: ${listaStp.vlan_id}
                Modo: ${listaStp.modo}
                Dispositivo: ${listaStp.dispositivo}
            """.trimIndent()

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("config_info", mensajeRuta)
            context.startActivity(intent)

        }

        holder.imageViewMenu.setOnClickListener { popupMenu(it, listaStp, position) } //Menu emergente

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
    private fun popupMenu(view: View, listaStp: Stp, position: Int){

        val modoStp = arrayOf("PVST", "MSTP", "Rapid-PVST")
        val modoAdapter = ArrayAdapter(context, R.layout.spinner_item, modoStp)
        modoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        val dispositivoStp = arrayOf("LS1-SW1", "LS1-SW2", "LS2-SW1", "LS2-SW2", "LS2-SW3")
        val dispositivoAdapter = ArrayAdapter(context, R.layout.spinner_item, dispositivoStp)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        //Inicia el menú emergente
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.show_menu)

        popupMenu.setOnMenuItemClickListener {

            when(it.itemId){

                R.id.editText ->
                {

                    //Infla AlertDialog
                    val v = LayoutInflater.from(context).inflate(R.layout.edit_stp_dialog, null)

                    //Inicializa items de AlertDialog
                    val textViewIdDialog: TextView = v.findViewById(R.id.textview_id_dialog)
                    val editTextPrioridadDialog: EditText = v.findViewById(R.id.edittext_prioridad_dialog)
                    val editTextVlanDialog: EditText = v.findViewById(R.id.edittext_VLAN_ID_dialog)
                    val spinnerModoDialog: Spinner = v.findViewById(R.id.spinner_modo_dialog)
                    val spinnerDispoDialog: Spinner = v.findViewById(R.id.spinner_dispositivo_dialog)

                    //Inicializa el adaptador de los spinner
                    spinnerModoDialog.adapter = modoAdapter
                    spinnerDispoDialog.adapter = dispositivoAdapter

                    //Asigna los datos de la lista al AlertDialog-------------
                    textViewIdDialog.text = listaStp.id.toString()
                    editTextPrioridadDialog.text = Editable.Factory.getInstance().newEditable(listaStp.prioridad.toString())
                    editTextVlanDialog.text = Editable.Factory.getInstance().newEditable(listaStp.vlan_id.toString())

                    if (modoStp.contains(listaStp.modo)){
                        val posicionModo = modoStp.indexOf(listaStp.modo)
                        spinnerModoDialog.setSelection(posicionModo)
                    }

                    if (dispositivoStp.contains(listaStp.dispositivo)){
                        val posicionDispo = dispositivoStp.indexOf(listaStp.dispositivo)
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
                            listaStp.id = textViewIdDialog.text.toString().toInt()
                            listaStp.prioridad = editTextPrioridadDialog.text.toString().toInt()
                            listaStp.vlan_id = editTextVlanDialog.text.toString().toInt()
                            listaStp.modo = spinnerModoDialog.selectedItem.toString()
                            listaStp.dispositivo = spinnerDispoDialog.selectedItem.toString()

                            // Verifica si la configuración de STP ya existe en la base de datos local
                            if (listaStp.modo == "MSTP") {
                                // Para MSTP, se permite múltiples instancias, así que no es necesario verificar duplicados
                            } else if (db.isSTPExist(listaStp.modo, listaStp.dispositivo)) {
                                val builder = androidx.appcompat.app.AlertDialog.Builder(context)
                                builder.setTitle("Advertencia") // Título del diálogo
                                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                                builder.setMessage("La configuración de ${listaStp.modo} ya se encuentra registrada para el dispositivo ${listaStp.dispositivo}") // Mensaje del diálogo
                                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                                val dialogo: androidx.appcompat.app.AlertDialog = builder.create() // Crea el diálogo
                                dialogo.show() // Muestra el diálogo
                                return@setPositiveButton
                            }

                            // Actualizar el registro en la base de datos
                            if (db.updateDataSTP(listaStp)) {
                                notifyItemChanged(position) // Notifica al adaptador que el ítem ha cambiado
                                Toast.makeText(context, "Los datos de la configuración de STP se actualizaron correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al actualizar los datos de la configuración de STP", Toast.LENGTH_SHORT).show()
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
        val registro = stp[position]

        // Eliminar el ítem de la base de datos
        db.deleteDataSTP(registro.id)

        // Eliminar el ítem de la lista
        stp.removeAt(position)

        // Notificar al adaptador de los cambios
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, stp.size)
    }

    //Lista de elemntos encontrados con el SearchView
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: ArrayList<Stp>){

        this.stp = mList
        notifyDataSetChanged()
    }

    fun getUserRole(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userRole", null)  // Retorna el rol o null si no está guardado
    }

    class stpViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textViewId: TextView = view.findViewById(R.id.textview_id)
        val textViewPrioridad: TextView = view.findViewById(R.id.textview_prioridad)
        val textViewVlan: TextView = view.findViewById(R.id.textview_vlan)
        val textViewModo: TextView = view.findViewById(R.id.textview_modo)
        val textViewDisp: TextView = view.findViewById(R.id.textview_dispositivo)
        val imageViewMenu: ImageView = view.findViewById(R.id.imageview_menu)
        val chatMenu: ImageButton = view.findViewById(R.id.chat_btn)
    }
}