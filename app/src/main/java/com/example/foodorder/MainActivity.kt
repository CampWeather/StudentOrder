package com.example.foodorder

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodorder.ui.FoodOrderApp
import com.example.foodorder.ui.theme.FoodOrderTheme
import com.example.foodorder.ui.theme.ThemeMode
import com.example.foodorder.viewmodel.FoodViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Pilihan tema disimpan di SharedPreferences supaya tetap ada setelah aplikasi ditutup.
        val prefs = getSharedPreferences("foodorder_prefs", MODE_PRIVATE)

        setContent {
            var themeMode by rememberSaveable {
                mutableStateOf(
                    runCatching { ThemeMode.valueOf(prefs.getString("theme_mode", null) ?: "") }
                        .getOrDefault(ThemeMode.SYSTEM)
                )
            }
            var dynamicColor by rememberSaveable {
                mutableStateOf(prefs.getBoolean("dynamic_color", false))
            }
            val vm: FoodViewModel = viewModel()

            FoodOrderTheme(themeMode = themeMode, dynamicColor = dynamicColor) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FoodOrderApp(
                        vm = vm,
                        themeMode = themeMode,
                        onThemeModeChange = {
                            themeMode = it
                            prefs.edit().putString("theme_mode", it.name).apply()
                        },
                        dynamicColor = dynamicColor,
                        onDynamicColorChange = {
                            dynamicColor = it
                            prefs.edit().putBoolean("dynamic_color", it).apply()
                        }
                    )
                }
            }
        }
    }
}
