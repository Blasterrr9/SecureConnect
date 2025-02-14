package com.example.ciscog.adapters

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.text.Editable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.models.Configuracion

class ConfigAdapter(private val context: Activity, private var config: ArrayList<Configuracion>): RecyclerView.Adapter<ConfigAdapter.configuracionViewHolder>()  {

    private var db = DataBaseLITE(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): configuracionViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_config, parent, false)
        return configuracionViewHolder(view)
    }

    override fun getItemCount(): Int {

        return config.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: configuracionViewHolder, position: Int) {

        val listConfig = config[position]

        val Usuario = db.getUsuarioById(listConfig.idUsuario)
        val Dispositivo = db.getDispositivoById(listConfig.idDispositivo)

        //Asignamos los datos de la lista al Cardview
        holder.textViewId.text = listConfig.id.toString()
        holder.textViewNombreConfig.text = listConfig.nombreConfig
        holder.textViewDescripcionConfig.text = listConfig.descripcionConfig
        holder.textViewTipoConfig.text = listConfig.tipoConfig
        holder.textViewFechaConfig.text = listConfig.fechaConfig
        holder.textViewIdUsuario.text = listConfig.idUsuario.toString() + " (" + Usuario!!.nombreUs + " " + Usuario.apellidoUs + ")"
        holder.textViewIdDispositivo.text = listConfig.idDispositivo.toString() + " (" + Dispositivo!!.nombreDisp + ")"

    }

    //Lista de elemntos encontrados con el SearchView
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: ArrayList<Configuracion>){

        this.config = mList
        notifyDataSetChanged()
    }

    class configuracionViewHolder(view: View): RecyclerView.ViewHolder(view){

        val textViewId: TextView = view.findViewById(R.id.textview_id)
        val textViewNombreConfig: TextView = view.findViewById(R.id.textview_nombre_config)
        val textViewDescripcionConfig: TextView = view.findViewById(R.id.textview_descripcion_config)
        val textViewTipoConfig: TextView = view.findViewById(R.id.textview_tipo_config)
        val textViewFechaConfig: TextView = view.findViewById(R.id.textview_fecha_config)
        val textViewIdUsuario: TextView = view.findViewById(R.id.textview_id_usuario)
        val textViewIdDispositivo: TextView = view.findViewById(R.id.textview_id_dispositivo)

    }
}