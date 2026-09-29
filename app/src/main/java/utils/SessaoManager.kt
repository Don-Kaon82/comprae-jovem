package com.example.compraejovem.utils

object SessaoManager {
    var estaLogado: Boolean = false
    var emailUsuario: String? = null
    var eVendedor: Boolean = false

    fun fazerLogin(email: String, ehVendedor: Boolean) {
        estaLogado = true
        emailUsuario = email
        eVendedor = ehVendedor
    }

    fun fazerLogout() {
        estaLogado = false
        emailUsuario = null
        eVendedor = false
    }
}