package com.example.compraejovem.data.remote

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Cada DTO usa @SerialName para mapear exatamente o nome da coluna no Postgres,
// já que Kotlin normalmente usa camelCase e o banco usa snake_case.

@Serializable
data class UsuarioDto(
    @SerialName("id_usuario") val idUsuario: Int? = null,
    val nome: String,
    val email: String,
    @SerialName("senha_hash") val senhaHash: String? = null, // não usado se Supabase Auth cuidar da senha
    val status: String = "ATIVO",
    @SerialName("data_cadastro") val dataCadastro: String? = null,
    @SerialName("ultimo_acesso") val ultimoAcesso: String? = null
)

@Serializable
data class PerfilDto(
    @SerialName("id_perfil") val idPerfil: Int? = null,
    val nome: String,
    val descricao: String? = null
)

@Serializable
data class UsuarioPerfilDto(
    @SerialName("id_usuario") val idUsuario: Int,
    @SerialName("id_perfil") val idPerfil: Int
)

@Serializable
data class ClienteDto(
    @SerialName("id_cliente") val idCliente: Int? = null,
    @SerialName("id_usuario") val idUsuario: Int,
    val cpf: String? = null,
    val telefone: String? = null,
    @SerialName("data_nascimento") val dataNascimento: String? = null
)

@Serializable
data class EnderecoDto(
    @SerialName("id_endereco") val idEndereco: Int? = null,
    @SerialName("id_cliente") val idCliente: Int,
    val cep: String? = null,
    val logradouro: String? = null,
    val numero: String? = null,
    val complemento: String? = null,
    val bairro: String? = null,
    val cidade: String? = null,
    val estado: String? = null,
    val tipo: String = "RESIDENCIAL",
    val principal: Boolean = false
)

@Serializable
data class CategoriaDto(
    @SerialName("id_categoria") val idCategoria: Int? = null,
    val nome: String,
    val descricao: String? = null,
    val status: String = "ATIVA"
)

@Serializable
data class ProdutoDto(
    @SerialName("id_produto") val idProduto: Int? = null,
    @SerialName("id_categoria") val idCategoria: Int,
    @SerialName("id_cliente_vendedor") val idClienteVendedor: Int? = null,
    val nome: String,
    val descricao: String? = null,
    val preco: Double,
    val estoque: Int = 0,
    val imagem: String? = null,
    val status: String = "DISPONIVEL",
    @SerialName("data_cadastro") val dataCadastro: String? = null
)

@Serializable
data class PedidoDto(
    @SerialName("id_pedido") val idPedido: Int? = null,
    @SerialName("id_cliente") val idCliente: Int,
    @SerialName("id_endereco_entrega") val idEnderecoEntrega: Int,
    @SerialName("data_pedido") val dataPedido: String? = null,
    val status: String = "PENDENTE",
    @SerialName("valor_total") val valorTotal: Double
)

@Serializable
data class ItemPedidoDto(
    @SerialName("id_item") val idItem: Int? = null,
    @SerialName("id_pedido") val idPedido: Int,
    @SerialName("id_produto") val idProduto: Int,
    val quantidade: Int,
    @SerialName("preco_unitario") val precoUnitario: Double,
    val subtotal: Double
)

@Serializable
data class PagamentoDto(
    @SerialName("id_pagamento") val idPagamento: Int? = null,
    @SerialName("id_pedido") val idPedido: Int,
    val metodo: String, // PIX, CARTAO, BOLETO
    val valor: Double,
    val status: String = "PENDENTE",
    @SerialName("data_pagamento") val dataPagamento: String? = null,
    @SerialName("codigo_transacao") val codigoTransacao: String? = null
)
