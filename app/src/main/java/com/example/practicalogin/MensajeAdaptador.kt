package com.example.practicalogin

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isGone
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MensajeAdaptador(private val lista_mensajes:List<Mensaje>) : RecyclerView.Adapter<MensajeAdaptador.MensajeViewHolder>() {
    private lateinit var contexto: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MensajeViewHolder {
        val vista_item =
            LayoutInflater.from(parent.context).inflate(R.layout.item_chat_publico, parent, false)
        //Para poder hacer referencia al contexto de la aplicacion
        contexto = parent.context

        return MensajeViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: MensajeViewHolder, position: Int) {
        val item_actual = lista_mensajes[position]

        if(item_actual.usuario_emisor==item_actual.usuario_receptor){
            //ES MIO,ASIGNAR A LA DERECHA Y YO
            holder.emisor.text=""
            holder.hora_emisor.text=""
            holder.nombre_emisor.text=""
            holder.nombre_receptor.text="Yo"
            holder.hora_receptor.text=item_actual.fecha_hora
            holder.yo.text=item_actual.contenido
            holder.imagen_receptor.isGone=false
            Glide.with(contexto).load(item_actual.img_usuario).into(holder.imagen_receptor)
            holder.imagen_emisor.isGone=true


        }else{
            //ES DE OTRO ASIGNAR A LA IZQUIERDA Y NOMBRE
            holder.yo.text=""
            holder.hora_receptor.text=""
            holder.nombre_receptor.text=""
            holder.nombre_emisor.text=item_actual.usuario_emisor
            holder.hora_emisor.text=item_actual.fecha_hora
            holder.emisor.text=item_actual.contenido
            holder.imagen_emisor.isGone=false
            Glide.with(contexto).load(item_actual.img_usuario).into(holder.imagen_emisor)
            holder.imagen_receptor.isGone=true
        }




    }

    override fun getItemCount(): Int = lista_mensajes.size


    class MensajeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val yo: TextView = itemView.findViewById(R.id.yo)
        val emisor: TextView = itemView.findViewById(R.id.emisor)
        val nombre_emisor: TextView = itemView.findViewById(R.id.nombre_emisor)
        val nombre_receptor: TextView = itemView.findViewById(R.id.nombre_receptor)
        val hora_receptor: TextView = itemView.findViewById(R.id.hora_receptor)
        val hora_emisor: TextView = itemView.findViewById(R.id.hora_emisor)
        val imagen_receptor : ImageView = itemView.findViewById(R.id.imagen_receptor)
        val imagen_emisor : ImageView = itemView.findViewById(R.id.imagen_emisor)
    }
}