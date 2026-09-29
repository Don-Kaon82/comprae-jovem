package com.example.compraejovem.ui.product

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.compraejovem.R
import com.example.compraejovem.data.MockData
import com.example.compraejovem.utils.CarrinhoManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.URL

class DetalheProdutoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhe_produto)

        val produtoId = intent.getIntExtra("produto_id", -1)
        val produto = MockData.produtos.find { it.id == produtoId }

        if (produto != null) {
            val imgDetalhe = findViewById<ImageView>(R.id.imgDetalhe)

            carregarImagemUniversal(imgDetalhe, produto.imagem)

            findViewById<TextView>(R.id.txtDetalheNome).text = produto.nome
            findViewById<TextView>(R.id.txtDetalheProfessor).text = "Vendido por: ${produto.professor}"
            findViewById<TextView>(R.id.txtDetalhePreco).text = "R$ ${String.format("%.2f", produto.preco)}"
            findViewById<TextView>(R.id.txtDetalheDescricao).text = produto.descricao ?: "Sem descrição"

            val btnAdicionar = findViewById<Button>(R.id.btnAdicionarCarrinho)
            btnAdicionar.setOnClickListener {
                CarrinhoManager.adicionar(produto)
                Toast.makeText(this, "${produto.nome} adicionado ao carrinho!", Toast.LENGTH_SHORT).show()
                finish()
            }
        } else {
            Toast.makeText(this, "Produto não encontrado!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun carregarImagemUniversal(imageView: ImageView, imagemStr: String?) {
        if (imagemStr.isNullOrEmpty()) {
            imageView.setImageResource(R.drawable.ic_placeholder)
            return
        }

        if (imagemStr.startsWith("http://") || imagemStr.startsWith("https://")) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val stream = URL(imagemStr).openStream()
                    val bitmap = BitmapFactory.decodeStream(stream)
                    withContext(Dispatchers.Main) {
                        imageView.setImageBitmap(bitmap)
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