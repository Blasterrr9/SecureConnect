package com.example.ciscog.models

class Stp {

    var id: Int = 0
    var prioridad: Int = 0
    var vlan_id: Int = 0
    var modo: String = ""
    var dispositivo: String = ""

    constructor(prioridad: Int, vlan_id: Int, modo: String, dispositivo: String) {
        this.prioridad = prioridad
        this.vlan_id = vlan_id
        this.modo = modo
        this.dispositivo = dispositivo
    }

    constructor(){

    }
}
