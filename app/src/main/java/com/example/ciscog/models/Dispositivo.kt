package com.example.ciscog.models

class Dispositivo {

    var id: Int = 0
    var nombreDisp: String = ""
    var tipoDisp: String = ""
    var ipDisp: String = ""


    constructor(nombreDisp: String, tipoDisp: String, ipDisp: String) {
        this.nombreDisp = nombreDisp
        this.tipoDisp = tipoDisp
        this.ipDisp = ipDisp
    }

    constructor(){

    }
}
