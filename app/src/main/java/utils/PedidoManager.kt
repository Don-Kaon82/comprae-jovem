package com.example.compraejovem.utils

import com.example.compraejovem.model.Pedido

object PedidoManager {
    private val listaPedidos = mutableListOf<Pedido>()

    fun adicionarPedido(pedido: Pedido) {
        listaPedidos.add(0, pedido) // Adiciona o mais recente no topo
    }

    fun getPedidos(): List<Pedido> = listaPedidos.toList()
}