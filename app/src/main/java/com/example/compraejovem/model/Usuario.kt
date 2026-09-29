package com.example.compraejovem.model

// Corrigido: estava em "com.compraejovem.app.model" (pacote inconsistente
// com o resto do projeto, que usa "com.example.compraejovem"). Isso não
// afeta o banco, é só uma correção de organização do código-fonte.
data class Usuario(
    val nome: String,
    val email: String,
    val tipo: String = "aluno" // "aluno" ou "professor"
)
