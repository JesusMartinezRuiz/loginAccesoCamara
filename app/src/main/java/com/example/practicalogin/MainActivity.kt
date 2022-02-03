package com.example.practicalogin

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.io.Serializable
import java.util.concurrent.CountDownLatch
import java.util.concurrent.atomic.AtomicInteger


class MainActivity : AppCompatActivity() {

    lateinit var name:TextInputEditText
    lateinit var pass:TextInputEditText
    lateinit var login:Button
    lateinit var registrar:TextView
    lateinit var db_ref: DatabaseReference
    lateinit var sto_ref: StorageReference
    private lateinit var androidId:String
    private lateinit var generador: AtomicInteger


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        generador=AtomicInteger(0)

        androidId= Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)

        name=findViewById(R.id.main_et_nombre)
        pass=findViewById(R.id.main_et_contraseña)
        login=findViewById(R.id.main_btn_login)
        registrar=findViewById(R.id.main_tv_newAcc)

        db_ref= FirebaseDatabase.getInstance().reference
        sto_ref= FirebaseStorage.getInstance().reference

        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP_Login"
        var SP = getSharedPreferences(sp_name,0)


        login.setOnClickListener {

            db_ref.child("foodies")
                .child("usuarios")
                .orderByChild("nombre")
                .equalTo(name.text.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if(snapshot.hasChildren()){
                            var pojo_usuario= snapshot.children.iterator().next().getValue(Usuario::class.java)!!

                            if(pojo_usuario.nombre.equals(name.text.toString()) && pojo_usuario.contraseña.equals(pass.text.toString())){

                                with(SP.edit()){
                                    putString(
                                        getString(R.string.id),
                                        pojo_usuario.id
                                    )

                                    putString(
                                        getString(R.string.username),
                                        pojo_usuario.nombre
                                    )

                                    putString(
                                        getString(R.string.type),
                                        pojo_usuario.tipo
                                    )

                                    putString(
                                        getString(R.string.privado),
                                        pojo_usuario.privado.toString()
                                    )

                                    commit()
                                }

                                    val actividad = Intent(applicationContext,BienvenidoUsuario::class.java)
                                    startActivity (actividad)


                            }else if(name.text.toString().equals("") || pass.text.toString().equals("")){
                                Toast.makeText(applicationContext, "Por favor Rellene todos los datos", Toast.LENGTH_SHORT).show()

                            }else{
                                Toast.makeText(applicationContext, "Datos introducidos incorrectos", Toast.LENGTH_SHORT).show()

                            }
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {
                        println(error.message)
                    }
                })





        }


        registrar.setOnClickListener {
            val actividad = Intent(applicationContext,Registro::class.java)
            startActivity (actividad)
        }

        db_ref.child("foodies").child("usuarios").addChildEventListener(object : ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {

                val pojo_usuario2=snapshot.getValue(Usuario::class.java)
                if(!pojo_usuario2!!.user_notificador.equals(androidId) && pojo_usuario2.estado_noti==Estado.CREADO){
                    generarNotificacion(generador.incrementAndGet(),pojo_usuario2,"Se ha creado el Usuario "+pojo_usuario2.nombre,"Nuevos datos en la app",Registro::class.java)
                    db_ref.child("foodies").child("usuarios").child(pojo_usuario2.id!!).child("estado_noti").setValue(Estado.NOTIFICADO)
                }

            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {
                val pojo_usuario2=snapshot.getValue(Usuario::class.java)
                if(!pojo_usuario2!!.user_notificador.equals(androidId) && pojo_usuario2.estado_noti==Estado.MODIFICADO) {
                    generarNotificacion(generador.incrementAndGet(),pojo_usuario2,"El usuario "+pojo_usuario2.nombreAnterior+" ahora se llama "+pojo_usuario2.nombre,"Actualizacion de Usuario en la app",VerPerfil::class.java)
                    db_ref.child("foodies").child("usuarios").child(pojo_usuario2.id!!).child("estado_noti")
                        .setValue(Estado.NOTIFICADO)
                }else if(!pojo_usuario2!!.user_notificador.equals(androidId) && pojo_usuario2.estado_noti==Estado.MODIFICADO_NOMBRE){
                    generarNotificacion(generador.incrementAndGet(),pojo_usuario2,"Se ha modificado el usuario  "+pojo_usuario2.nombre,"Datos editados en la app",VerPerfil::class.java)
                    db_ref.child("foodies").child("usuarios").child(pojo_usuario2.id!!).child("estado_noti")
                        .setValue(Estado.NOTIFICADO)
                }


            }

            override fun onChildRemoved(snapshot: DataSnapshot) {
                val pojo_usuario2=snapshot.getValue(Usuario::class.java)
                if(!pojo_usuario2!!.user_notificador.equals(androidId)){
                    generarNotificacion(generador.incrementAndGet(),pojo_usuario2,"Se ha borrado el usuario "+pojo_usuario2.nombre,"Datos borrados en la app",VerLista::class.java)
                }
            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }

    private fun generarNotificacion(id_noti:Int, pojo: Serializable, contenido:String, titulo:String, destino:Class<*>) {
        val idcanal = getString(R.string.id_canal)
        val iconolargo = BitmapFactory.decodeResource(
            resources,
            R.drawable.logonotif
        )
        val actividad = Intent(applicationContext,destino)
        actividad.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK )
        actividad.putExtra("club", pojo)
        val pendingIntent= PendingIntent.getActivity(this,0,actividad, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(this, idcanal)
            .setLargeIcon(iconolargo)
            .setSmallIcon(R.drawable.logonotif)
            .setContentTitle(titulo)
            .setContentText(contenido)
            .setSubText("sistema de información")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        with(NotificationManagerCompat.from(this)){
            notify(id_noti,notification)
        }
    }

    private fun crearCanalNotificaciones() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nombre = getString(R.string.nombre_canal)
            val idcanal = getString(R.string.id_canal)
            val descripcion = getString(R.string.description_canal)
            val importancia = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(idcanal, nombre, importancia).apply {
                description = descripcion
            }

            val nm: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

}