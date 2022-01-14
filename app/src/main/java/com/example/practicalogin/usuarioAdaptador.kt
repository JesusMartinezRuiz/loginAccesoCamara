package com.example.practicalogin

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import java.io.Serializable

class UsuarioAdaptador(private val lista_usuario:List<Usuario>) : RecyclerView.Adapter<UsuarioAdaptador.UsuarioViewHolder>() {
    private lateinit var contexto: Context


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsuarioViewHolder {
        val vista_item= LayoutInflater.from(parent.context).inflate(R.layout.item_usuario,parent, false)
        //Para poder hacer referencia al contexto de la aplicacion desde otros metodos de la clase
        contexto=parent.context

        return UsuarioViewHolder(vista_item)
    }

    override fun onBindViewHolder(holder: UsuarioViewHolder, position: Int) {
        val item_actual=lista_usuario[position]

        holder.nombre.text=item_actual.nombre
        holder.fecha.text=item_actual.fecha.toString()



        Glide.with(contexto).load(item_actual.url_usuario).into(holder.miniatura)

        holder.editar.setOnClickListener {

            val activity= Intent(contexto,VerPerfil::class.java)
            activity.putExtra("usuario",item_actual as Serializable)

            contexto.startActivity(activity)

        }

        holder.borrar.setOnClickListener {

            val androidId= Settings.Secure.getString(contexto.contentResolver, Settings.Secure.ANDROID_ID)
            val db_reference= FirebaseDatabase.getInstance().getReference()


            db_reference.child("foodies")
                .child("usuarios")
                .child(item_actual.id!!)
                .child("user_notificador")
                .setValue(androidId)

            db_reference.child("foodies")
                .child("usuarios")
                .child(item_actual.id!!)
                .removeValue()

            Toast.makeText(contexto,
                "Usuario borrado con exito",
                Toast.LENGTH_SHORT)
                .show()

        }

    }
    override fun getItemCount(): Int = lista_usuario.size

    class UsuarioViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        val miniatura: ImageView = itemView.findViewById(R.id.item_foto)
        val nombre: TextView = itemView.findViewById(R.id.item_nombre)
        val fecha: TextView = itemView.findViewById(R.id.item_fecha)
        val editar: ImageView =itemView.findViewById(R.id.item_editar)
        val borrar: ImageView =itemView.findViewById(R.id.item_borrar)


    }

}


