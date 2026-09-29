package com.example.compraejovem.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.compraejovem.R
import com.example.compraejovem.data.repository.AuthRepository
import kotlinx.coroutines.launch

class CadastroUsuarioActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_usuario)

        val edtNome = findViewById<EditText>(R.id.edtCadastroNome)
        val edtEmail = findViewById<EditText>(R.id.edtCadastroEmail)
        val edtSenha = findViewById<EditText>(R.id.edtCadastroSenha)
        val edtCpf = findViewById<EditText>(R.id.edtCadastroCpf)
        val edtTelefone = findViewById<EditText>(R.id.edtCadastroTelefone)
        val edtNascimento = findViewById<EditText>(R.id.edtCadastroNascimento)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarCadastro)

        btnConfirmar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val email = edtEmail.text.toString().trim()
            val senha = edtSenha.text.toString()
            val cpf = edtCpf.text.toString().trim()
            val telefone = edtTelefone.text.toString().trim()
            val nascimento = edtNascimento.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || senha.length < 6) {
                Toast.makeText(this, "Preencha nome, e-mail e senha (mín. 6 caracteres)", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            btnConfirmar.isEnabled = false
            lifecycleScope.launch {
                AuthRepository.cadastrarCliente(
                    nome = nome,
                    email = email,
                    senha = senha,
                    cpf = cpf.ifEmpty { null },
                    telefone = telefone.ifEmpty { null },
                    dataNascimento = nascimento.ifEmpty { null }
                ).onSuccess {
                    Toast.makeText(this@CadastroUsuarioActivity, "Conta criada! Faça login.", Toast.LENGTH_LONG).show()
                    finish()
                }.onFailure {
                    btnConfirmar.isEnabled = true
                    Toast.makeText(
                        this@CadastroUsuarioActivity,
                        "Erro ao criar conta: ${it.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
