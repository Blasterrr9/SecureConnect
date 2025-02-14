package com.example.ciscog.models

class Eigrp {

    var id: Int = 0
    var asnumber: Int = 0
    var red: String = ""
    var wildcard: String = ""
    var dispositivo: String = ""

    constructor(asnumber: Int, red: String, wildcard: String, dispositivo: String) {
        this.asnumber = asnumber
        this.red = red
        this.wildcard = wildcard
        this.dispositivo = dispositivo
    }

    constructor(){

    }
}
