package com.example.ciscog.models

class RutasEst {

    var id: Int = 0
    var ipDestino: String = ""
    var mascara: String = ""
    var ipSalto: String = ""
    var dispositivo: String = ""

    constructor(ipDestino: String, mascara: String, ipSalto: String, dispositivo: String) {
        this.ipDestino = ipDestino
        this.mascara = mascara
        this.ipSalto = ipSalto
        this.dispositivo = dispositivo
    }

    constructor(){

    }
}
