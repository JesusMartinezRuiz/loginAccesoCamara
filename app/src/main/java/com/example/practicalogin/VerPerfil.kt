package com.example.practicalogin

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isGone
import com.bumptech.glide.Glide
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.concurrent.CountDownLatch

class VerPerfil : AppCompatActivity() {

    lateinit var fecha:TextView
    lateinit var foto:ImageView
    lateinit var nombre:TextInputEditText
    lateinit var contraseña:TextInputEditText
    lateinit var platoEstrella:TextInputEditText
    lateinit var editar:Button
    lateinit var borrar:Button
    lateinit var ascender:Button
    lateinit var chatPublico:Button
    lateinit var chatPrivado:Button
    private var url_usuario: Uri?=null
    private lateinit var db_ref: DatabaseReference
    private lateinit var sto_ref: StorageReference


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ver_perfil)



        fecha=findViewById(R.id.ver_fecha)
        foto=findViewById(R.id.ver_foto)
        nombre=findViewById(R.id.ver_ipNombre)
        contraseña=findViewById(R.id.ver_ipContraseña)
        platoEstrella=findViewById(R.id.ver_ipPlatoEstrella)
        editar=findViewById(R.id.ver_editar)
        borrar=findViewById(R.id.ver_borrar)
        ascender=findViewById(R.id.ascender_admin)
        chatPrivado=findViewById(R.id.chatPrivado)
        chatPublico=findViewById(R.id.chatPublico)

        db_ref= FirebaseDatabase.getInstance().getReference()
        sto_ref= FirebaseStorage.getInstance().getReference()

        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP_Login"
        val SP = getSharedPreferences(sp_name,0)


        val spid= SP.getString(
            getString(R.string.id),
            "falloSharedID"
        )

        val spTipo= SP.getString(
            getString(R.string.type),
            "falloShareTipo"
        )

        if (spTipo=="1"){
            ascender.isGone=true
        }

        db_ref.child("foodies")
            .child("usuarios").child(spid!!)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {


                        var pojo_usuario= snapshot.getValue(Usuario::class.java)

                        fecha.setText(pojo_usuario?.fecha)
                        nombre.setText(pojo_usuario?.nombre)
                        contraseña.setText(pojo_usuario?.contraseña)
                        platoEstrella.setText(pojo_usuario?.platoEstrella)
                        Glide.with(applicationContext).load(pojo_usuario?.url_usuario).into(foto)


                        foto.setOnClickListener {
                            obtener_url.launch("image/*")
                        }

                        borrar.setOnClickListener {

                            val db_reference= FirebaseDatabase.getInstance().getReference()
                            db_reference.child("foodies")
                                .child("usuarios")
                                .child(pojo_usuario?.id!!)
                                .removeValue()



                            with(SP.edit()){
                                putString(
                                    getString(R.string.id),
                                    "Nothing"
                                )


                                putString(
                                    getString(R.string.username),
                                    "Nothing"
                                )

                                putString(
                                    getString(R.string.type),
                                    "Nothing"
                                )
                                commit()
                            }

                            val actividad = Intent(applicationContext,MainActivity::class.java)
                            startActivity (actividad)

                        }

                        editar.setOnClickListener{
                            if(nombre.text.toString().trim().equals("") ||
                                contraseña.text.toString().trim().equals("")||
                                platoEstrella.text.toString().trim().equals("") ) {

                                Toast.makeText(applicationContext, "Falta datos", Toast.LENGTH_SHORT).show()

                            }else{


                                var url_usuario_firebase:String?=pojo_usuario?.url_usuario
                                var estado:Int?=Estado.MODIFICADO

                                GlobalScope.launch(Dispatchers.IO) {
                                    if(!nombre.text.toString().trim().equals(pojo_usuario?.nombre) && existe_plato(nombre.text.toString().trim())){
                                        tostadaCorrutina("El Usuario ya existe")

                                    }else{
                                        if(url_usuario!=null){
                                            url_usuario_firebase=editarImagen(pojo_usuario?.id!!,url_usuario!!)
                                        }

                                        editarUsuario(pojo_usuario?.id!!,
                                            nombre.text.toString().trim(),
                                            contraseña.text.toString().trim(),
                                            pojo_usuario?.tipo!!,
                                            pojo_usuario?.fecha!!,
                                            pojo_usuario?.privado!!,
                                            platoEstrella.text.toString().trim(),
                                            url_usuario_firebase!!,
                                            estado!!,
                                            pojo_usuario.nombre!!
                                        )
                                        tostadaCorrutina("Datos de usuario modificados con éxito")

                                    }
                                }

                            }

                        }



                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        chatPublico.setOnClickListener {

        }

        chatPrivado.setOnClickListener{

        }


    }

    private fun existe_plato(nombre:String):Boolean{
        var resultado:Boolean?=false

        val semaforo= CountDownLatch(1)
        db_ref.child("foodies")
            .child("usuarios")
            .orderByChild("nombre")
            .equalTo(nombre)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if(snapshot.hasChildren()){
                        resultado=true;
                    }
                    semaforo.countDown()
                }

                override fun onCancelled(error: DatabaseError) {
                    println(error.message)
                }
            })

        semaforo.await();

        return resultado!!;
    }

    private suspend fun editarImagen(id:String,imagen:Uri):String{
        var url_plato_firebase:Uri?=null

        url_plato_firebase=sto_ref.child("foodies").child("usuarios").child(id)
            .putFile(imagen).await().storage.downloadUrl.await()

        return url_plato_firebase.toString()
    }

    private fun editarUsuario(id:String,nombre:String,contraseña:String,tipo:String,fecha:String,privado:Boolean,platoEstrella:String,url_firebase:String,estado: Int,nombreAnterior:String){
        val androidId= Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        val nuevo_usuario= Usuario(
            id,
            nombre,
            contraseña,
            tipo,
            fecha,
            privado,
            platoEstrella,
            url_firebase,
            estado,
            androidId,
            nombreAnterior
        )
        db_ref.child("foodies").child("usuarios").child(id).setValue(nuevo_usuario)

    }

    val obtener_url= registerForActivityResult(ActivityResultContracts.GetContent()){
            uri:Uri?->
        when (uri){
            null-> Toast.makeText(applicationContext,"No has seleccionado una imagen", Toast.LENGTH_SHORT).show()
            else->{
                url_usuario=uri
                foto.setImageURI(url_usuario)
                Toast.makeText(applicationContext,"Has seleccionado una nueva imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private suspend fun tostadaCorrutina(texto:String){
        runOnUiThread{
            Toast.makeText(
                applicationContext,
                texto,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}