package com.example.practicalogin

import java.io.Serializable

data class Mensaje(var id:String?=null,
                   var usuario_emisor:String?=null,
                   var usuario_receptor:String?=null,
                   var contenido:String?=null,
                   var fecha_hora:String?=null):Serializable