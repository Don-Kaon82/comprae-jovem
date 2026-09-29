package com.example.compraejovem.ui.cart

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.compraejovem.R
import com.example.compraejovem.model.Pedido
import com.example.compraejovem.ui.auth.LoginActivity
import com.example.compraejovem.ui.home.HomeActivity
import com.example.compraejovem.utils.CarrinhoManager
import com.example.compraejovem.utils.PedidoManager
import com.example.compraejovem.utils.SessaoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class PagamentoActivity : AppCompatActivity() {

    private lateinit var edtCep: EditText
    private lateinit var edtRua: EditText
    private lateinit var edtNumero: EditText
    private lateinit var edtBairro: EditText
    private lateinit var edtCidade: EditText
    private lateinit var edtEstado: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pagamento)

        edtCep = findViewById(R.id.edtCep)
        edtRua = findViewById(R.id.edtRua)
        edtNumero = findViewById(R.id.edtNumero)
        edtBairro = findViewById(R.id.edtBairro)
        edtCidade = findViewById(R.id.edtCidade)
        edtEstado = findViewById(R.id.edtEstado)

        val btnBuscarCep = findViewById<Button>(R.id.btnBuscarCep)
        val btnConfirmar = findViewById<Button>(R.id.btnConfirmarPagamento)
        val txtTotal = findViewById<TextView>(R.id.txtTotalPagamento)
        val rgForma = findViewById<RadioGroup>(R.id.rgFormaPagamento)

        txtTotal.text = "Total: R$ ${String.format("%.2f", CarrinhoManager.getTotal())}"

        btnBuscarCep.setOnClickListener {
            val cep = edtCep.text.toString().replace("-", "").trim()
            if (cep.length == 8) {
                buscarEnderecoPorCep(cep)
            } else {
                Toast.makeText(this, "Digite um CEP válido!", Toast.LENGTH_SHORT).show()
            }
        }

        btnConfirmar.setOnClickListener {
            if (!SessaoManager.estaLogado) {
                Toast.makeText(this, "Faça login para finalizar a compra!", Toast.LENGTH_LONG).show()
                startActivity(Intent(this, LoginActivity::class.java))
                return@setOnClickListener
            }

            val rua = edtRua.text.toString().trim()
            val numero = edtNumero.text.toString().trim()
            val cidade = edtCidade.text.toString().trim()

            if (rua.isEmpty() || numero.isEmpty() || cidade.isEmpty()) {
                Toast.makeText(this, "Preencha o endereço de entrega!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val formaPagamento = when (rgForma.checkedRadioButtonId) {
                R.id.rbPix -> "Pix"
                R.id.rbCartao -> "Cartão de Crédito"
                else -> "Dinheiro na Entrega"
            }

            val resumoItens = CarrinhoManager.getItens().joinToString("\n") {
                "• ${it.produto.nome} (x${it.quantidade})"
            }

            val dataHoje = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())

            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_MONTH, 5)
            val dataPrevisao = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(calendar.time)

            val novoPedido = Pedido(
                id = "#${System.currentTimeMillis().toString().takeLast(5)}",
                dataHora = dataHoje,
                dataPrevistaEntrega = dataPrevisao,
                total = CarrinhoManager.getTotal(),
                formaPagamento = formaPagamento,
                enderecoEntrega = "$rua, Nº $numero - $cidade",
                status = "Em Separação 📦",
                resumoItens = resumoItens
            )

            PedidoManager.adicionarPedido(novoPedido)
            CarrinhoManager.limpar()

            AlertDialog.Builder(this)
                .setTitle("🎉 Pedido Confirmado!")
                .setMessage(
                    "Obrigado pela compra, ${SessaoManager.emailUsuario}!\n\n" +
                            "🚚 Previsão de Entrega: $dataPrevisao\n" +
                            "📍 Endereço: $rua, Nº $numero - $cidade\n\n" +
                            "Acompanhe o status no menu 'Meus Pedidos'."
                )
                .setCancelable(false)
                .setPositiveButton("Acompanhar Pedido") { _, _ ->
                    startActivity(Intent(this, MeusPedidosActivity::class.java))
                    finish()
                }
                .show()
        }
    }

    private fun buscarEnderecoPorCep(cep: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val jsonStr = URL("https://viacep.com.br/ws/$cep/json/").readText()
                val json = JSONObject(jsonStr)

                if (!json.has("erro")) {
                    withContext(Dispatchers.Main) {
                        edtRua.setText(json.optString("logradouro"))
                        edtBairro.setText(json.optString("bairro"))
                        edtCidade.setText(json.optString("localidade"))
                        edtEstado.setText(json.optString("uf"))
                        edtNumero.requestFocus()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@PagamentoActivity, "Erro ao buscar CEP!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}