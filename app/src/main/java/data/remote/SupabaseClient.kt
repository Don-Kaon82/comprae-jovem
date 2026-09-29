package com.example.compraejovem.data.remote

import com.example.compraejovem.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest

/**
 * Instância única (singleton) do client do Supabase.
 * A URL e a chave anônima vêm do local.properties -> BuildConfig,
 * então NUNCA ficam hardcoded aqui no código-fonte.
 */
object SupabaseClientProvider {

    val client = createSupabaseClient(
        supabaseUrl = BuildConfig.SUPABASE_URL,
        supabaseKey = BuildConfig.SUPABASE_ANON_KEY
    ) {
        install(Postgrest)
        install(Auth)
    }
}
