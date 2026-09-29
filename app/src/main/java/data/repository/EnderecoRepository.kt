package com.example.compraejovem.data.repository

import com.example.compraejovem.data.remote.EnderecoDto
import com.example.compraejovem.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest

object EnderecoRepository {

    private val client = SupabaseClientProvider.client

    /** SELECT: lista todos os endereços de um cliente */
    suspend fun listarPorCliente(idCliente: Int): List<EnderecoDto> =
        client.postgrest["enderecos"]
            .select { filter { eq("id_cliente", idCliente) } }
            .decodeList()

    /** INSERT: cadastra um novo endereço */
    suspend fun cadastrar(endereco: EnderecoDto): EnderecoDto =
        client.postgrest["enderecos"]
            .insert(endereco) { select() }
            .decodeSingle()

    /** UPDATE: edita um endereço existente */
    suspend fun editar(idEndereco: Int, endereco: EnderecoDto) {
        client.postgrest["enderecos"]
            .update(endereco) {
                filter { eq("id_endereco", idEndereco) }
            }
    }

    /** DELETE: remove um endereço */
    suspend fun excluir(idEndereco: Int) {
        client.postgrest["enderecos"]
            .delete { filter { eq("id_endereco", idEndereco) } }
    }

    /** UPDATE: define um endereço como principal e desmarca os outros do mesmo cliente */
    suspend fun definirComoPrincipal(idCliente: Int, idEndereco: Int) {
        client.postgrest["enderecos"]
            .update({ set("principal", false) }) {
                filter { eq("id_cliente", idCliente) }
            }
        client.postgrest["enderecos"]
            .update({ set("principal", true) }) {
                filter { eq("id_endereco", idEndereco) }
            }
    }
}
