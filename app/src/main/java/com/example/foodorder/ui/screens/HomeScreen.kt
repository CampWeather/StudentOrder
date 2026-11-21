package com.example.foodorder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodorder.data.Food
import com.example.foodorder.data.FoodCategory
import com.example.foodorder.data.FoodRepository
import com.example.foodorder.data.Pricing
import com.example.foodorder.data.toRupiah
import com.example.foodorder.ui.WidthClass
import com.example.foodorder.ui.components.BestSellerBadge
import com.example.foodorder.ui.components.EmptyState
import com.example.foodorder.ui.components.FoodTile
import com.example.foodorder.ui.components.NoWindowInsets
import com.example.foodorder.ui.rememberWidthClass
import com.example.foodorder.viewmodel.FoodViewModel
import kotlinx.coroutines.launch

private data class CategoryFilter(val key: String, val emoji: String)

private val categoryFilters = listOf(
    CategoryFilter("Semua", "✨"),
    CategoryFilter(FoodCategory.MAKANAN.label, FoodCategory.MAKANAN.emoji),
    CategoryFilter(FoodCategory.MINUMAN.label, FoodCategory.MINUMAN.emoji),
    CategoryFilter(FoodCategory.CAMILAN.label, FoodCategory.CAMILAN.emoji),
    CategoryFilter("Favorit", "❤️")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    vm: FoodViewModel,
    snackbarHostState: SnackbarHostState,
    onFoodClick: (Int) -> Unit,
    onGoToCart: () -> Unit
) {
    // State UI: tetap tersimpan saat layar diputar (rememberSaveable)
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("Semua") }
    val scope = rememberCoroutineScope()

    // Layout adaptif: 1 kolom di HP, 2-3 kolom di layar lebar
    val columns = when (rememberWidthClass()) {
        WidthClass.Compact -> 1
        WidthClass.Medium -> 2
        WidthClass.Expanded -> 3
    }

    val visibleFoods = FoodRepository.foods.filter { food ->
        val matchQuery = query.isBlank() || food.name.contains(query.trim(), ignoreCase = true)
        val matchFilter = when (filter) {
            "Semua" -> true
            "Favorit" -> vm.isFavorite(food.id)
            else -> food.category.label == filter
        }
        matchQuery && matchFilter
    }
    val rows = visibleFoods.chunked(columns)

    val onQuickAdd: (Food) -> Unit = { food ->
        vm.addToCart(food)
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = "${food.name} masuk keranjang",
                actionLabel = "Lihat",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) onGoToCart()
        }
    }

    Scaffold(
        contentWindowInsets = NoWindowInsets,
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            Icons.Rounded.Restaurant,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Text("FoodOrder", style = MaterialTheme.typography.titleLarge)
                    }
                }
            )
        }
    ) { padding ->
        // LazyColumn = daftar makanan yang efisien (hanya item yang terlihat yang digambar)
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { PromoBanner() }

            item {
                OutlinedTextField(
                    value = query,
                    onValueChange = { query = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Cari menu favoritmu…") },
                    leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { query = "" }) {
                                Icon(Icons.Rounded.Close, contentDescription = "Hapus pencarian")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp)
                )
            }

            item {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(categoryFilters) { item ->
                        FilterChip(
                            selected = filter == item.key,
                            onClick = { filter = item.key },
                            label = { Text(item.key) },
                            leadingIcon = { Text(item.emoji) }
                        )
                    }
                }
            }

            item {
                Text(
                    text = "Menu hari ini (${visibleFoods.size})",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            if (rows.isEmpty()) {
                item {
                    EmptyState(
                        emoji = "🔍",
                        title = "Menu tidak ditemukan",
                        message = if (filter == "Favorit") "Tandai menu dengan ikon hati di halaman detail."
                        else "Coba kata kunci atau kategori lain."
                    )
                }
            }

            items(rows, key = { row -> row.first().id }) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(IntrinsicSize.Max),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    row.forEach { food ->
                        FoodCard(
                            food = food,
                            onClick = { onFoodClick(food.id) },
                            onAdd = { onQuickAdd(food) },
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                        )
                    }
                    // Isi sisa kolom agar lebar kartu terakhir tetap sama
                    repeat(columns - row.size) { Spacer(Modifier.weight(1f)) }
                }
            }
        }
    }
}

@Composable
private fun PromoBanner() {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text("Mau makan apa hari ini?", style = MaterialTheme.typography.headlineSmall)
                Text(
                    "Gratis ongkir untuk pesanan di atas ${Pricing.FREE_DELIVERY_MIN.toRupiah()}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Text("🍽️", fontSize = 48.sp, modifier = Modifier.padding(start = 12.dp))
        }
    }
}

@Composable
private fun FoodCard(
    food: Food,
    onClick: () -> Unit,
    onAdd: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FoodTile(food = food, size = 80.dp)

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                if (food.isBestSeller) BestSellerBadge()
                Text(
                    food.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    food.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        food.price.toRupiah(),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        "${food.rating}",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }

            FilledIconButton(onClick = onAdd) {
                Icon(Icons.Rounded.Add, contentDescription = "Tambah ${food.name} ke keranjang")
            }
        }
    }
}
