package com.example.foodorder.data

object Pricing {
    const val DELIVERY_FEE = 5_000
    const val FREE_DELIVERY_MIN = 50_000
    const val MAX_QUANTITY = 20

    /** Ongkir flat, gratis kalau subtotal mencapai batas atau pesanan diambil sendiri. */
    fun deliveryFee(subtotal: Int, isDelivery: Boolean): Int =
        if (!isDelivery || subtotal == 0 || subtotal >= FREE_DELIVERY_MIN) 0 else DELIVERY_FEE
}
