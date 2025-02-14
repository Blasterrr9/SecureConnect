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
import com.example.ciscog.DataBaseLITE
import com.example.ciscog.R
import com.example.ciscog.ReportePlantilla3
import com.example.ciscog.models.Reporte

class ReporteAdapter(private val context: Activity, private var reporte: ArrayList<Reporte>): RecyclerView.Adapter<ReporteAdapter.reporteViewHolder>()  {

    private var db = DataBaseLITE(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): reporteViewHolder {

        val view = LayoutInflater.from(parent.context).inflate(R.layout.cardview_reportes, parent, false)
        return reporteViewHolder(view)
    }

    override fun getItemCount(): Int {

        return reporte.size
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: reporteViewHolder, position: Int) {
    /*
        val listaVlan= vlan[position]

        //Asignamos los datos de la lista al Cardview
        holder.textViewId.text = listaVlan.id.toString()
        holder.textViewVlan_Id.text = listaVlan.vlan_id.toString()
        holder.textViewNombre_Vlan.text = listaVlan.nombre
        holder.textViewDisp.text = listaVlan.dispositivo*/

        // Redirigir a ChatActivity al hacer clic en el botón de chat
        holder.visMenu.setOnClickListener {
            val intent = Intent(context, ReportePlantilla3::class.java)
            context.startActivity(intent)
        }

        /*holder.imageViewMenu.setOnClickListener { popupMenu(it, listaVlan, position) }*/ //Menu emergente

    }


    //Lista de elemntos encontrados con el SearchView
    @SuppressLint("NotifyDataSetChanged")
    fun setFilteredList(mList: ArrayList<Reporte>){

        this.reporte = mList
        notifyDataSetChanged()
    }



    class reporteViewHolder(view: View): RecyclerView.ViewHolder(view){

        /*val textViewId: TextView = view.findViewById(R.id.textview_id)
        val textViewVlan_Id: TextView = view.findViewById(R.id.textview_vlan_id)
        val textViewNombre_Vlan: TextView = view.findViewById(R.id.textview_nombre_vlan)
        val textViewDisp: TextView = view.findViewById(R.id.textview_dispositivo)
        val imageViewMenu: ImageView = view.findViewById(R.id.imageview_menu)*/
        val visMenu: ImageButton = view.findViewById(R.id.visualizar_btn)
    }
}