package com.example.foodorder.data

import java.text.NumberFormat
import java.util.Locale

enum class FoodCategory(val label: String, val emoji: String) {
    MAKANAN("Makanan", "🍛"),
    MINUMAN("Minuman", "🧋"),
    CAMILAN("Camilan", "🍌")
}

data class Food(
    val id: Int,
    val name: String,
    val description: String,
    val price: Int,
    val category: FoodCategory,
    val emoji: String,
    val rating: Double,
    val minutes: Int,
    val isBestSeller: Boolean = false
)

/** Data dummy menu. Ganti emoji dengan gambar (painterResource) kalau sudah punya aset foto. */
object FoodRepository {
    val foods: List<Food> = listOf(
        Food(
            1, "Nasi Goreng Spesial",
            "Nasi goreng bumbu rempah dengan telur mata sapi, ayam suwir, dan acar segar.",
            18_000, FoodCategory.MAKANAN, "🍛", 4.8, 15, isBestSeller = true
        ),
        Food(
            2, "Ayam Geprek Sambal Bawang",
            "Ayam crispy digeprek dengan sambal bawang pedas, disajikan dengan nasi hangat.",
            20_000, FoodCategory.MAKANAN, "🍗", 4.7, 20, isBestSeller = true
        ),
        Food(
            3, "Sate Ayam Madura",
            "10 tusuk sate ayam bakar dengan bumbu kacang kental dan lontong.",
            25_000, FoodCategory.MAKANAN, "🍢", 4.6, 25
        ),
        Food(
            4, "Mie Ayam Bakso",
            "Mie kenyal dengan topping ayam kecap, dua bakso sapi, dan kuah kaldu gurih.",
            17_000, FoodCategory.MAKANAN, "🍜", 4.5, 15
        ),
        Food(
            5, "Gado-Gado Siram",
            "Sayuran rebus, tahu, tempe, dan telur dengan siraman saus kacang.",
            16_000, FoodCategory.MAKANAN, "🥗", 4.4, 12
        ),
        Food(
            6, "Soto Betawi",
            "Soto berkuah santan dengan potongan daging sapi, tomat, dan emping.",
            24_000, FoodCategory.MAKANAN, "🍲", 4.6, 20
        ),
        Food(
            7, "Es Teh Manis Jumbo",
            "Teh melati dingin dengan gula pas, ukuran jumbo 500 ml.",
            6_000, FoodCategory.MINUMAN, "🥤", 4.6, 5, isBestSeller = true
        ),
        Food(
            8, "Es Jeruk Peras",
            "Jeruk peras segar tanpa pengawet, disajikan dengan es batu.",
            8_000, FoodCategory.MINUMAN, "🍊", 4.3, 5
        ),
        Food(
            9, "Kopi Susu Gula Aren",
            "Espresso, susu segar, dan gula aren asli. Manis legit, tidak bikin enek.",
            15_000, FoodCategory.MINUMAN, "☕", 4.8, 8, isBestSeller = true
        ),
        Food(
            10, "Es Campur",
            "Campuran alpukat, kelapa muda, cincau, dan sirup dengan susu kental manis.",
            14_000, FoodCategory.MINUMAN, "🍧", 4.4, 8
        ),
        Food(
            11, "Pisang Goreng Keju",
            "Pisang kepok goreng renyah dengan taburan keju dan susu cokelat.",
            12_000, FoodCategory.CAMILAN, "🍌", 4.5, 10
        ),
        Food(
            12, "Cireng Isi Ayam",
            "Cireng garing di luar, kenyal di dalam, isi ayam pedas, 6 buah.",
            10_000, FoodCategory.CAMILAN, "🥟", 4.2, 10
        )
    )

    fun byId(id: Int): Food? = foods.firstOrNull { it.id == id }
}

/** 18000 -> "Rp18.000" */
fun Int.toRupiah(): String {
    val formatter = NumberFormat.getNumberInstance(Locale.forLanguageTag("id-ID"))
    return "Rp" + formatter.format(this)
}
