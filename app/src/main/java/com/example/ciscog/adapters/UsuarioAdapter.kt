package com.example.ciscog.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.MainActivity
import com.example.ciscog.R
import com.example.ciscog.models.Usuario
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class UsuarioAdapter(private val context: Activity, private var usuarios: ArrayList<Usuario>, private val emailActual: String) : RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder>() {

    private var db = DataBaseLITE(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_usuario, parent, false)
        return UsuarioViewHolder(view)
    }

    override fun getItemCount(): Int {
        return usuarios.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val usuario = usuarios[position]

        // Asignamos los datos del usuario al CardView
        holder.textViewId.text = usuario.id.toString()
        holder.textViewNombre.text = usuario.nombreUs
        holder.textViewApellido.text = usuario.apellidoUs
        holder.textViewEmail.text = usuario.emailUs
        holder.textViewRol.text = usuario.rolUs

        // Formateamos la fecha de creación como texto legible
        //holder.textViewFechaCreacion.text = "Creado el: ${usuario.fechaUs}"
        holder.textViewFechaCreacion.text = usuario.fechaUs


        holder.imageViewMenu.setOnClickListener { popupMenu(it, usuario, position) } // Menú emergente
    }

    // Menú emergente que se despliega en cada CardView del RecyclerView
    @SuppressLint("DiscouragedPrivateApi", "NotifyDataSetChanged", "MissingInflatedId")
    private fun popupMenu(view: View, usuario: Usuario, position: Int) {

        // Roles disponibles para el Spinner
        val roles = arrayOf("Administrador", "Empleado", "Usuario")
        val rolAdapter = ArrayAdapter(context, R.layout.spinner_item, roles)
        rolAdapter.setDropDownViewResource(R.layout.spinner_dropdown_item)

        // Inicia el menú emergente
        val popupMenu = PopupMenu(context, view)
        popupMenu.inflate(R.menu.show_menu)

        popupMenu.setOnMenuItemClickListener {

            when (it.itemId) {

                R.id.editText -> {
                    // Infla AlertDialog
                    val v = LayoutInflater.from(context).inflate(R.layout.edit_usuario_dialog, null)

                    // Inicializa items de AlertDialog
                    val textViewIdDialog: TextView = v.findViewById(R.id.textview_id_dialog)
                    val editTextNombre: EditText = v.findViewById(R.id.edittext_nombre_dialog)
                    val editTextApellido: EditText = v.findViewById(R.id.edittext_apellido_dialog)
                    val textViewEmail: TextView = v.findViewById(R.id.textview_email_dialog)
                    val textViewFechaCreacionDialog: TextView = v.findViewById(R.id.textview_fecha_creacion_dialog)
                    val spinnerRolDialog: Spinner = v.findViewById(R.id.spinner_rol_dialog)

                    // Inicializa el adaptador del Spinner
                    spinnerRolDialog.adapter = rolAdapter

                    // Asigna los datos del usuario al AlertDialog
                    textViewIdDialog.text = usuario.id.toString()
                    editTextNombre.text = Editable.Factory.getInstance().newEditable(usuario.nombreUs)
                    editTextApellido.text = Editable.Factory.getInstance().newEditable(usuario.apellidoUs)
                    textViewEmail.text = usuario.emailUs
                    textViewFechaCreacionDialog.text = "Creado el: ${usuario.fechaUs}"

                    if (roles.contains(usuario.rolUs)) {
                        val posicionRol = roles.indexOf(usuario.rolUs)
                        spinnerRolDialog.setSelection(posicionRol)
                    }

                    // Construye el AlertDialog
                    AlertDialog.Builder(context)
                        .setView(v)
                        .setPositiveButton("Ok") { dialog, _ ->
                            // Asigna los datos del AlertDialog al usuario
                            usuario.id = textViewIdDialog.text.toString().toInt()
                            usuario.nombreUs = editTextNombre.text.toString()
                            usuario.apellidoUs = editTextApellido.text.toString()
                            usuario.rolUs = spinnerRolDialog.selectedItem.toString()

                            // Actualizar el registro en la base de datos
                            if (db.updateDataUsuario(usuario)) {
                                notifyItemChanged(position) // Notifica al adaptador que el ítem ha cambiado
                                Toast.makeText(context, "Los datos del usuario se actualizaron correctamente", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(context, "Error al actualizar los datos del usuario", Toast.LENGTH_SHORT).show()
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
                R.id.delete -> {
                    if  (emailActual == usuario.emailUs) {
                        AlertDialog.Builder(context)
                            .setTitle("Eliminar")
                            .setIcon(R.drawable.ic_warning)
                            .setMessage("¿Estás seguro de eliminar esta información?")
                            .setPositiveButton("Sí") { dialog, _ ->

                                deleteItem(position)
                                Toast.makeText(context, "Los datos se eliminaron correctamente", Toast.LENGTH_SHORT).show()
                                dialog.dismiss()

                                // Espera de 2 segundos antes de redirigir al MainActivity
                                android.os.Handler().postDelayed({
                                    val intent = Intent(context, MainActivity::class.java)
                                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                    context.startActivity(intent)
                                }, 2000) // 2000 milisegundos = 2 segundos

                            }.setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    } else {
                        Toast.makeText(context, "Solamente puedes eliminar tu propio usuario del sistema", Toast.LENGTH_SHORT).show()
                    }

                    true
                }
                else -> true
            }
        }

        popupMenu.show()

        // Configuración para mostrar íconos (Editar, Eliminar) en el menú emergente (All versions)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            try {
                val popup = PopupMenu::class.java.getDeclaredField("mPopup")
                popup.isAccessible = true
                val menu = popup.get(popupMenu)
                menu.javaClass.getDeclaredMethod("setForceShowIcon", Boolean::class.java).invoke(menu, true)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun deleteItem(position: Int) {
        val usuario = usuarios[position]
        val credential = EmailAuthProvider.getCredential(usuario.emailUs, usuario.contrasenaUs)


        // Eliminar el ítem de la base de datos
        db.deleteDataUsuario(usuario.id)

        // Eliminar el ítem de la lista
        usuarios.removeAt(position)

        // Notificar al adaptador de los cambios
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, usuarios.size)

        // Eliminar el usuario de Firebase Authentication
        val firebaseUser: FirebaseUser? = FirebaseAuth.getInstance().currentUser
        firebaseUser?.reauthenticate(credential)?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                firebaseUser?.delete()?.addOnCompleteListener { deleteTask ->
                    if (deleteTask.isSuccessful) {
                        Toast.makeText(context, "Usuario eliminado de Firebase Authentication", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al eliminar el usuario de Firebase", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                Toast.makeText(context, "Error al reautentificar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Lista de elementos encontrados con el SearchView
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: ArrayList<Usuario>) {
        this.usuarios = mList
        notifyDataSetChanged()
    }

    class UsuarioViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textViewId: TextView = view.findViewById(R.id.textview_id)
        val textViewNombre: TextView = view.findViewById(R.id.textview_nombre)
        val textViewApellido: TextView = view.findViewById(R.id.textview_apellido)
        val textViewEmail: TextView = view.findViewById(R.id.textview_email)
        val textViewRol: TextView = view.findViewById(R.id.textview_rol)
        val textViewFechaCreacion: TextView = view.findViewById(R.id.textview_fecha_creacion)
        val imageViewMenu: ImageView = view.findViewById(R.id.imageview_menu)
    }
}
