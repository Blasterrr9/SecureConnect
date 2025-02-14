package com.example.ciscog.models

class Nat {

    var id: Int = 0
    var tipo: String = ""
    var ipPriv: String = ""
    var ipPub: String = ""
    var dispositivo: String = ""

    constructor(tipo: String, ipPriv: String, ipPub: String, dispositivo: String) {
        this.tipo = tipo
        this.ipPriv = ipPriv
        this.ipPub = ipPub
        this.dispositivo = dispositivo
    }

    constructor(){

    }
}
