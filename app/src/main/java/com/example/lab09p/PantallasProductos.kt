package com.example.lab09p

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage

private fun precioFinal(p: ProductModel): Double =
    p.price * (1 - p.discountPercentage / 100)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TarjetaProducto(p: ProductModel, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = p.thumbnail,
                    contentDescription = p.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxWidth().height(140.dp)
                )
                if (p.discountPercentage >= 1) {
                    Text(
                        "-${p.discountPercentage.toInt()}%",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(8.dp)
                            .background(Color(0xFFE53935), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
            Column(Modifier.padding(10.dp)) {
                Text(
                    p.title,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Text(p.category, fontSize = 12.sp, color = Color.Gray)
                Text(
                    "$" + "%.2f".format(precioFinal(p)),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text("⭐ ${p.rating}", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun ListaProductos(vm: ProductViewModel, onVer: (Int) -> Unit) {
    LaunchedEffect(Unit) {
        if (vm.productos.isEmpty() && vm.busqueda.isBlank()) vm.buscar("")
    }

    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = vm.busqueda,
            onValueChange = { vm.buscar(it) },
            label = { Text("Buscar productos") },
            leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = "Buscar") },
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().padding(12.dp)
        )

        when {
            vm.cargando -> Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator()
            }
            vm.error != null -> Text(vm.error!!, Modifier.padding(16.dp))
            vm.productos.isEmpty() -> Text("Sin resultados", Modifier.padding(16.dp))
            else -> LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(vm.productos) { p -> TarjetaProducto(p) { onVer(p.id) } }
            }
        }
    }
}

@Composable
fun DetalleProducto(vm: ProductViewModel, id: Int) {
    LaunchedEffect(id) { vm.cargarProducto(id) }
    val p = vm.seleccionado

    when {
        vm.error != null -> Text(vm.error!!, Modifier.padding(16.dp))
        p == null -> Box(Modifier.fillMaxSize(), Alignment.Center) {
            CircularProgressIndicator()
        }
        else -> LazyColumn(
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(p.images) { url ->
                            AsyncImage(
                                model = url,
                                contentDescription = p.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .width(260.dp)
                                    .height(260.dp)
                                    .clip(RoundedCornerShape(20.dp))
                            )
                        }
                    }
                    Text(p.title, fontSize = 24.sp, fontWeight = FontWeight.Bold)
                    Text("${p.brand ?: "Sin marca"} • ${p.category}", color = Color.Gray)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "$" + "%.2f".format(precioFinal(p)),
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            "  $" + "%.2f".format(p.price),
                            textDecoration = TextDecoration.LineThrough,
                            color = Color.Gray
                        )
                        Text(
                            "  -${p.discountPercentage.toInt()}%",
                            color = Color(0xFFE53935),
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text("⭐ ${p.rating}   •   Stock: ${p.stock}")
                    Text(p.description)
                    Text("Reseñas", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                }
            }
            items(p.reviews ?: emptyList()) { r ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp)) {
                        Text("${r.reviewerName}  ⭐ ${r.rating}", fontWeight = FontWeight.Bold)
                        Text(r.comment)
                    }
                }
            }
        }
    }
}