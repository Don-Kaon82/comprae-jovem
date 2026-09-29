package com.example.compraejovem.model

data class Pedido(
    val id: String,
    val dataHora: String,
    val dataPrevistaEntrega: String, // Nova data de previsão
    val total: Double,
    val formaPagamento: String,
    val enderecoEntrega: String,
    val status: String = "Em Separação 📦",
    val resumoItens: String
)