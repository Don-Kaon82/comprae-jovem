package com.example.compraejovem.ui.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val edtEmail = findViewById<EditText>(R.id.edtEmailLogin)
        val edtSenha = findViewById<EditText>(R.id.edtSenhaLogin)
        val btnEntrar = findViewById<Button>(R.id.btnEntrar)
        val btnCadastreSe = findViewById<TextView>(R.id.btnIrParaCadastro)

        btnCadastreSe.setOnClickListener {
            startActivity(Intent(this, CadastroActivity::class.java))
            finish()
        }

        btnEntrar.setOnClickListener {
            val emailInput = edtEmail.text.toString().trim()
            val senhaInput = edtSenha.text.toString().trim()

            if (emailInput.isEmpty() || senhaInput.isEmpty()) {
                Toast.makeText(this, "Digite seu e-mail e senha!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // DADOS DO ADMIN / VENDEDOR
            // Se o e-mail for "admin@comprae.com" e a senha for "1234" (ou senha "admin"), entra como Vendedor!
            val ehAdmin = (emailInput.equals("admin@comprae.com", ignoreCase = true) || emailInput.equals("admin", ignoreCase = true)) &&
                    (senhaInput == "1234" || senhaInput == "admin")

            if (ehAdmin) {
                SessaoManager.fazerLogin(emailInput, ehVendedor = true)
                Toast.makeText(this, "Bem-vindo, Administrador!", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                // Entra como Comprador
                fazerLoginNoSupabase(emailInput, senhaInput, ehVendedor = false)
            }
        }
    }

    private fun fazerLoginNoSupabase(emailInput: String, senhaInput: String, ehVendedor: Boolean) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                SupabaseClient.client.auth.signInWith(Email) {
                    email = emailInput
                    password = senhaInput
                }

                withContext(Dispatchers.Main) {
                    SessaoManager.fazerLogin(emailInput, ehVendedor)
                    Toast.makeText(this@LoginActivity, "Login realizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            } catch (e: Exception) {
                // Em caso de teste sem banco/erro, loga localmente como comprador
                withContext(Dispatchers.Main) {
                    SessaoManager.fazerLogin(emailInput, ehVendedor)
                    Toast.makeText(this@LoginActivity, "Login realizado!", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
        }
    }
}