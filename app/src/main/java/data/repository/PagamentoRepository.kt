package com.example.compraejovem.data.repository

import com.example.compraejovem.data.remote.PagamentoDto
import com.example.compraejovem.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest
import java.util.UUID

object PagamentoRepository {

    private val client = SupabaseClientProvider.client

    /**
     * INSERT: registra o pagamento de um pedido.
     * Ao aprovar, também atualiza o status do PEDIDO para PAGAMENTO_APROVADO.
     */
    suspend fun registrarPagamento(
        idPedido: Int,
        metodo: String, // PIX, CARTAO, BOLETO
        valor: Double,
        aprovado: Boolean
    ): Result<Int> = runCatching {
        val statusPagamento = if (aprovado) "APROVADO" else "RECUSADO"

        val pagamentoCriado = client.postgrest["pagamentos"]
            .insert(
                PagamentoDto(
                    idPedido = idPedido,
                    metodo = metodo,
                    valor = valor,
                    status = statusPagamento,
                    dataPagamento = "now()",
                    codigoTransacao = UUID.randomUUID().toString()
                )
            ) { select() }
            .decodeSingle<PagamentoDto>()

        if (aprovado) {
            PedidoRepository.atualizarStatus(idPedido, "PAGAMENTO_APROVADO")
        } else {
            PedidoRepository.atualizarStatus(idPedido, "CANCELADO")
        }

        pagamentoCriado.idPagamento!!
    }
}
