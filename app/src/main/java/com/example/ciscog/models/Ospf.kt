package com.example.ciscog.models

class Ospf {

    var id: Int = 0
    var area: Int = 0
    var red: String = ""
    var wildcard: String = ""
    var dispositivo: String = ""

    constructor(area: Int, red: String, wildcard: String, dispositivo: String) {
        this.area = area
        this.red = red
        this.wildcard = wildcard
        this.dispositivo = dispositivo
    }

    constructor(){

    }
}
