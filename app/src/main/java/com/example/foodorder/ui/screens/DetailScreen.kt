package com.example.foodorder.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FavoriteBorder
import androidx.compose.material.icons.rounded.ShoppingCart
import androidx.compose.material.icons.rounded.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodorder.data.Food
import com.example.foodorder.data.FoodRepository
import com.example.foodorder.data.toRupiah
import com.example.foodorder.ui.WidthClass
import com.example.foodorder.ui.components.BestSellerBadge
import com.example.foodorder.ui.components.EmptyState
import com.example.foodorder.ui.components.NoWindowInsets
import com.example.foodorder.ui.components.QuantityStepper
import com.example.foodorder.ui.components.categoryContainerColor
import com.example.foodorder.ui.rememberWidthClass
import com.example.foodorder.viewmodel.FoodViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(
    foodId: Int,
    vm: FoodViewModel,
    onBack: () -> Unit,
    onGoToCart: () -> Unit
) {
    val food = FoodRepository.byId(foodId)

    // State interaksi: jumlah pesanan, catatan, dan dialog konfirmasi
    var quantity by rememberSaveable { mutableIntStateOf(1) }
    var note by rememberSaveable { mutableStateOf("") }
    var showDialog by rememberSaveable { mutableStateOf(false) }
    val isWide = rememberWidthClass() != WidthClass.Compact

    Scaffold(
        contentWindowInsets = NoWindowInsets,
        topBar = {
            TopAppBar(
                title = { Text("Detail Menu") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (food != null) {
                        val isFavorite = vm.isFavorite(food.id)
                        IconButton(onClick = { vm.toggleFavorite(food.id) }) {
                            Icon(
                                imageVector = if (isFavorite) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = if (isFavorite) "Hapus dari favorit" else "Tambah ke favorit",
                                tint = if (isFavorite) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            )
        },
        bottomBar = {
            if (food != null) {
                Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Column {
                            Text(
                                "Total",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                (food.price * quantity).toRupiah(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = {
                                vm.addToCart(food, quantity, note)
                                showDialog = true
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) {
                            Icon(Icons.Rounded.ShoppingCart, contentDescription = null, modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Tambahkan")
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (food == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    emoji = "😕",
                    title = "Menu tidak ditemukan",
                    message = "Menu ini mungkin sudah tidak tersedia.",
                    actionLabel = "Kembali",
                    onAction = onBack
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isWide) {
                    // Layar lebar: foto di kiri, info di kanan
                    Row(
                        modifier = Modifier.widthIn(max = 960.dp),
                        horizontalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        DetailHero(food, Modifier.weight(1f).height(340.dp))
                        DetailInfo(
                            food = food,
                            quantity = quantity,
                            onQuantityChange = { quantity = it },
                            note = note,
                            onNoteChange = { note = it },
                            modifier = Modifier.weight(1f)
                        )
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        DetailHero(food, Modifier.fillMaxWidth().height(220.dp))
                        DetailInfo(
                            food = food,
                            quantity = quantity,
                            onQuantityChange = { quantity = it },
                            note = note,
                            onNoteChange = { note = it }
                        )
                    }
                }
            }
        }
    }

    // Dialog konfirmasi "Add to Cart"
    if (showDialog && food != null) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            icon = {
                Icon(
                    Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(40.dp)
                )
            },
            title = { Text("Berhasil Ditambahkan!") },
            text = { Text("$quantity× ${food.name} sudah masuk ke keranjang.") },
            confirmButton = {
                Button(onClick = {
                    showDialog = false
                    onGoToCart()
                }) { Text("Lihat Keranjang") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDialog = false
                    onBack()
                }) { Text("Lanjut Belanja") }
            }
        )
    }
}

@Composable
private fun DetailHero(food: Food, modifier: Modifier = Modifier) {
    val container = categoryContainerColor(food.category)
    Box(
        modifier = modifier
            .clip(MaterialTheme.shapes.extraLarge)
            .background(
                Brush.verticalGradient(
                    listOf(container, MaterialTheme.colorScheme.surfaceContainerHigh)
                )
            )
    ) {
        Text(food.emoji, fontSize = 120.sp, modifier = Modifier.align(Alignment.Center))
        if (food.isBestSeller) {
            BestSellerBadge(Modifier.align(Alignment.TopStart).padding(16.dp))
        }
    }
}

@Composable
private fun DetailInfo(
    food: Food,
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    note: String,
    onNoteChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(food.name, style = MaterialTheme.typography.headlineMedium)
            Text(
                food.price.toRupiah(),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(
                onClick = {},
                label = { Text("${food.rating}") },
                leadingIcon = {
                    Icon(
                        Icons.Rounded.Star,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(AssistChipDefaults.IconSize)
                    )
                }
            )
            AssistChip(onClick = {}, label = { Text("${food.minutes} mnt") })
            AssistChip(onClick = {}, label = { Text(food.category.label) })
        }

        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text("Tentang menu", style = MaterialTheme.typography.titleMedium)
            Text(
                food.description,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Jumlah", style = MaterialTheme.typography.titleMedium)
            QuantityStepper(
                quantity = quantity,
                onDecrease = { onQuantityChange(quantity - 1) },
                onIncrease = { onQuantityChange(quantity + 1) }
            )
        }

        OutlinedTextField(
            value = note,
            onValueChange = onNoteChange,
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Catatan untuk penjual (opsional)") },
            placeholder = { Text("Contoh: pedas sedang, tanpa bawang") },
            minLines = 2,
            shape = RoundedCornerShape(16.dp)
        )
    }
}
