package com.example.compraejovem.ui.cart

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compraejovem.R
import com.example.compraejovem.adapter.CarrinhoAdapter
import com.example.compraejovem.utils.CarrinhoManager

class CarrinhoActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var txtTotal: TextView
    private lateinit var txtVazio: TextView
    private lateinit var btnFinalizar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrinho)

        recycler = findViewById(R.id.recyclerCarrinho)
        txtTotal = findViewById(R.id.txtTotalCarrinho)
        txtVazio = findViewById(R.id.txtCarrinhoVazio)
        btnFinalizar = findViewById(R.id.btnFinalizarCompra)

        atualizarTela()

        btnFinalizar.setOnClickListener {
            if (CarrinhoManager.getItens().isEmpty()) {
                Toast.makeText(this, "Seu carrinho está vazio!", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this, PagamentoActivity::class.java))
            }
        }
    }

    override fun onResume() {
        super.onResume()
        atualizarTela()
    }

    private fun atualizarTela() {
        val itens = CarrinhoManager.getItens()

        if (itens.isEmpty()) {
            txtVazio.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            txtVazio.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = CarrinhoAdapter(
                itens = itens,
                onAumentar = { item ->
                    CarrinhoManager.aumentarQuantidade(item.produto.id ?: 0)
                    atualizarTela()
                },
                onDiminuir = { item ->
                    CarrinhoManager.diminuirQuantidade(item.produto.id ?: 0)
                    atualizarTela()
                },
                onRemover = { item ->
                    CarrinhoManager.remover(item.produto.id ?: 0)
                    atualizarTela()
                }
            )
        }

        txtTotal.text = "Total: R$ ${String.format("%.2f", CarrinhoManager.getTotal())}"
    }
}