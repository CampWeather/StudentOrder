# FoodOrder: Aplikasi Pemesanan Makanan dengan Material Design 3

Aplikasi Android pemesanan makanan sederhana yang dibuat dengan **Kotlin**, **Jetpack Compose**, dan **Material Design 3 (Material You)**. Dibuat sebagai tugas mata kuliah Pemrograman Perangkat Bergerak (PPB).

**Rilo Zidane Afrianta Tambunan (NRP 5053251048)** 

---

## 1. Deskripsi

FoodOrder memungkinkan pengguna melihat daftar menu, mencari dan memfilter menu, membuka detail makanan, menambahkannya ke keranjang, melakukan checkout, lalu menerima nomor pesanan. Pengguna juga dapat mengganti tema terang, gelap, atau mengikuti sistem, serta mengaktifkan warna dinamis (Material You) di Android 12 ke atas.

Tampilan menyesuaikan ukuran layar: HP memakai `NavigationBar` di bawah, sedangkan layar lebar (tablet atau mode landscape) memakai `NavigationRail` di kiri.

## 2. Teknologi yang Digunakan

| Komponen | Keterangan |
|---|---|
| Bahasa | Kotlin 2.2.10 |
| UI | Jetpack Compose (Compose BOM 2026.02.01) |
| Desain | Material Design 3 (`androidx.compose.material3`) |
| Navigasi | Navigation Compose 2.9.0 |
| State | `ViewModel` + `mutableStateListOf` |
| Penyimpanan tema | `SharedPreferences` |
| Ikon | `material-icons-extended` |
| Build | Android Gradle Plugin 9.3.3, Gradle Kotlin DSL |
| SDK | minSdk 24, targetSdk 37 |

## 3. Struktur Project

```
app/src/main/java/com/example/foodorder/
├── MainActivity.kt            # entry point, tema, dan penyimpanan preferensi
├── data/
│   ├── Food.kt                # model Food, kategori, daftar menu dummy, format rupiah
│   └── Pricing.kt             # aturan ongkir dan batas jumlah pesanan
├── viewmodel/
│   └── FoodViewModel.kt       # state keranjang dan favorit
└── ui/
    ├── Adaptive.kt            # kelas lebar layar (Compact / Medium / Expanded)
    ├── FoodOrderApp.kt        # NavHost, NavigationBar / NavigationRail
    ├── components/
    │   └── Components.kt      # komponen bersama
    ├── screens/
    │   ├── HomeScreen.kt      # daftar menu, pencarian, filter
    │   ├── DetailScreen.kt    # detail menu dan tambah ke keranjang
    │   ├── CartScreen.kt      # keranjang belanja
    │   ├── CheckoutScreen.kt  # form pemesanan dan pembayaran
    │   ├── SuccessScreen.kt   # konfirmasi dan nomor pesanan
    │   └── ThemeScreen.kt     # pengaturan tema
    └── theme/
        ├── Color.kt           # palet warna light dan dark
        └── Theme.kt           # tema, tipografi, bentuk, dynamic color
```

## 4. Fitur Aplikasi

| Layar | Fitur |
|---|---|
| **Beranda** | Daftar menu dengan `LazyColumn`, kolom pencarian, filter kategori (Makanan, Minuman, Camilan, Favorit), tambah cepat ke keranjang dengan Snackbar |
| **Detail Menu** | Rating dan estimasi waktu, deskripsi, pengatur jumlah, catatan untuk penjual, dialog konfirmasi setelah ditambahkan |
| **Keranjang** | Ubah jumlah, hapus item dengan opsi **Urungkan**, subtotal, ongkir, indikator progres menuju gratis ongkir |
| **Checkout** | Pilihan antar atau ambil sendiri, data penerima dengan validasi, empat metode pembayaran, ringkasan pesanan |
| **Sukses** | Nomor pesanan (contoh `FO-1234`) dan tombol kembali ke beranda |
| **Tema** | Mode terang, gelap, atau sistem; Switch warna dinamis; pratinjau tipografi dan warna. Pilihan tersimpan setelah aplikasi ditutup |
| **Navigasi** | Tiga tab utama (Beranda, Keranjang, Tema) dengan Badge jumlah item di tab Keranjang |

**Aturan harga:** ongkir Rp5.000, gratis jika subtotal mencapai Rp50.000 atau pesanan diambil sendiri. Jumlah maksimal per menu adalah 20.

## 5. Material Components yang Digunakan

`CenterAlignedTopAppBar`, `TopAppBar`, `Card`, `Button`, `TextButton`, `FilledIconButton`, `IconButton`, `OutlinedTextField`, `FilterChip`, `AssistChip`, `NavigationBar`, `NavigationRail`, `AlertDialog`, `Snackbar`, `Switch`, `RadioButton`, `SegmentedButton`, `BadgedBox` dan `Badge`, `ListItem`, `LinearProgressIndicator`, `HorizontalDivider`.

Tema memakai skema warna terang dan gelap sendiri, tipografi Material 3 (judul `Serif`, isi `Sans-serif`), dan bentuk sudut membulat yang diatur di `Theme.kt`.

## 6. Potongan Kode Penting

### a. Model data

```kotlin
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
```

### b. ViewModel keranjang

`mutableStateListOf` membuat Compose otomatis menggambar ulang tampilan saat isi keranjang berubah.

```kotlin
class FoodViewModel : ViewModel() {
    private val _cart = mutableStateListOf<CartItem>()
    val cart: List<CartItem> get() = _cart

    val totalItems: Int get() = _cart.sumOf { it.quantity }
    val subtotal: Int get() = _cart.sumOf { it.total }

    fun addToCart(food: Food, quantity: Int = 1, note: String = "") { /* ... */ }
    fun changeQuantity(foodId: Int, delta: Int) { /* ... */ }
    fun removeFromCart(foodId: Int) { /* ... */ }
}
```

### c. Layout adaptif

```kotlin
enum class WidthClass { Compact, Medium, Expanded }

@Composable
fun rememberWidthClass(): WidthClass {
    val widthDp = LocalConfiguration.current.screenWidthDp
    return when {
        widthDp < 600 -> WidthClass.Compact
        widthDp < 840 -> WidthClass.Medium
        else -> WidthClass.Expanded
    }
}
```

Hasilnya dipakai untuk memilih `NavigationBar` atau `NavigationRail`, serta jumlah kolom dan tata letak panel di layar lebar.

### d. Tema terang, gelap, dan dynamic color

```kotlin
enum class ThemeMode { LIGHT, DARK, SYSTEM }

@Composable
fun FoodOrderTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val darkTheme = when (themeMode) {
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        darkTheme -> DarkColors
        else -> LightColors
    }
    MaterialTheme(colorScheme = colorScheme, typography = AppTypography, shapes = AppShapes, content = content)
}
```

## 7. Cara Menjalankan

**Prasyarat:** Android Studio versi terbaru dan emulator atau HP Android (minimal Android 7.0 / API 24).

1. Clone repository ini:
   ```bash
   git clone https://github.com/CampWeather/StudentOrder.git
   ```
2. Buka folder `FoodOrder` di Android Studio (**File → Open**).
3. Tunggu Gradle sync selesai. Jika diminta, klik **Sync Now**.
4. Pilih emulator atau perangkat, lalu klik **Run** (▶).

## 8. Screenshot

| Beranda | memilih Menu | Keranjang |
|---|---|---|
| <img width="228" height="506" alt="WhatsApp Image 2026-11-21 at 20 24 44" src="https://github.com/user-attachments/assets/375abb36-1a30-411e-8b50-b31db5b6fad6" /> | <img width="228" height="506" alt="WhatsApp Image 2026-11-21 at 20 24 45" src="https://github.com/user-attachments/assets/2f6e3244-0028-4761-ba6a-930db8a8cbf7" /> | <img width="228" height="506" alt="WhatsApp Image 2026-11-21 at 20 24 45 (1)" src="https://github.com/user-attachments/assets/5b4b11c3-bd5f-481e-add7-4889da8b7871" /> |

| Checkout | Sukses | Tema (Light) |
|---|---|---|
| <img width="228" height="506" alt="WhatsApp Image 2026-11-21 at 20 24 45 (2)" src="https://github.com/user-attachments/assets/384796ef-4107-48fc-a2ac-348c88435127" /> <img width="228" height="506" alt="WhatsApp Image 2026-11-21 at 20 24 45 (3)" src="https://github.com/user-attachments/assets/9a4c400b-b61f-4720-a344-07d84ea91987" /> | <img width="228" height="506" alt="WhatsApp Image 2026-11-21 at 20 24 46" src="https://github.com/user-attachments/assets/f8625461-19a5-4161-ae5c-6dec24e63fb3" /> | <img width="228" height="506" alt="WhatsApp Image 2026-11-21 at 20 24 46 (1)" src="https://github.com/user-attachments/assets/c8fb4f9c-4db9-41f6-bb42-74c302409640" /> |

| Search (ditemukan) | Search (tidak ditemukan) |
|---|---|
| <img width="228" height="506" alt="WhatsApp Image 2026-11-21 at 20 24 46 (2)" src="https://github.com/user-attachments/assets/86047aa7-69a0-48f9-9195-b91dc12ee47e" /> | <img width="228" height="506" alt="WhatsApp Image 2026-11-21 at 20 24 47" src="https://github.com/user-attachments/assets/03f45466-3b64-4917-a66d-695c4daf1bad" /> | 


## 9. Kesimpulan

Jetpack Compose dan Material Design 3 memudahkan pembuatan antarmuka yang konsisten, modern, dan mudah diubah temanya. Pemisahan `data`, `viewmodel`, dan `ui` menjaga kode tetap rapi, sementara `ViewModel` mempertahankan isi keranjang saat layar diputar. Penyesuaian layout berdasarkan lebar layar membuat aplikasi nyaman dipakai di HP maupun layar yang lebih besar.
