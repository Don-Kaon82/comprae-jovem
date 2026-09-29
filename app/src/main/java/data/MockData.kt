package com.example.compraejovem.data

import com.example.compraejovem.model.Produto

object MockData {

    // Começa com a lista 100% vazia
    val produtos = mutableListOf<Produto>()

    fun adicionarProduto(produto: Produto) {
        produtos.add(0, produto) // Adiciona o novo produto cadastrado no topo da lista
    }
}