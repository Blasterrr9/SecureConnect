package com.example.ciscog.models

class Vlan {

    var id: Int = 0
    var vlan_id: Int = 0
    var nombre: String = ""
    var dispositivo: String = ""

    constructor(vlan_id: Int, nombre: String, dispositivo: String) {
        this.vlan_id = vlan_id
        this.nombre = nombre
        this.dispositivo = dispositivo
    }

    constructor(){

    }
}
