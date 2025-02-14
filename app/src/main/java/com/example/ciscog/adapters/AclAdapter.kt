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
import com.example.ciscog.models.Acl

class AclAdapter(private val context: Activity, private var acl: ArrayList<Acl>): RecyclerView.Adapter<AclAdapter.aclViewHolder>()  {

    private var db = DataBaseLITE(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): aclViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_acl, parent, false)
        return aclViewHolder(view)
    }

    override fun getItemCount(): Int {

        return acl.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: aclViewHolder, position: Int) {

        val listaAcl = acl[position]

        //Asignamos los datos de la lista al Cardview
        holder.textViewId.text = listaAcl.id.toString()
        holder.textViewTipo_Acl.text = listaAcl.tipo
        holder.textViewRegla.text = listaAcl.regla
        holder.textViewDisp.text = listaAcl.dispositivo

        // Redirigir a ChatActivity al hacer clic en el botón de chat
        holder.chatMenu.setOnClickListener {
            val mensajeRuta = """
                Para una topología de red en la cual se tiene aplicada una Lista de Control de Acceso a un dispositivo Router, explica la configuración siguiente (ID: ${listaAcl.id}):
                
                Tipo: ${listaAcl.tipo}
                Regla: ${listaAcl.regla}
                Dispositivo: ${listaAcl.dispositivo}
            """.trimIndent()

            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("config_info", mensajeRuta)
            context.startActivity(intent)

        }

        holder.imageViewMenu.setOnClickListener { popupMenu(it, listaAcl, position) } //Menu emergente

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
    private fun popupMenu(view: View, listaAcl: Acl, position: Int){

        val tipoAcl = arrayOf("Estándar", "Extendida")
        val tipoAdapter = ArrayAdapter(context, R.layout.spinner_item, tipoAcl)
        tipoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        val accionAcl = arrayOf("permit", "deny")
        val accionAdapter = ArrayAdapter(context, R.layout.spinner_item, accionAcl)
        accionAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        val dispositivoAcl = arrayOf("LS1-R01", "LS1-R02", "LS2-R01", "LS2-R02", "LS2-R03")
        val dispositivoAdapter = ArrayAdapter(context, R.layout.spinner_item, dispositivoAcl)
        dispositivoAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        //Inicia el menú emergente
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.show_menu)

        popupMenu.setOnMenuItemClickListener {

            when(it.itemId){

                R.id.editText ->
                {

                    //Infla AlertDialog
                    val v = LayoutInflater.from(context).inflate(R.layout.edit_acl_dialog, null)

                    //Inicializa items de AlertDialog
                    val textViewIdDialog: TextView = v.findViewById(R.id.textview_id_dialog)
                    val spinnerTipo_AclDialog: Spinner = v.findViewById(R.id.spinner_tipo_acl_dialog)
                    val spinnerAccionDialog: Spinner = v.findViewById(R.id.spinner_accion_dialog)
                    val editTextReglaDialog: EditText = v.findViewById(R.id.edittext_regla_dialog)
                    val spinnerDispoDialog: Spinner = v.findViewById(R.id.spinner_dispositivo_dialog)
                    val chatMenuDialog: ImageButton = v.findViewById(R.id.chat_btn_dialog)

                    // Redirigir a ChatActivity al hacer clic en el botón de chat
                    chatMenuDialog.setOnClickListener {
                        val mensajeRuta = """
                            A continuación, voy a realizar una Lista de Control de Acceso en un dispositivo Router, ayúdame a realizarla con los parámetros que te voy a dar a continuación:
                        """.trimIndent()

                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("config_info", mensajeRuta)
                        context.startActivity(intent)
                    }

                    //Inicializa el adaptador de los spinner
                    spinnerAccionDialog.adapter = accionAdapter
                    spinnerTipo_AclDialog.adapter = tipoAdapter
                    spinnerDispoDialog.adapter = dispositivoAdapter

                    //Asigna los datos de la lista al AlertDialog-------------
                    textViewIdDialog.text = listaAcl.id.toString()
                    val accion = listaAcl.regla.split(" ")[0]
                    val regla_sin_accion = listaAcl.regla.split(" ").drop(1).joinToString(" ")

                    editTextReglaDialog.text = Editable.Factory.getInstance().newEditable(regla_sin_accion)

                    if (accionAcl.contains(accion)){
                        val posicionAccion = accionAcl.indexOf(accion)
                        spinnerAccionDialog.setSelection(posicionAccion)
                    }

                    if (tipoAcl.contains(listaAcl.tipo)){
                        val posicionAcl = tipoAcl.indexOf(listaAcl.tipo)
                        spinnerTipo_AclDialog.setSelection(posicionAcl)
                    }

                    if (dispositivoAcl.contains(listaAcl.dispositivo)){
                        val posicionDispo = dispositivoAcl.indexOf(listaAcl.dispositivo)
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

                            // Asignamos los datos del AlertDialog a la lista
                            val tipoAcl = spinnerTipo_AclDialog.selectedItem.toString()
                            val reglaAcl = editTextReglaDialog.text.toString()

                            // Validar el formato de la regla según el tipo de ACL
                            val isValidAcl = validarFormatoAcl(tipoAcl, reglaAcl)
                            if (!isValidAcl) {
                                val builder = AlertDialog.Builder(context)
                                builder.setTitle("Advertencia") // Título del diálogo
                                builder.setIcon(R.drawable.ic_warning) // Ícono del diálogo
                                builder.setMessage("El formato de la regla no es válido para el tipo de ACL seleccionado") // Mensaje del diálogo
                                builder.setPositiveButton("Aceptar", null) // Botón de aceptación sin acción adicional
                                val dialogo: AlertDialog = builder.create() // Crea el diálogo
                                dialogo.show() // Muestra el diálogo
                                return@setPositiveButton
                            }

                            //Asignamos los datos del AlertDialog a la lista
                            listaAcl.id = textViewIdDialog.text.toString().toInt()
                            listaAcl.tipo = spinnerTipo_AclDialog.selectedItem.toString()
                            listaAcl.regla = "${spinnerAccionDialog.selectedItem.toString()} ${editTextReglaDialog.text.toString()}"
                            listaAcl.dispositivo = spinnerDispoDialog.selectedItem.toString()

                            if (db.isACLExist(listaAcl.tipo, listaAcl.regla, listaAcl.dispositivo)) {
                                Toast.makeText(context, "La Lista de Control de Acceso ya existe", Toast.LENGTH_SHORT).show()
                            } else {
                                // Actualizar el registro en la base de datos
                                if (db.updateDataACL(listaAcl)) {
                                    notifyItemChanged(position) // Notifica al adaptador que el ítem ha cambiado
                                    Toast.makeText(context, "Los datos de la ACL se actualizaron correctamente", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "Error al actualizar los datos de la ACL", Toast.LENGTH_SHORT).show()
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
        val registro = acl[position]

        // Eliminar el ítem de la base de datos
        db.deleteDataACL(registro.id)

        // Eliminar el ítem de la lista
        acl.removeAt(position)

        // Notificar al adaptador de los cambios
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, acl.size)
    }

    //Lista de elemntos encontrados con el SearchView
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: ArrayList<Acl>){

        this.acl = mList
        notifyDataSetChanged()
    }

    fun getUserRole(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPreferences.getString("userRole", null)  // Retorna el rol o null si no está guardado
    }

    private fun validarFormatoAcl(tipo: String, regla: String): Boolean {
        return if (tipo == "Estándar") {
            // Validación para ACL estándar: la regla debe seguir el formato esperado (por ejemplo, "host <ip>")
            val regex = Regex("^(\\d{1,3}(\\.\\d{1,3}){3}\\s+\\d{1,3}(\\.\\d{1,3}){3}\$)")
            regex.matches(regla)
        } else if (tipo == "Extendida") {
            // Validación básica para ACL extendida
            val contieneProtocolo = regla.contains(Regex("\\b(TCP|UDP|ICMP)\\b", RegexOption.IGNORE_CASE))
            val contieneIp = regla.contains(Regex("\\d{1,3}(\\.\\d{1,3}){3}"))
            val contieneAny = regla.contains("any", ignoreCase = true)

            // Regla mínima: debe tener al menos protocolo, una IP o "any", y una longitud mínima
            contieneProtocolo && (contieneIp || contieneAny) && regla.length >= 15
        } else {
            false // Tipo no válido
        }
    }


    class aclViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textViewId: TextView = view.findViewById(R.id.textview_id)
        val textViewTipo_Acl: TextView = view.findViewById(R.id.textview_tipo_acl)
        val textViewRegla: TextView = view.findViewById(R.id.textview_regla)
        val textViewDisp: TextView = view.findViewById(R.id.textview_dispositivo)
        val imageViewMenu: ImageView = view.findViewById(R.id.imageview_menu)
        val chatMenu: ImageButton = view.findViewById(R.id.chat_btn)
    }
}