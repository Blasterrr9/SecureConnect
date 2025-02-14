package com.example.ciscog.models

class Acl {

    var id: Int = 0
    var tipo: String = ""
    var regla: String = ""
    var dispositivo: String = ""

    constructor(tipo: String, regla: String, dispositivo: String) {
        this.tipo = tipo
        this.regla = regla
        this.dispositivo = dispositivo
    }

    constructor(){

    }
}
