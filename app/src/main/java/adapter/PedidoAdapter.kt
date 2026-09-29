package com.example.compraejovem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.compraejovem.R
import com.example.compraejovem.model.Pedido

class PedidoAdapter(private val pedidos: List<Pedido>) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    class PedidoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtId: TextView = view.findViewById(R.id.txtPedidoId)
        val txtStatus: TextView = view.findViewById(R.id.txtPedidoStatus)
        val txtData: TextView = view.findViewById(R.id.txtPedidoData)
        val txtPrevisao: TextView = view.findViewById(R.id.txtPedidoPrevisao)
        val txtItens: TextView = view.findViewById(R.id.txtPedidoItens)
        val txtEndereco: TextView = view.findViewById(R.id.txtPedidoEndereco)
        val txtTotal: TextView = view.findViewById(R.id.txtPedidoTotal)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]
        holder.txtId.text = "Pedido ${pedido.id}"
        holder.txtStatus.text = pedido.status
        holder.txtData.text = "Data da compra: ${pedido.dataHora}"
        holder.txtPrevisao.text = "🚚 Previsão de Chegada: ${pedido.dataPrevistaEntrega}"
        holder.txtItens.text = "Itens:\n${pedido.resumoItens}"
        holder.txtEndereco.text = "Entrega em: ${pedido.enderecoEntrega}"
        holder.txtTotal.text = "Total: R$ ${String.format("%.2f", pedido.total)} (${pedido.formaPagamento})"
    }

    override fun getItemCount() = pedidos.size
}