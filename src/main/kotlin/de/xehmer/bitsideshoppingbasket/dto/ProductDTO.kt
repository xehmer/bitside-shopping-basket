package de.xehmer.bitsideshoppingbasket.dto

import java.math.BigDecimal

data class ProductDTO(
    val productCode: String,
    var price: BigDecimal,
)
