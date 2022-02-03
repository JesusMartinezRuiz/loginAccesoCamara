package com.example.practicalogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class ChatPrivadoSeleccion : AppCompatActivity() {

    lateinit var recycler: RecyclerView
    lateinit var lista:ArrayList<Usuario>
    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference
    lateinit var nombre_usuario:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_privado_seleccion)

        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP_Login"
        var SP = getSharedPreferences(sp_name,0)



        nombre_usuario= SP.getString(
            getString(R.string.username),
            "FailedShared"
        ).toString()

            db_ref= FirebaseDatabase.getInstance().getReference()
            sto_ref= FirebaseStorage.getInstance().getReference()
            lista=ArrayList<Usuario>()

            recycler=findViewById(R.id.rv_privadoSeleccion)
            recycler.adapter=PrivadoSeleccionAdaptador(lista)
            recycler.layoutManager= LinearLayoutManager(applicationContext)
            recycler.setHasFixedSize(true)


            db_ref.child("foodies")
                .child("usuarios")
                .addValueEventListener(object: ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        lista.clear()
                        snapshot.children.forEach { hijo->
                            val pojo_usuario=hijo?.getValue(Usuario::class.java)
                            if (pojo_usuario!!.privado==false){

                            }else if (pojo_usuario.nombre==nombre_usuario){

                            }else{
                                lista.add(pojo_usuario!!)
                            }
                        }
                        recycler.adapter?.notifyDataSetChanged()
                    }
                    override fun onCancelled(error: DatabaseError) {

                    }
                })




    }
}