package com.example.ciscog.models

class Configuracion {

    var id: Int = 0
    var nombreConfig: String = ""
    var descripcionConfig: String = ""
    var tipoConfig: String = ""
    var fechaConfig: String = ""
    var idUsuario: Int = 0
    var idDispositivo: Int = 0


    constructor(nombreConfig: String, descripcionConfig: String, tipoConfig: String, fechaConfig: String, idUsuario: Int, idDispositivo: Int) {
        this.nombreConfig = nombreConfig
        this.descripcionConfig = descripcionConfig
        this.tipoConfig = tipoConfig
        this.fechaConfig = fechaConfig
        this.idUsuario = idUsuario
        this.idDispositivo = idDispositivo
    }

    constructor(){

    }
}
