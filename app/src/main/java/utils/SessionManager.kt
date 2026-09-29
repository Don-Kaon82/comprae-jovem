package com.example.compraejovem.utils

/**
 * Guarda o e-mail do usuário autenticado durante a sessão do app.
 * O Supabase Auth já mantém a sessão real (token) internamente;
 * isso aqui é só um atalho para as telas saberem qual USUARIOS/CLIENTES
 * usar nas consultas, sem precisar repassar o e-mail via Intent extras
 * em toda tela.
 */
object SessionManager {
    var emailLogado: String? = null
        private set

    fun login(email: String) {
        emailLogado = email
    }

    fun logout() {
        emailLogado = null
    }
}
