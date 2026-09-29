package com.example.compraejovem.ui.product

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.compraejovem.R
import com.example.compraejovem.data.MockData
import com.example.compraejovem.data.SupabaseClient
import com.example.compraejovem.model.Produto
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class CadastroProdutoActivity : AppCompatActivity() {

    private var base64FotoConvertida: String? = null
    private lateinit var imgPreview: ImageView
    private lateinit var edtUrlFoto: EditText

    private val selecionarFotoGaleria = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imgPreview.setImageURI(uri)
            edtUrlFoto.setText("")

            CoroutineScope(Dispatchers.IO).launch {
                val base64 = converterUriParaBase64(uri)
                withContext(Dispatchers.Main) {
                    base64FotoConvertida = base64
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro_produto)

        imgPreview = findViewById(R.id.imgPreviewCadastro)
        edtUrlFoto = findViewById(R.id.edtUrlFoto)
        val btnSelecionarFoto = findViewById<Button>(R.id.btnSelecionarFoto)
        val edtNome = findViewById<EditText>(R.id.edtNovoNome)
        val edtProfessor = findViewById<EditText>(R.id.edtNovoProfessor)
        val edtPreco = findViewById<EditText>(R.id.edtNovoPreco)
        val edtDescricao = findViewById<EditText>(R.id.edtNovaDescricao)
        val btnSalvar = findViewById<Button>(R.id.btnSalvarProduto)

        btnSelecionarFoto.setOnClickListener {
            selecionarFotoGaleria.launch("image/*")
        }

        btnSalvar.setOnClickListener {
            val nome = edtNome.text.toString().trim()
            val professor = edtProfessor.text.toString().trim()
            val precoText = edtPreco.text.toString().trim()
            val descricao = edtDescricao.text.toString().trim()
            val urlDigiting = edtUrlFoto.text.toString().trim()

            if (nome.isEmpty() || professor.isEmpty() || precoText.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val precoFormatado = precoText.replace(",", ".")
            val precoDouble = precoFormatado.toDoubleOrNull() ?: 0.0
            val imagemFinal = if (urlDigiting.isNotEmpty()) urlDigiting else (base64FotoConvertida ?: "")

            // Produto sem ID para o Supabase gerar automaticamente
            val novoProduto = Produto(
                nome = nome,
                descricao = descricao,
                preco = precoDouble,
                estoque = 10,
                imagem = imagemFinal,
                status = "ativo",
                professor = professor
            )

            btnSalvar.isEnabled = false
            btnSalvar.text = "Enviando ao Supabase..."

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Tenta gravar no Supabase
                    SupabaseClient.client.from("produtos").insert(novoProduto)

                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@CadastroProdutoActivity, "🎉 Produto salvo no Banco do Supabase!", Toast.LENGTH_LONG).show()
                        finish()
                    }
                } catch (e: Exception) {
                    // MOSTRA O ERRO REAL NA TELA PARA SABERMOS O MOTIVO
                    withContext(Dispatchers.Main) {
                        btnSalvar.isEnabled = true
                        btnSalvar.text = "Salvar e Publicar Produto"

                        AlertDialog.Builder(this@CadastroProdutoActivity)
                            .setTitle("❌ Erro ao salvar no Supabase")
                            .setMessage("O banco do professor recusou este produto.\n\nDetalhes do Erro:\n${e.localizedMessage ?: e.message}")
                            .setPositiveButton("Entendi", null)
                            .show()
                    }
                }
            }
        }
    }

    private fun converterUriParaBase64(uri: Uri): String? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            val originalBitmap = BitmapFactory.decodeStream(inputStream) ?: return null
            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, 400, 400, true)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val bytes = outputStream.toByteArray()
            Base64.encodeToString(bytes, Base64.NO_WRAP)
        } catch (e: Exception) {
            null
        }
    }
}