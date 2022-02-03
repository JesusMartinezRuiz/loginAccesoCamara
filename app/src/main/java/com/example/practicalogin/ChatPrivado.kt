package com.example.practicalogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList

class ChatPrivado : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var lista:ArrayList<Mensaje>
    private lateinit var db_ref: DatabaseReference
    private lateinit var nombre_usuario:String
    private lateinit var mensaje_enviado: EditText
    private lateinit var boton_enviar: Button
    private lateinit var img_usuario : String
    private lateinit var id_user: String
    private lateinit var id_usuario:String
    lateinit var user:Usuario



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_privado)

        user= intent.getSerializableExtra("usuarioPrivado")as Usuario


        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP_Login"
        var SP = getSharedPreferences(sp_name,0)



        nombre_usuario= SP.getString(
            getString(R.string.username),
            "FailedShared"
        ).toString()

        id_user= SP.getString(
            getString(R.string.id),
            "FailedShared"
        ).toString()

        db_ref= FirebaseDatabase.getInstance().getReference()
        lista=ArrayList<Mensaje>()
        mensaje_enviado=findViewById(R.id.et_enviar_chat_privado)
        boton_enviar=findViewById(R.id.bt_enviar_mensaje_privado)


        boton_enviar.setOnClickListener{
            val mensaje=mensaje_enviado.text.toString().trim()


            if(mensaje!=""){
                val hoy: Calendar = Calendar.getInstance()
                val formateador: SimpleDateFormat = SimpleDateFormat("YYYY-MM-dd HH:mm:ss");
                val fecha_hora = formateador.format(hoy.getTime());

                val id_mensaje=db_ref.child("foodies").child("mensajes").push().key!!
                val nuevo_mensaje=Mensaje(id_mensaje,nombre_usuario,"",mensaje,fecha_hora,id_user,"",true)
                db_ref.child("foodies").child("mensajes").child(id_mensaje).setValue(nuevo_mensaje)
                mensaje_enviado.setText("")

            }else{
                Toast.makeText(applicationContext, "Escribe algo", Toast.LENGTH_SHORT).show()
            }
        }





        db_ref.child("foodies").child("mensajes").addChildEventListener(object: ChildEventListener {
            override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                GlobalScope.launch(Dispatchers.IO) {
                    val semaforo= CountDownLatch(1)

                    val pojo_mensaje = snapshot.getValue(Mensaje::class.java)
                    pojo_mensaje!!.usuario_receptor = nombre_usuario



                    db_ref.child("foodies").child("usuarios").child(pojo_mensaje.id_usuario!!)
                        .addListenerForSingleValueEvent(
                            object: ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {

                                    val user_img = snapshot?.getValue(Usuario::class.java)

                                    pojo_mensaje.img_usuario=user_img?.url_usuario



                                    semaforo.countDown()
                                }

                                override fun onCancelled(error: DatabaseError) {

                                }
                            }

                        )

                    semaforo.await()
                    if (pojo_mensaje.privado==true && pojo_mensaje.usuario_emisor==nombre_usuario){
                        lista.add(pojo_mensaje)
                    }
                    runOnUiThread {
                        recycler.adapter!!.notifyDataSetChanged()
                        recycler.scrollToPosition(lista.size - 1)
                    }



                }
            }

            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onChildRemoved(snapshot: DataSnapshot) {

            }

            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {

            }

            override fun onCancelled(error: DatabaseError) {

            }
        })



        recycler=findViewById(R.id.rv_chat_privado)
        recycler.adapter=MensajeAdaptador(lista)
        recycler.layoutManager= LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)


    }
}