package com.example.compraejovem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.compraejovem.R
import com.example.compraejovem.model.ItemCarrinho

class CarrinhoAdapter(
    private val itens: List<ItemCarrinho>,
    private val onAumentar: (ItemCarrinho) -> Unit,
    private val onDiminuir: (ItemCarrinho) -> Unit,
    private val onRemover: (ItemCarrinho) -> Unit
) : RecyclerView.Adapter<CarrinhoAdapter.CarrinhoViewHolder>() {

    class CarrinhoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNome: TextView = view.findViewById(R.id.txtNomeItemCarrinho)
        val txtPreco: TextView = view.findViewById(R.id.txtPrecoItemCarrinho)
        val txtQtd: TextView = view.findViewById(R.id.txtQtdCarrinho)
        val btnAumentar: Button = view.findViewById(R.id.btnAumentar)
        val btnDiminuir: Button = view.findViewById(R.id.btnDiminuir)
        val btnRemover: Button = view.findViewById(R.id.btnRemoverCarrinho)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarrinhoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_carrinho, parent, false)
        return CarrinhoViewHolder(view)
    }

    override fun onBindViewHolder(holder: CarrinhoViewHolder, position: Int) {
        val item = itens[position]
        holder.txtNome.text = item.produto.nome
        holder.txtPreco.text = "R$ ${String.format("%.2f", item.subtotal)}"
        holder.txtQtd.text = item.quantidade.toString()

        holder.btnAumentar.setOnClickListener { onAumentar(item) }
        holder.btnDiminuir.setOnClickListener { onDiminuir(item) }
        holder.btnRemover.setOnClickListener { onRemover(item) }
    }

    override fun getItemCount() = itens.size
}