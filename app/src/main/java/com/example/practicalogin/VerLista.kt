package com.example.practicalogin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class VerLista : AppCompatActivity() {

    lateinit var recycler:RecyclerView
    lateinit var lista:ArrayList<Usuario>
    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_lista)

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()
        lista=ArrayList<Usuario>()

        recycler=findViewById(R.id.lista)
        recycler.adapter=UsuarioAdaptador(lista)
        recycler.layoutManager=LinearLayoutManager(applicationContext)
        recycler.setHasFixedSize(true)


        db_ref.child("foodies")
            .child("usuarios")
            .addValueEventListener(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    lista.clear()
                    snapshot.children.forEach { hijo->
                        val pojo_usuario=hijo?.getValue(Usuario::class.java)
                        lista.add(pojo_usuario!!)
                    }
                    recycler.adapter?.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {

                }
            })

    }
}