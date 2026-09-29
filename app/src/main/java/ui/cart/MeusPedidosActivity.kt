package com.example.compraejovem.ui.cart

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compraejovem.R
import com.example.compraejovem.adapter.PedidoAdapter
import com.example.compraejovem.utils.PedidoManager

class MeusPedidosActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meus_pedidos)

        val recycler = findViewById<RecyclerView>(R.id.recyclerMeusPedidos)
        val txtSemPedidos = findViewById<TextView>(R.id.txtSemPedidos)
        val pedidos = PedidoManager.getPedidos()

        if (pedidos.isEmpty()) {
            txtSemPedidos.visibility = View.VISIBLE
            recycler.visibility = View.GONE
        } else {
            txtSemPedidos.visibility = View.GONE
            recycler.visibility = View.VISIBLE
            recycler.layoutManager = LinearLayoutManager(this)
            recycler.adapter = PedidoAdapter(pedidos)
        }
    }
}