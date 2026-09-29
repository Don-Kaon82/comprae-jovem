package com.example.compraejovem.adapter

import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.compraejovem.R
import com.example.compraejovem.model.Produto
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class ProdutoAdapter(
    private val produtos: List<Produto>,
    private val onItemClick: (Produto) -> Unit
) : RecyclerView.Adapter<ProdutoAdapter.ProdutoViewHolder>() {

    class ProdutoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgProduto: ImageView = view.findViewById(R.id.imgProduto)
        val txtNome: TextView = view.findViewById(R.id.txtNomeProduto)
        val txtPreco: TextView = view.findViewById(R.id.txtPrecoProduto)
        val txtProfessor: TextView = view.findViewById(R.id.txtProfessor)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProdutoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_produto, parent, false)
        return ProdutoViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProdutoViewHolder, position: Int) {
        val produto = produtos[position]

        carregarImagemUniversal(holder.imgProduto, produto.imagem)

        holder.txtNome.text = produto.nome
        holder.txtPreco.text = "R$ ${String.format("%.2f", produto.preco)}"
        holder.txtProfessor.text = produto.professor

        holder.itemView.setOnClickListener { onItemClick(produto) }
    }

    override fun getItemCount() = produtos.size

    private fun carregarImagemUniversal(imageView: ImageView, imagemStr: String?) {
        if (imagemStr.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.ic_placeholder)
            return
        }

        if (imagemStr.startsWith("http://") || imagemStr.startsWith("https://")) {
            val tagUrl = imagemStr
            imageView.tag = tagUrl

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val stream = URL(imagemStr).openStream()
                    val bitmap = BitmapFactory.decodeStream(stream)
                    withContext(Dispatchers.Main) {
                        if (imageView.tag == tagUrl) {
                            imageView.setImageBitmap(bitmap)
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        imageView.setImageResource(R.drawable.ic_placeholder)
                    }
                }
            }
            return
        }

        try {
            val imageBytes = Base64.decode(imagemStr, Base64.DEFAULT)
            val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
            if (bitmap != null) {
                imageView.setImageBitmap(bitmap)
                return
            }
        } catch (e: Exception) {}

        try {
            imageView.setImageURI(Uri.parse(imagemStr))
        } catch (e: Exception) {
            imageView.setImageResource(R.drawable.ic_placeholder)
        }
    }
}