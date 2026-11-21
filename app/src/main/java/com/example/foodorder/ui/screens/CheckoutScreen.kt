package com.example.foodorder.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.foodorder.data.Pricing
import com.example.foodorder.data.toRupiah
import com.example.foodorder.ui.components.CenteredBox
import com.example.foodorder.ui.components.EmptyState
import com.example.foodorder.ui.components.NoWindowInsets
import com.example.foodorder.ui.components.PriceRow
import com.example.foodorder.viewmodel.FoodViewModel

private enum class DeliveryMode(val label: String) {
    DELIVERY("🛵 Diantar"),
    PICKUP("🏪 Ambil sendiri")
}

private enum class PaymentMethod(val label: String, val hint: String) {
    TRANSFER("Transfer Bank", "BCA, BNI, Mandiri"),
    EWALLET("E-Wallet", "OVO, GoPay, DANA"),
    QRIS("QRIS", "Scan dari aplikasi pembayaran apa saja"),
    COD("Bayar di Tempat", "Tunai ke kurir saat pesanan tiba")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckoutScreen(
    vm: FoodViewModel,
    onBack: () -> Unit,
    onOrderPlaced: (String) -> Unit
) {
    // State form
    var name by rememberSaveable { mutableStateOf("") }
    var phone by rememberSaveable { mutableStateOf("") }
    var address by rememberSaveable { mutableStateOf("") }
    var courierNote by rememberSaveable { mutableStateOf("") }
    var deliveryMode by rememberSaveable { mutableStateOf(DeliveryMode.DELIVERY) }
    var payment by rememberSaveable { mutableStateOf(PaymentMethod.TRANSFER) }
    var submitted by rememberSaveable { mutableStateOf(false) }

    val cart = vm.cart
    val isDelivery = deliveryMode == DeliveryMode.DELIVERY
    val subtotal = vm.subtotal
    val fee = Pricing.deliveryFee(subtotal, isDelivery)
    val total = subtotal + fee

    // Validasi sederhana
    val nameError = if (name.isBlank()) "Nama penerima wajib diisi" else null
    val phoneError = if (phone.count { it.isDigit() } !in 9..14) "Nomor HP tidak valid (9–14 digit)" else null
    val addressError = if (isDelivery && address.isBlank()) "Alamat pengantaran wajib diisi" else null
    val hasError = nameError != null || phoneError != null || addressError != null

    Scaffold(
        modifier = Modifier.imePadding(),
        contentWindowInsets = NoWindowInsets,
        topBar = {
            TopAppBar(
                title = { Text("Checkout") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Kembali")
                    }
                }
            )
        },
        bottomBar = {
            if (cart.isNotEmpty()) {
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
                                "Total bayar",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                total.toRupiah(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        Button(
                            onClick = {
                                submitted = true
                                if (!hasError) onOrderPlaced(vm.placeOrder())
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(52.dp)
                        ) { Text("Buat Pesanan") }
                    }
                }
            }
        }
    ) { padding ->
        if (cart.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                EmptyState(
                    emoji = "🛒",
                    title = "Belum ada pesanan",
                    message = "Tambahkan menu ke keranjang dulu.",
                    actionLabel = "Kembali",
                    onAction = onBack
                )
            }
        } else {
            CenteredBox(padding) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    SectionCard("Cara menerima pesanan") {
                        SingleChoiceSegmentedButtonRow(Modifier.fillMaxWidth()) {
                            DeliveryMode.entries.forEachIndexed { index, mode ->
                                SegmentedButton(
                                    selected = deliveryMode == mode,
                                    onClick = { deliveryMode = mode },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = DeliveryMode.entries.size
                                    )
                                ) { Text(mode.label) }
                            }
                        }
                    }

                    SectionCard("Data penerima") {
                        FormField(
                            value = name,
                            onValueChange = { name = it },
                            label = "Nama penerima",
                            error = if (submitted) nameError else null
                        )
                        FormField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "Nomor HP",
                            error = if (submitted) phoneError else null,
                            keyboardType = KeyboardType.Phone
                        )
                        if (isDelivery) {
                            FormField(
                                value = address,
                                onValueChange = { address = it },
                                label = "Alamat lengkap",
                                error = if (submitted) addressError else null,
                                minLines = 2
                            )
                            FormField(
                                value = courierNote,
                                onValueChange = { courierNote = it },
                                label = "Catatan untuk kurir (opsional)",
                                error = null,
                                imeAction = ImeAction.Done
                            )
                        }
                    }

                    SectionCard("Metode pembayaran") {
                        PaymentMethod.entries.forEach { method ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .selectable(
                                        selected = payment == method,
                                        onClick = { payment = method },
                                        role = Role.RadioButton
                                    )
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = payment == method, onClick = null)
                                Spacer(Modifier.width(12.dp))
                                Column {
                                    Text(method.label, style = MaterialTheme.typography.bodyLarge)
                                    Text(
                                        method.hint,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    SectionCard("Ringkasan pesanan") {
                        cart.forEach { item ->
                            PriceRow(
                                label = "${item.quantity}× ${item.food.name}",
                                value = item.total.toRupiah()
                            )
                        }
                        HorizontalDivider()
                        PriceRow("Subtotal", subtotal.toRupiah())
                        PriceRow(
                            "Ongkir",
                            if (fee == 0) "Gratis" else fee.toRupiah(),
                            valueColor = if (fee == 0) MaterialTheme.colorScheme.primary
                            else androidx.compose.ui.graphics.Color.Unspecified
                        )
                        HorizontalDivider()
                        PriceRow("Total pembayaran", total.toRupiah(), emphasized = true)
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
private fun FormField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardType: KeyboardType = KeyboardType.Text,
    imeAction: ImeAction = ImeAction.Next,
    minLines: Int = 1
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        isError = error != null,
        supportingText = { if (error != null) Text(error) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType, imeAction = imeAction),
        singleLine = minLines == 1,
        minLines = minLines,
        shape = RoundedCornerShape(16.dp)
    )
}
