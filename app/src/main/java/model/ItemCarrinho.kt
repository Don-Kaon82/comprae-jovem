package com.example.compraejovem.model

data class ItemCarrinho(
    val produto: Produto,
    var quantidade: Int = 1
) {
    val subtotal: Double
        get() = produto.preco * quantidade
}