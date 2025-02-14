package com.example.ciscog.models

class Reporte {

    var id: Int = 0
    var tipo: String = ""
    var atributo1_titulo: String = ""
    var atributo1_valor: String = ""
    var atributo2_titulo: String = ""
    var atributo2_valor: String = ""
    var atributo3_titulo: String = ""
    var atributo3_valor: String = ""
    var atributo4_titulo: String = ""
    var atributo4_valor: String = ""

    constructor(tipo: String, atributo1_titulo: String, atributo1_valor: String, atributo2_titulo: String, atributo2_valor: String, atributo3_titulo: String, atributo3_valor: String, atributo4_titulo: String, atributo4_valor: String) {
        this.tipo = tipo
        this.atributo1_titulo = atributo1_titulo
        this.atributo1_valor = atributo1_valor
        this.atributo2_titulo = atributo2_titulo
        this.atributo2_valor = atributo2_valor
        this.atributo3_titulo = atributo3_titulo
        this.atributo3_valor = atributo3_valor
        this.atributo4_titulo = atributo4_titulo
        this.atributo4_valor = atributo4_valor
    }

    constructor(){

    }
}
