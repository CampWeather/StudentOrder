package com.example.foodorder.ui.screens

import android.os.Build
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.DarkMode
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.SettingsBrightness
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.foodorder.ui.components.CenteredBox
import com.example.foodorder.ui.components.NoWindowInsets
import com.example.foodorder.ui.theme.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeScreen(
    themeMode: ThemeMode,
    onThemeModeChange: (ThemeMode) -> Unit,
    dynamicColor: Boolean,
    onDynamicColorChange: (Boolean) -> Unit
) {
    val dynamicSupported = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    Scaffold(
        contentWindowInsets = NoWindowInsets,
        topBar = { CenterAlignedTopAppBar(title = { Text("Tema Aplikasi", style = MaterialTheme.typography.titleLarge) }) }
    ) { padding ->
        CenteredBox(padding) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("Mode tampilan", style = MaterialTheme.typography.titleMedium)

                ThemeMode.entries.forEach { mode ->
                    val (title, desc, icon) = when (mode) {
                        ThemeMode.LIGHT -> Triple("Light Mode", "Tampilan terang", Icons.Rounded.LightMode)
                        ThemeMode.DARK -> Triple("Dark Mode", "Tampilan gelap, hemat baterai", Icons.Rounded.DarkMode)
                        ThemeMode.SYSTEM -> Triple("Ikuti Sistem", "Menyesuaikan pengaturan perangkat", Icons.Rounded.SettingsBrightness)
                    }
                    ThemeModeCard(
                        title = title,
                        description = desc,
                        icon = icon,
                        selected = mode == themeMode,
                        onClick = { onThemeModeChange(mode) }
                    )
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    ListItem(
                        headlineContent = { Text("Warna dinamis") },
                        supportingContent = {
                            Text(
                                if (dynamicSupported) "Ambil warna dari wallpaper (Material You)"
                                else "Butuh Android 12 ke atas"
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = dynamicColor && dynamicSupported,
                                onCheckedChange = onDynamicColorChange,
                                enabled = dynamicSupported
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                    )
                }

                Text(
                    "Palet warna",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    ColorSwatch(MaterialTheme.colorScheme.primary, "Primary")
                    ColorSwatch(MaterialTheme.colorScheme.secondary, "Secondary")
                    ColorSwatch(MaterialTheme.colorScheme.tertiary, "Tertiary")
                    ColorSwatch(MaterialTheme.colorScheme.primaryContainer, "Container")
                    ColorSwatch(MaterialTheme.colorScheme.surfaceContainerHigh, "Surface")
                }

                Text(
                    "Skala tipografi",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text("Headline Small", style = MaterialTheme.typography.headlineSmall)
                        Text("Title Medium", style = MaterialTheme.typography.titleMedium)
                        Text("Body Large — teks utama untuk deskripsi menu.", style = MaterialTheme.typography.bodyLarge)
                        Text("Label Large", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemeModeCard(
    title: String,
    description: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer
            else MaterialTheme.colorScheme.surfaceContainerLow
        ),
        border = if (selected) BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(icon, contentDescription = null)
            Column(Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(
                    description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            RadioButton(selected = selected, onClick = null)
        }
    }
}

@Composable
private fun ColorSwatch(color: Color, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .background(color, CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, CircleShape)
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}
