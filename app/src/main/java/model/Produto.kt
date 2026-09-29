package com.example.compraejovem.model

import com.example.compraejovem.R
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Produto(
    @SerialName("id_produto") val id: Int? = null,
    @SerialName("id_categoria") val idCategoria: Int? = 1,
    val nome: String = "",
    val descricao: String? = "",
    val preco: Double = 0.0,
    val estoque: Int = 10,
    val imagem: String? = null,
    val status: String? = "ativo",
    val professor: String = "Prof. Vendedor"
)