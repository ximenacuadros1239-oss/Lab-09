package com.example.lab09p

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ProductViewModel : ViewModel() {

    private val api = Retrofit.Builder()
        .baseUrl("https://dummyjson.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(ProductApiService::class.java)

    var productos by mutableStateOf<List<ProductModel>>(emptyList())
        private set
    var seleccionado by mutableStateOf<ProductModel?>(null)
        private set
    var busqueda by mutableStateOf("")
        private set
    var cargando by mutableStateOf(false)
        private set
    var error by mutableStateOf<String?>(null)
        private set

    private var jobBusqueda: Job? = null

    fun buscar(texto: String) {
        busqueda = texto
        jobBusqueda?.cancel()
        jobBusqueda = viewModelScope.launch {
            if (texto.isNotBlank()) delay(400)   // espera a que el usuario termine de escribir
            cargando = true
            error = null
            try {
                productos = if (texto.isBlank()) api.getProducts().products
                else api.searchProducts(texto).products
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = "No se pudo cargar: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }

    fun cargarProducto(id: Int) {
        viewModelScope.launch {
            cargando = true
            error = null
            seleccionado = null
            try {
                seleccionado = api.getProductById(id)
            } catch (e: Exception) {
                error = "No se pudo cargar: ${e.message}"
            } finally {
                cargando = false
            }
        }
    }
}