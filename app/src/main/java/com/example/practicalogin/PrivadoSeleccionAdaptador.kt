package com.example.practicalogin

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.Serializable

class PrivadoSeleccionAdaptador(private val lista_usuario:List<Usuario>) : RecyclerView.Adapter<PrivadoSeleccionAdaptador.UsuarioViewHolder>() {

    private lateinit var contexto: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {



        val vista_item= LayoutInflater.from(parent.context).inflate(R.layout.row_privado_seleccion,parent, false)
        //Para poder hacer referencia al contexto de la aplicacion desde otros metodos de la clase
        contexto=parent.context



        return UsuarioViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {

        val item_actual=lista_usuario[position]

        holder.nombre.text=item_actual.nombre
        Glide.with(contexto).load(item_actual.url_usuario).into(holder.miniatura)

        holder.cl.setOnClickListener {
            val inte = Intent (contexto, ChatPrivado::class.java)
            inte.putExtra("usuarioPrivado",item_actual)
            contexto.startActivity(inte)
        }


    }
    override fun getItemCount(): Int = lista_usuario.size

    class UsuarioViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val miniatura: ImageView = itemView.findViewById(R.id.iv_row_privado)
        val nombre: TextView = itemView.findViewById(R.id.tv_row_privado)
        val cl :ConstraintLayout = itemView.findViewById(R.id.privadoSelecLayout)

    }



}
