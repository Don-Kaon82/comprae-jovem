package com.example.compraejovem.ui.home

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.compraejovem.R
import com.example.compraejovem.adapter.ProdutoAdapter
import com.example.compraejovem.data.MockData
import com.example.compraejovem.data.SupabaseClient
import com.example.compraejovem.model.Produto
import com.example.compraejovem.ui.auth.LoginActivity
import com.example.compraejovem.ui.cart.CarrinhoActivity
import com.example.compraejovem.ui.cart.MeusPedidosActivity
import com.example.compraejovem.ui.product.CadastroProdutoActivity
import com.example.compraejovem.ui.product.DetalheProdutoActivity
import com.example.compraejovem.utils.CarrinhoManager
import com.example.compraejovem.utils.SessaoManager
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeActivity : AppCompatActivity() {

    private lateinit var recycler: RecyclerView
    private lateinit var txtCarrinho: TextView
    private lateinit var edtBusca: EditText
    private lateinit var btnNovoProduto: Button
    private lateinit var txtStatusUsuario: TextView
    private lateinit var btnAuthAcao: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val rootLayout = findViewById<View>(R.id.rootHomeLayout)
        if (rootLayout != null) {
            ViewCompat.setOnApplyWindowInsetsListener(rootLayout) { view, insets ->
                val statusBarHeight = insets.getInsets(WindowInsetsCompat.Type.statusBars()).top
                view.setPadding(0, statusBarHeight, 0, 0)
                insets
            }
        }

        recycler = findViewById(R.id.recyclerProdutos)
        txtCarrinho = findViewById(R.id.txtCarrinho)
        edtBusca = findViewById(R.id.edtBusca)
        btnNovoProduto = findViewById(R.id.btnNovoProdutoHome)
        txtStatusUsuario = findViewById(R.id.txtStatusUsuario)
        btnAuthAcao = findViewById(R.id.btnAuthAcao)

        txtCarrinho.setOnClickListener {
            startActivity(Intent(this, CarrinhoActivity::class.java))
        }

        txtStatusUsuario.setOnClickListener {
            if (SessaoManager.estaLogado) {
                startActivity(Intent(this, MeusPedidosActivity::class.java))
            }
        }

        btnAuthAcao.setOnClickListener {
            if (SessaoManager.estaLogado) {
                SessaoManager.fazerLogout()
                Toast.makeText(this, "Você saiu da conta.", Toast.LENGTH_SHORT).show()
                atualizarInterfaceUsuario()
            } else {
                startActivity(Intent(this, LoginActivity::class.java))
            }
        }

        edtBusca.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val filtro = s.toString().lowercase()
                val filtrados = MockData.produtos.filter {
                    it.nome.lowercase().contains(filtro) ||
                            it.professor.lowercase().contains(filtro) ||
                            (it.descricao?.lowercase()?.contains(filtro) == true)
                }
                configurarRecycler(filtrados)
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onResume() {
        super.onResume()
        atualizarBadgeCarrinho()
        atualizarInterfaceUsuario()
        carregarProdutosDoSupabase()
    }

    private fun carregarProdutosDoSupabase() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val produtosBanco = SupabaseClient.client.from("produtos")
                    .select()
                    .decodeList<Produto>()

                if (produtosBanco.isNotEmpty()) {
                    MockData.produtos.clear()
                    MockData.produtos.addAll(produtosBanco)
                }

                withContext(Dispatchers.Main) {
                    configurarRecycler(MockData.produtos)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    configurarRecycler(MockData.produtos)
                }
            }
        }
    }

    private fun atualizarInterfaceUsuario() {
        if (SessaoManager.estaLogado) {
            val perfil = if (SessaoManager.eVendedor) "Loja" else "Aluno"
            txtStatusUsuario.text = "👤 ${SessaoManager.emailUsuario} ($perfil)"
            btnAuthAcao.text = "Sair"

            if (SessaoManager.eVendedor) {
                btnNovoProduto.visibility = View.VISIBLE
                btnNovoProduto.setOnClickListener {
                    startActivity(Intent(this, CadastroProdutoActivity::class.java))
                }
            } else {
                btnNovoProduto.visibility = View.GONE
            }
        } else {
            txtStatusUsuario.text = "👤 Visitante (Não logado)"
            btnAuthAcao.text = "Entrar"
            btnNovoProduto.visibility = View.GONE
        }
    }

    private fun configurarRecycler(produtos: List<Produto>) {
        recycler.layoutManager = GridLayoutManager(this, 2)
        recycler.adapter = ProdutoAdapter(produtos) { produto ->
            val intent = Intent(this, DetalheProdutoActivity::class.java)
            intent.putExtra("produto_id", produto.id ?: 0)
            startActivity(intent)
        }
    }

    private fun atualizarBadgeCarrinho() {
        val qtd = CarrinhoManager.getQuantidadeTotal()
        txtCarrinho.text = "🛒 ($qtd)"
    }
}