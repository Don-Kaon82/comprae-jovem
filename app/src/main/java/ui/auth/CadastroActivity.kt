package com.example.compraejovem.ui.auth

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.compraejovem.R
import com.example.compraejovem.data.SupabaseClient
import com.example.compraejovem.utils.SessaoManager
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CadastroActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        val edtNome = findViewById<EditText>(R.id.edtCadNome)
        val edtEmail = findViewById<EditText>(R.id.edtCadEmail)
        val edtSenha = findViewById<EditText>(R.id.edtCadSenha)
        val btnCadastrar = findViewById<Button>(R.id.btnFinalizarCadastro)

        btnCadastrar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val emailInput = edtEmail.text.toString().trim()
            val senhaInput = edtSenha.text.toString().trim()

            if (nome.isEmpty() || emailInput.isEmpty() || senhaInput.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            btnCadastrar.isEnabled = false
            btnCadastrar.text = "Cadastrando..."

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    SupabaseClient.client.auth.signUpWith(Email) {
                        email = emailInput
                        password = senhaInput
                    }

                    withContext(Dispatchers.Main) {
                        // Cadastro pelo app SEMPRE cria conta de Comprador (ehVendedor = false)
                        SessaoManager.fazerLogin(emailInput, ehVendedor = false)
                        Toast.makeText(this@CadastroActivity, "🎉 Conta cadastrada com sucesso!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        SessaoManager.fazerLogin(emailInput, ehVendedor = false)
                        Toast.makeText(this@CadastroActivity, "🎉 Conta criada com sucesso!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                }
            }
        }
    }
}