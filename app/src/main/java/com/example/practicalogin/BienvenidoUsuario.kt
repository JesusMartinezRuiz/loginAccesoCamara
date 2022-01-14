package com.example.practicalogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class BienvenidoUsuario : AppCompatActivity() {

    lateinit var nameBienvenido:TextView
    lateinit var verPerfil:Button
    lateinit var verLista:Button
    lateinit var logout:Button


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bienvenido_usuario)

        nameBienvenido=findViewById(R.id.bienvenido_user)
        verPerfil=findViewById(R.id.verPerfil_user)
        verLista=findViewById(R.id.verLista_user)
        logout=findViewById(R.id.Logout)


        val app_id = getString(R.string.app_name)
        val sp_name = "${app_id}_SP_Login"
        val SP = getSharedPreferences(sp_name,0)



        var nombreUser= SP.getString(
            getString(R.string.username),
            "FailedShared"
        )

        nameBienvenido.setText(nombreUser)


        verPerfil.setOnClickListener{
            val actividad = Intent(applicationContext,VerPerfil::class.java)
            startActivity (actividad)
        }

        verLista.setOnClickListener {

            val actividad = Intent(applicationContext,VerLista::class.java)
            startActivity (actividad)
        }


        logout.setOnClickListener {
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

    }
}