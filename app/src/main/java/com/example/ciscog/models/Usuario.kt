package com.example.ciscog.models

class Usuario {

    var id: Int = 0
    var nombreUs: String = ""
    var apellidoUs: String = ""
    var emailUs: String = ""
    var contrasenaUs: String = ""
    var rolUs: String = ""
    var fechaUs: String = ""

    constructor(nombreUs: String, apellidoUs: String, emailUs: String, contrasenaUs: String, rolUs: String, fechaUs: String) {
        this.nombreUs = nombreUs
        this.apellidoUs = apellidoUs
        this.emailUs = emailUs
        this.contrasenaUs = contrasenaUs
        this.rolUs = rolUs
        this.fechaUs = fechaUs
    }

    constructor(){

    }
}
