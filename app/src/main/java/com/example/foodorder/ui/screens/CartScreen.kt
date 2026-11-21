package com.example.foodorder.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.foodorder.data.Pricing
import com.example.foodorder.data.toRupiah
import com.example.foodorder.ui.components.CenteredBox
import com.example.foodorder.ui.components.EmptyState
import com.example.foodorder.ui.components.FoodTile
import com.example.foodorder.ui.components.NoWindowInsets
import com.example.foodorder.ui.components.PriceRow
import com.example.foodorder.ui.components.QuantityStepper
import com.example.foodorder.viewmodel.CartItem
import com.example.foodorder.viewmodel.FoodViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CartScreen(
    vm: FoodViewModel,
    snackbarHostState: SnackbarHostState,
    onCheckout: () -> Unit,
    onBrowse: () -> Unit
) {
    val cart = vm.cart
    val scope = rememberCoroutineScope()

    val onDelete: (CartItem) -> Unit = { item ->
        val index = cart.indexOfFirst { it.food.id == item.food.id }
        vm.removeFromCart(item.food.id)
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = "${item.food.name} dihapus",
                actionLabel = "Urungkan"
            )
            if (result == SnackbarResult.ActionPerformed) vm.restoreToCart(item, index)
        }
    }

    Scaffold(
        contentWindowInsets = NoWindowInsets,
        topBar = { CenterAlignedTopAppBar(title = { Text("Keranjang", style = MaterialTheme.typography.titleLarge) }) }
    ) { padding ->
        if (cart.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                EmptyState(
                    emoji = "🛒",
                    title = "Keranjangmu masih kosong",
                    message = "Yuk pilih menu favoritmu dulu.",
                    actionLabel = "Lihat menu",
                    onAction = onBrowse
                )
            }
        } else {
            CenteredBox(padding) {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(cart, key = { it.food.id }) { item ->
                        CartItemCard(
                            item = item,
                            onDecrease = { vm.changeQuantity(item.food.id, -1) },
                            onIncrease = { vm.changeQuantity(item.food.id, +1) },
                            onDelete = { onDelete(item) },
                            modifier = Modifier.animateItem()
                        )
                    }
                }
                SummaryPanel(subtotal = vm.subtotal, onCheckout = onCheckout)
            }
        }
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                FoodTile(food = item.food, size = 64.dp)
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Text(
                        item.food.name,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        "${item.food.price.toRupiah()} / porsi",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (item.note.isNotBlank()) {
                        Text(
                            "Catatan: ${item.note}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.secondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Rounded.Delete,
                        contentDescription = "Hapus ${item.food.name}",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    item.total.toRupiah(),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                QuantityStepper(
                    quantity = item.quantity,
                    onDecrease = onDecrease,
                    onIncrease = onIncrease,
                    maxQuantity = Pricing.MAX_QUANTITY
                )
            }
        }
    }
}

@Composable
private fun SummaryPanel(subtotal: Int, onCheckout: () -> Unit) {
    val fee = Pricing.deliveryFee(subtotal, isDelivery = true)
    Surface(
        tonalElevation = 3.dp,
        shadowElevation = 8.dp,
        shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FreeDeliveryProgress(subtotal)
            PriceRow("Subtotal", subtotal.toRupiah())
            PriceRow(
                "Ongkir",
                if (fee == 0) "Gratis" else fee.toRupiah(),
                valueColor = if (fee == 0) MaterialTheme.colorScheme.primary
                else androidx.compose.ui.graphics.Color.Unspecified
            )
            HorizontalDivider()
            PriceRow("Total", (subtotal + fee).toRupiah(), emphasized = true)
            Button(
                onClick = onCheckout,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) { Text("Checkout") }
        }
    }
}

@Composable
private fun FreeDeliveryProgress(subtotal: Int) {
    val remaining = (Pricing.FREE_DELIVERY_MIN - subtotal).coerceAtLeast(0)
    val progress by animateFloatAsState(
        targetValue = (subtotal.toFloat() / Pricing.FREE_DELIVERY_MIN).coerceIn(0f, 1f),
        label = "freeDeliveryProgress"
    )
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = if (remaining == 0) "🎉 Ongkir gratis untuk pesananmu!"
            else "Tambah ${remaining.toRupiah()} lagi untuk gratis ongkir",
            style = MaterialTheme.typography.bodyMedium
        )
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(50))
        )
    }
}
