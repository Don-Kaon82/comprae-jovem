package com.example.compraejovem.data.repository

import com.example.compraejovem.data.remote.ClienteDto
import com.example.compraejovem.data.remote.SupabaseClientProvider
import com.example.compraejovem.data.remote.UsuarioDto
import com.example.compraejovem.data.remote.UsuarioPerfilDto
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

/**
 * Autenticação real via Supabase Auth (substitui a senha fixa "1234"
 * que existia no LoginActivity).
 *
 * A senha em si é gerenciada pelo Supabase Auth (nunca fica em texto puro
 * no nosso banco). A tabela USUARIOS guarda os dados de perfil do app;
 * o campo senha_hash pode ficar nulo/"GERENCIADO_PELO_SUPABASE_AUTH" já
 * que a autenticação de fato acontece no Auth do Supabase.
 */
object AuthRepository {

    private val client = SupabaseClientProvider.client

    /** Cadastro de um novo cliente: cria o Auth user + USUARIOS + CLIENTES + vínculo com perfil CLIENTE */
    suspend fun cadastrarCliente(
        nome: String,
        email: String,
        senha: String,
        cpf: String?,
        telefone: String?,
        dataNascimento: String? // formato "yyyy-MM-dd"
    ): Result<Int> = runCatching {
        // 1. Cria o usuário no Supabase Auth
        client.auth.signUpWith(Email) {
            this.email = email
            this.password = senha
        }

        // 2. Insere na tabela USUARIOS (senha_hash fica marcado, pois quem
        //    autentica de fato é o Supabase Auth)
        val usuarioInserido = client.postgrest["usuarios"]
            .insert(
                UsuarioDto(nome = nome, email = email, senhaHash = "GERENCIADO_PELO_SUPABASE_AUTH"),
            ) { select() }
            .decodeSingle<UsuarioDto>()

        val idUsuario = usuarioInserido.idUsuario!!

        // 3. Vincula ao perfil CLIENTE (assume que o perfil já foi criado via seed do SQL)
        val perfilCliente = client.postgrest["perfis"]
            .select(columns = Columns.list("id_perfil")) {
                filter { eq("nome", "CLIENTE") }
            }
            .decodeSingle<Map<String, Int>>()
        client.postgrest["usuario_perfil"].insert(
            UsuarioPerfilDto(idUsuario = idUsuario, idPerfil = perfilCliente["id_perfil"]!!)
        )

        // 4. Cria o registro em CLIENTES (relação 1:1 com USUARIOS)
        val clienteInserido = client.postgrest["clientes"]
            .insert(
                ClienteDto(
                    idUsuario = idUsuario,
                    cpf = cpf,
                    telefone = telefone,
                    dataNascimento = dataNascimento
                )
            ) { select() }
            .decodeSingle<ClienteDto>()

        clienteInserido.idCliente!!
    }

    /** Login: autentica no Supabase Auth e atualiza ultimo_acesso em USUARIOS */
    suspend fun login(email: String, senha: String): Result<Unit> = runCatching {
        client.auth.signInWith(Email) {
            this.email = email
            this.password = senha
        }

        client.postgrest["usuarios"]
            .update({ set("ultimo_acesso", "now()") }) {
                filter { eq("email", email) }
            }
    }

    /** Verifica se o usuário logado possui um determinado perfil (ex: "ADMIN", "ESTOQUISTA") */
    suspend fun possuiPerfil(email: String, nomePerfil: String): Boolean = runCatching {
        val usuario = client.postgrest["usuarios"]
            .select(columns = Columns.list("id_usuario")) {
                filter { eq("email", email) }
            }
            .decodeSingle<Map<String, Int>>()

        val perfil = client.postgrest["perfis"]
            .select(columns = Columns.list("id_perfil")) {
                filter { eq("nome", nomePerfil) }
            }
            .decodeSingle<Map<String, Int>>()

        val vinculo = client.postgrest["usuario_perfil"]
            .select {
                filter {
                    eq("id_usuario", usuario["id_usuario"]!!)
                    eq("id_perfil", perfil["id_perfil"]!!)
                }
            }
            .decodeList<Map<String, Int>>()

        vinculo.isNotEmpty()
    }.getOrDefault(false)

    /** Busca o id_cliente vinculado ao usuário logado, para usar nas telas de pedido/endereço */
    suspend fun getClienteIdDoUsuarioLogado(email: String): Int? = runCatching {
        val usuario = client.postgrest["usuarios"]
            .select(columns = Columns.list("id_usuario")) {
                filter { eq("email", email) }
            }
            .decodeSingle<Map<String, Int>>()

        val cliente = client.postgrest["clientes"]
            .select(columns = Columns.list("id_cliente")) {
                filter { eq("id_usuario", usuario["id_usuario"]!!) }
            }
            .decodeSingle<Map<String, Int>>()

        cliente["id_cliente"]
    }.getOrNull()
}
