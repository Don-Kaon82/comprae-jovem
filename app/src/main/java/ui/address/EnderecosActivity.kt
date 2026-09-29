package com.example.compraejovem.ui.address

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compraejovem.R
import com.example.compraejovem.adapter.EnderecoAdapter
import com.example.compraejovem.data.remote.EnderecoDto
import com.example.compraejovem.data.repository.AuthRepository
import com.example.compraejovem.data.repository.EnderecoRepository
import com.example.compraejovem.utils.SessionManager
import kotlinx.coroutines.launch

class EnderecosActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private var idClienteAtual: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enderecos)

        recycler = findViewById(R.id.recyclerEnderecos)
        recycler.layoutManager = LinearLayoutManager(this)

        val edtCep = findViewById<EditText>(R.id.edtEnderecoCep)
        val edtLogradouro = findViewById<EditText>(R.id.edtEnderecoLogradouro)
        val edtNumero = findViewById<EditText>(R.id.edtEnderecoNumero)
        val edtBairro = findViewById<EditText>(R.id.edtEnderecoBairro)
        val edtCidade = findViewById<EditText>(R.id.edtEnderecoCidade)
        val edtEstado = findViewById<EditText>(R.id.edtEnderecoEstado)
        val btnAdicionar = findViewById<Button>(R.id.btnAdicionarEndereco)

        val email = SessionManager.emailLogado
        if (email == null) {
            Toast.makeText(this, "Faça login novamente", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        lifecycleScope.launch {
            idClienteAtual = AuthRepository.getClienteIdDoUsuarioLogado(email)
            carregarEnderecos()
        }

        btnAdicionar.setOnClickListener {
            val idCliente = idClienteAtual
            if (idCliente == null) {
                Toast.makeText(this, "Não foi possível identificar o cliente", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (edtLogradouro.text.isBlank() || edtCidade.text.isBlank()) {
                Toast.makeText(this, "Preencha ao menos logradouro e cidade", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                runCatching {
                    EnderecoRepository.cadastrar(
                        EnderecoDto(
                            idCliente = idCliente,
                            cep = edtCep.text.toString().ifBlank { null },
                            logradouro = edtLogradouro.text.toString(),
                            numero = edtNumero.text.toString().ifBlank { null },
                            bairro = edtBairro.text.toString().ifBlank { null },
                            cidade = edtCidade.text.toString(),
                            estado = edtEstado.text.toString().ifBlank { null }
                        )
                    )
                }.onSuccess {
                    edtCep.text.clear(); edtLogradouro.text.clear(); edtNumero.text.clear()
                    edtBairro.text.clear(); edtCidade.text.clear(); edtEstado.text.clear()
                    carregarEnderecos()
                }.onFailure {
                    Toast.makeText(this@EnderecosActivity, "Erro ao salvar: ${it.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun carregarEnderecos() {
        val idCliente = idClienteAtual ?: return
        lifecycleScope.launch {
            runCatching { EnderecoRepository.listarPorCliente(idCliente) }
                .onSuccess { lista ->
                    recycler.adapter = EnderecoAdapter(
                        lista,
                        onDefinirPrincipal = { endereco ->
                            lifecycleScope.launch {
                                EnderecoRepository.definirComoPrincipal(idCliente, endereco.idEndereco!!)
                                carregarEnderecos()
                            }
                        },
                        onExcluir = { endereco ->
                            lifecycleScope.launch {
                                EnderecoRepository.excluir(endereco.idEndereco!!)
                                carregarEnderecos()
                            }
                        }
                    )
                }
                .onFailure {
                    Toast.makeText(this@EnderecosActivity, "Erro ao carregar endereços: ${it.message}", Toast.LENGTH_LONG).show()
                }
        }
    }
}
