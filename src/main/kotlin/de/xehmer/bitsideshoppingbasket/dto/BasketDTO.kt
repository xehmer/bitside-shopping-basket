package de.xehmer.bitsideshoppingbasket.dto

import java.math.BigDecimal
import java.util.*

data class BasketDTO(
    val uuid: UUID,
    val entries: List<BasketEntryDTO>,
    val totalPrice: BigDecimal,
)

data class BasketEntryDTO(
    val quantity: Int,
    val product: ProductDTO
)
