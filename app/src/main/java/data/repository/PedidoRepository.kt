package com.example.compraejovem.data.repository

import com.example.compraejovem.data.remote.ItemPedidoDto
import com.example.compraejovem.data.remote.PedidoDto
import com.example.compraejovem.data.remote.SupabaseClientProvider
import com.example.compraejovem.model.ItemCarrinho
import io.github.jan.supabase.postgrest.postgrest

object PedidoRepository {

    private val client = SupabaseClientProvider.client

    /**
     * INSERT: cria o pedido em PEDIDOS e cada linha do carrinho em ITENS_PEDIDO.
     * Segue exatamente o fluxo pedido pelo professor:
     * 1) cria pedido com status PENDENTE
     * 2) cria um item em ITENS_PEDIDO por produto do carrinho
     * 3) calcula subtotal por item e valor_total do pedido
     */
    suspend fun finalizarCompra(
        idCliente: Int,
        idEnderecoEntrega: Int,
        itensCarrinho: List<ItemCarrinho>
    ): Result<Int> = runCatching {
        val valorTotal = itensCarrinho.sumOf { it.produto.preco * it.quantidade }

        val pedidoCriado = client.postgrest["pedidos"]
            .insert(
                PedidoDto(
                    idCliente = idCliente,
                    idEnderecoEntrega = idEnderecoEntrega,
                    status = "PENDENTE",
                    valorTotal = valorTotal
                )
            ) { select() }
            .decodeSingle<PedidoDto>()

        val idPedido = pedidoCriado.idPedido!!

        itensCarrinho.forEach { item ->
            val subtotal = item.produto.preco * item.quantidade
            val idProdutoGarantido = item.produto.id ?: 0 // Converte de Int? para Int

            client.postgrest["itens_pedido"].insert(
                ItemPedidoDto(
                    idPedido = idPedido,
                    idProduto = idProdutoGarantido,
                    quantidade = item.quantidade,
                    precoUnitario = item.produto.preco,
                    subtotal = subtotal
                )
            )
            // Atualiza estoque do produto vendido
            ProdutoRepository.baixarEstoque(idProdutoGarantido, item.quantidade)
        }

        idPedido
    }

    /** UPDATE: usado após confirmação/recusa do pagamento */
    suspend fun atualizarStatus(idPedido: Int, novoStatus: String) {
        client.postgrest["pedidos"]
            .update({ set("status", novoStatus) }) {
                filter { eq("id_pedido", idPedido) }
            }
    }

    /** SELECT: histórico de pedidos de um cliente */
    suspend fun listarPorCliente(idCliente: Int): List<PedidoDto> =
        client.postgrest["pedidos"]
            .select { filter { eq("id_cliente", idCliente) } }
            .decodeList()
}