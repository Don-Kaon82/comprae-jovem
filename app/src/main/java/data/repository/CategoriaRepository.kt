package com.example.compraejovem.data.repository

import com.example.compraejovem.data.remote.CategoriaDto
import com.example.compraejovem.data.remote.SupabaseClientProvider
import io.github.jan.supabase.postgrest.postgrest

object CategoriaRepository {

    private val client = SupabaseClientProvider.client

    /** SELECT: lista todas as categorias ativas */
    suspend fun listarAtivas(): List<CategoriaDto> =
        client.postgrest["categorias"]
            .select { filter { eq("status", "ATIVA") } }
            .decodeList()

    /**
     * O app atual pede a categoria como texto livre (não como FK), mas o
     * modelo do professor exige CATEGORIAS.id_categoria como FK em PRODUTOS.
     * Esta função faz a ponte: procura uma categoria com esse nome e, se não
     * existir, cria uma nova — sem duplicar nem inventar uma estrutura nova.
     */
    suspend fun buscarOuCriarPorNome(nome: String): Int {
        val existente = client.postgrest["categorias"]
            .select { filter { eq("nome", nome) } }
            .decodeList<CategoriaDto>()

        if (existente.isNotEmpty()) return existente.first().idCategoria!!

        val criada = client.postgrest["categorias"]
            .insert(CategoriaDto(nome = nome)) { select() }
            .decodeSingle<CategoriaDto>()
        return criada.idCategoria!!
    }
}
