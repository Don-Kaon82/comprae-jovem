package com.example.compraejovem.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.compraejovem.R
import com.example.compraejovem.data.remote.EnderecoDto

class EnderecoAdapter(
    private val enderecos: List<EnderecoDto>,
    private val onDefinirPrincipal: (EnderecoDto) -> Unit,
    private val onExcluir: (EnderecoDto) -> Unit
) : RecyclerView.Adapter<EnderecoAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtResumo: TextView = view.findViewById(R.id.txtEnderecoResumo)
        val txtPrincipal: TextView = view.findViewById(R.id.txtEnderecoPrincipal)
        val btnDefinirPrincipal: Button = view.findViewById(R.id.btnDefinirPrincipal)
        val btnExcluir: Button = view.findViewById(R.id.btnExcluirEndereco)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_endereco, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val endereco = enderecos[position]
        holder.txtResumo.text =
            "${endereco.logradouro ?: ""}, ${endereco.numero ?: ""} - ${endereco.bairro ?: ""}, ${endereco.cidade ?: ""}/${endereco.estado ?: ""}"
        holder.txtPrincipal.visibility = if (endereco.principal) View.VISIBLE else View.GONE
        holder.btnDefinirPrincipal.setOnClickListener { onDefinirPrincipal(endereco) }
        holder.btnExcluir.setOnClickListener { onExcluir(endereco) }
    }

    override fun getItemCount() = enderecos.size
}
