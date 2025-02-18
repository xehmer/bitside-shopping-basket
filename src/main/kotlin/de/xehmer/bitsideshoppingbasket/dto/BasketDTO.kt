package de.xehmer.bitsideshoppingbasket.dto

import java.math.BigDecimal
import java.util.*

data class BasketDTO(
    val uuid: UUID,
    val entries: MutableList<BasketEntryDTO>,
    var totalPrice: BigDecimal,
)

data class BasketEntryDTO(
    var quantity: Int,
    val product: ProductDTO
)
