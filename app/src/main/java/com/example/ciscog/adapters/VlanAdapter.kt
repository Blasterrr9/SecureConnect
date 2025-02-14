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
import com.example.ciscog.models.Vlan

class VlanAdapter(private val context: Activity, private var vlan: ArrayList<Vlan>): RecyclerView.Adapter<VlanAdapter.vlanViewHolder>()  {

    private var db = DataBaseLITE(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): vlanViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_vlan, parent, false)
        return vlanViewHolder(view)
    }

    override fun getItemCount(): Int {

        return vlan.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: vlanViewHolder, position: Int) {

        val listaVlan= vlan[position]

        //Asignamos los datos de la lista al Cardview
        holder.textViewId.text = listaVlan.id.toString()
        holder.textViewVlan_Id.text = listaVlan.vlan_id.toString()
        holder.textViewNombre_Vlan.text = listaVlan.nombre
        holder.textViewDisp.text = listaVlan.dispositivo

        // Redirigir a ChatActivity al hacer clic en el botón de chat
        holder.chatMenu.setOnClickListener {
            val mensajeRuta = """
                Para una topología de red en la cual se tiene creada una VLAN en un dispositivo Switch, explica los siguientes parámetros:
                
                VLAN ID: ${listaVlan.vlan_id}
                Nombre: ${listaVlan.nombre}
                Dispositivo: ${listaVlan.dispositivo}
            """.trimIndent()

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("config_info", mensajeRuta)
            context.startActivity(intent)

        }

        holder.imageViewMenu.setOnClickListener { popupMenu(it, listaVlan, position) } //Menu emergente

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
    private fun popupMenu(view: View, listaVlan: Vlan, position: Int){

        val dispositivoVlan = arrayOf("LS1-SW1", "LS1-SW2", "LS2-SW1", "LS2-SW2", "LS2-SW3")
        val dispositivoAdapter = ArrayAdapter(context, R.layout.spinner_item, dispositivoVlan)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        //Inicia el menú emergente
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.show_menu)

        popupMenu.setOnMenuItemClickListener {

            when(it.itemId){

                R.id.editText ->
                {

                    //Infla AlertDialog
                    val v = LayoutInflater.from(context).inflate(R.layout.edit_vlan_dialog, null)

                    //Inicializa items de AlertDialog
                    val textViewIdDialog: TextView = v.findViewById(R.id.textview_id_dialog)
                    val textViewVlan_IdDialog: TextView = v.findViewById(R.id.textview_vlan_id_dialog)
                    val editTextNombre_vlanDialog: EditText = v.findViewById(R.id.edittext_nombre_vlan_dialog)
                    val spinnerDispoDialog: Spinner = v.findViewById(R.id.spinner_dispositivo_dialog)

                    //Inicializa el adaptador de los spinner
                    spinnerDispoDialog.adapter = dispositivoAdapter

                    //Asigna los datos de la lista al AlertDialog-------------
                    textViewIdDialog.text = listaVlan.id.toString()
                    textViewVlan_IdDialog.text = listaVlan.vlan_id.toString()
                    editTextNombre_vlanDialog.text = Editable.Factory.getInstance().newEditable(listaVlan.nombre)

                    if (dispositivoVlan.contains(listaVlan.dispositivo)){

                        val posicionDispo = dispositivoVlan.indexOf(listaVlan.dispositivo)
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
                            listaVlan.id = textViewIdDialog.text.toString().toInt()
                            listaVlan.vlan_id = textViewVlan_IdDialog.text.toString().toInt()
                            listaVlan.nombre = editTextNombre_vlanDialog.text.toString()
                            listaVlan.dispositivo = spinnerDispoDialog.selectedItem.toString()

                            // Actualizar el registro en la base de datos
                            if (db.updateDataVLAN(listaVlan)) {
                                notifyItemChanged(position) // Notifica al adaptador que el ítem ha cambiado
                                Toast.makeText(context, "Los datos de la VLAN se actualizaron correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al actualizar los datos de la VLAN", Toast.LENGTH_SHORT).show()
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
        val registro = vlan[position]

        // Eliminar el ítem de la base de datos
        db.deleteDataVLAN(registro.id)

        // Eliminar el ítem de la lista
        vlan.removeAt(position)

        // Notificar al adaptador de los cambios
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, vlan.size)
    }

    //Lista de elemntos encontrados con el SearchView
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: ArrayList<Vlan>){

        this.vlan = mList
        notifyDataSetChanged()
    }

    fun getUserRole(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userRole", null)  // Retorna el rol o null si no está guardado
    }

    class vlanViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textViewId: TextView = view.findViewById(R.id.textview_id)
        val textViewVlan_Id: TextView = view.findViewById(R.id.textview_vlan_id)
        val textViewNombre_Vlan: TextView = view.findViewById(R.id.textview_nombre_vlan)
        val textViewDisp: TextView = view.findViewById(R.id.textview_dispositivo)
        val imageViewMenu: ImageView = view.findViewById(R.id.imageview_menu)
        val chatMenu: ImageButton = view.findViewById(R.id.chat_btn)
    }
}