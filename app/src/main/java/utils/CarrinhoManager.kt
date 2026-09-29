package com.example.compraejovem.utils

import com.example.compraejovem.model.ItemCarrinho
import com.example.compraejovem.model.Produto

object CarrinhoManager {
    private val itens = mutableListOf<ItemCarrinho>()

    fun adicionar(produto: Produto) {
        val idProd = produto.id ?: 0
        val existente = itens.find { (it.produto.id ?: 0) == idProd }
        if (existente != null) {
            existente.quantidade++
        } else {
            itens.add(ItemCarrinho(produto, 1))
        }
    }

    fun aumentarQuantidade(produtoId: Int?) {
        val idProd = produtoId ?: 0
        val item = itens.find { (it.produto.id ?: 0) == idProd }
        item?.let { it.quantidade++ }
    }

    fun diminuirQuantidade(produtoId: Int?) {
        val idProd = produtoId ?: 0
        val item = itens.find { (it.produto.id ?: 0) == idProd }
        if (item != null) {
            if (item.quantidade > 1) {
                item.quantidade--
            } else {
                itens.remove(item)
            }
        }
    }

    fun remover(produtoId: Int?) {
        val idProd = produtoId ?: 0
        itens.removeAll { (it.produto.id ?: 0) == idProd }
    }

    fun getItens(): List<ItemCarrinho> = itens.toList()

    fun getTotal(): Double = itens.sumOf { it.subtotal }

    fun getQuantidadeTotal(): Int = itens.sumOf { it.quantidade }

    fun limpar() = itens.clear()
}