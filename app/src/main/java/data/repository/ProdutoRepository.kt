package com.example.compraejovem.data.repository

import com.example.compraejovem.data.remote.ProdutoDto
import com.example.compraejovem.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest

object ProdutoRepository {

    private val client = SupabaseClientProvider.client

    /** SELECT: lista produtos disponíveis (usado na HomeActivity) */
    suspend fun listarDisponiveis(): List<ProdutoDto> =
        client.postgrest["produtos"]
            .select { filter { eq("status", "DISPONIVEL") } }
            .decodeList()

    /** SELECT: lista produtos de uma categoria específica */
    suspend fun listarPorCategoria(idCategoria: Int): List<ProdutoDto> =
        client.postgrest["produtos"]
            .select {
                filter {
                    eq("id_categoria", idCategoria)
                    eq("status", "DISPONIVEL")
                }
            }
            .decodeList()

    /** SELECT: detalhe de um produto (usado na DetalheProdutoActivity) */
    suspend fun buscarPorId(idProduto: Int): ProdutoDto =
        client.postgrest["produtos"]
            .select { filter { eq("id_produto", idProduto) } }
            .decodeSingle()

    /** INSERT: cadastro de novo produto (usado na CadastroProdutoActivity) */
    suspend fun cadastrar(produto: ProdutoDto): ProdutoDto =
        client.postgrest["produtos"]
            .insert(produto) { select() }
            .decodeSingle()

    /** UPDATE: baixa de estoque após confirmação de um pedido/pagamento */
    suspend fun baixarEstoque(idProduto: Int, quantidadeVendida: Int) {
        val produtoAtual = buscarPorId(idProduto)
        val novoEstoque = (produtoAtual.estoque - quantidadeVendida).coerceAtLeast(0)
        client.postgrest["produtos"]
            .update({ set("estoque", novoEstoque) }) {
                filter { eq("id_produto", idProduto) }
            }
    }
}
