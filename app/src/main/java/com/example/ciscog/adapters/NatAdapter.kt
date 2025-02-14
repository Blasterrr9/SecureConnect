package com.example.ciscog.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
<<<<<<< HEAD
import android.content.Intent
=======
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
import android.os.Build
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
<<<<<<< HEAD
import com.example.ciscog.ChatActivity
=======
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Nat

class NatAdapter(private val context: Activity, private var nat: ArrayList<Nat>): RecyclerView.Adapter<NatAdapter.natViewHolder>()  {

    private var db = DataBaseLITE(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): natViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_nats, parent, false)
        return natViewHolder(view)
    }

    override fun getItemCount(): Int {

        return nat.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: natViewHolder, position: Int) {

        val listNat = nat[position]

        //Asignamos los datos de la lista al Cardview
        holder.textViewId.text = listNat.id.toString()
        holder.textViewTipo.text = listNat.tipo
        holder.textViewIpPriv.text = listNat.ipPriv
        holder.textViewIpPub.text = listNat.ipPub
        holder.textViewDisp.text = listNat.dispositivo

<<<<<<< HEAD
        // Redirigir a ChatActivity al hacer clic en el botón de chat
        holder.chatMenu.setOnClickListener {
            val mensajeRuta = """
                Para una topología de red en la cual se tiene aplicada una configuración de NAT a un dispositivo Router, explica la configuración siguiente (ID: ${listNat.id}):
                
                Tipo: ${listNat.tipo}
                IP Privada: ${listNat.ipPriv}
                IP Pública: ${listNat.ipPub}
                Dispositivo: ${listNat.dispositivo}
            """.trimIndent()

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("config_info", mensajeRuta)
            context.startActivity(intent)

        }

=======
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
        holder.imageViewMenu.setOnClickListener { popupMenu(it, listNat, position) } //Menu emergente

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
    private fun popupMenu(view: View, listNat: Nat, position: Int){

        //CONFIGURACIÓN DE LOS ADAPTADORES DEL SPINNER
        val tipoNat = arrayOf("Estática", "Dinámica")
        val tipoAdapter = ArrayAdapter(context, R.layout.spinner_item, tipoNat)
        tipoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        val dispositivoNat = arrayOf("LS1-R01", "LS1-R02", "LS2-R01", "LS2-R02", "LS2-R03")
        val dispositivoAdapter = ArrayAdapter(context, R.layout.spinner_item, dispositivoNat)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        //Inicia el menú emergente
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.show_menu)

        popupMenu.setOnMenuItemClickListener {

            when(it.itemId){

                R.id.editText ->
                {

                    //Infla AlertDialog
                    val v = LayoutInflater.from(context).inflate(R.layout.edit_nat_dialog, null)

                    //Inicializa items de AlertDialog
                    val textViewIdDialog: TextView = v.findViewById(R.id.textview_id_dialog)
                    val spinnerTipoDialog: Spinner = v.findViewById(R.id.spinner_tipo_dialog)
                    val editTextIpPrivDialog: EditText = v.findViewById(R.id.edittext_ippriv_dialog)
                    val editTextIpPubDialog: EditText = v.findViewById(R.id.edittext_ippub_dialog)
                    val spinnerDispoDialog: Spinner = v.findViewById(R.id.spinner_dispositivo_dialog)

                    //Inicializa el adaptador de los spinner
                    spinnerTipoDialog.adapter = tipoAdapter
                    spinnerDispoDialog.adapter = dispositivoAdapter

                    //Asigna los datos de la lista al AlertDialog-------------
                    textViewIdDialog.text = listNat.id.toString()
                    editTextIpPrivDialog.text = Editable.Factory.getInstance().newEditable(listNat.ipPriv)
                    editTextIpPubDialog.text = Editable.Factory.getInstance().newEditable(listNat.ipPub)

                    if (tipoNat.contains(listNat.tipo)){

                        val posicionTipo = tipoNat.indexOf(listNat.tipo)
                        spinnerTipoDialog.setSelection(posicionTipo)
                    }
                    if (dispositivoNat.contains(listNat.dispositivo)){

                        val posicionDispo = dispositivoNat.indexOf(listNat.dispositivo)
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
                            listNat.id = textViewIdDialog.text.toString().toInt()
                            listNat.tipo = spinnerTipoDialog.selectedItem.toString()
                            listNat.ipPriv = editTextIpPrivDialog.text.toString()
                            listNat.ipPub = editTextIpPubDialog.text.toString()
                            listNat.dispositivo = spinnerDispoDialog.selectedItem.toString()

<<<<<<< HEAD
                            if(db.isNATExist(listNat.tipo, listNat.ipPriv, listNat.ipPub, listNat.dispositivo)){
                                Toast.makeText(context, "Esa Configuración de NAT ya existe", Toast.LENGTH_SHORT).show()
                            } else {
                                // Actualizar el registro en la base de datos
                                if (db.updateDataNAT(listNat)) {
                                    notifyItemChanged(position) // Notifica al adaptador que el ítem ha cambiado
                                    Toast.makeText(context, "Los datos de la configuración NAT se actualizaron correctamente", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al actualizar los datos de la configuración NAT", Toast.LENGTH_SHORT).show()
                                }
                            }
=======
                            // Actualizar el registro en la base de datos
                            if (db.updateDataNAT(listNat)) {
                                notifyItemChanged(position) // Notifica al adaptador que el ítem ha cambiado
                                Toast.makeText(context, "Los datos de la configuración NAT se actualizaron correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al actualizar los datos de la configuración NAT", Toast.LENGTH_SHORT).show()
                            }

>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
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
        val laptop = nat[position]

        // Eliminar el ítem de la base de datos
        db.deleteDataNAT(laptop.id)

        // Eliminar el ítem de la lista
        nat.removeAt(position)

        // Notificar al adaptador de los cambios
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, nat.size)
    }

    //Lista de elemntos encontrados con el SearchView
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: ArrayList<Nat>){

        this.nat = mList
        notifyDataSetChanged()
    }

    fun getUserRole(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userRole", null)  // Retorna el rol o null si no está guardado
    }

    class natViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textViewId: TextView = view.findViewById(R.id.textview_id)
        val textViewTipo: TextView = view.findViewById(R.id.textview_tipo)
        val textViewIpPriv: TextView = view.findViewById(R.id.textview_ipPriv)
        val textViewIpPub: TextView = view.findViewById(R.id.textview_ipPub)
        val textViewDisp: TextView = view.findViewById(R.id.textview_dispositivo)
        val imageViewMenu: ImageView = view.findViewById(R.id.imageview_menu)
<<<<<<< HEAD
        val chatMenu: ImageButton = view.findViewById(R.id.chat_btn)
=======
>>>>>>> 836dac7 (Primer prototipo de la aplicación(recuperado))
    }
}