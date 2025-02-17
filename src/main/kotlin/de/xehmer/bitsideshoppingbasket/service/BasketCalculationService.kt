package de.xehmer.bitsideshoppingbasket.service

import de.xehmer.bitsideshoppingbasket.persistence.BasketEntity
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class BasketCalculationService {
  fun calculateBasketTotal(basket: BasketEntity): BigDecimal {
    return basket.entries.sumOf { it.product.price * BigDecimal.valueOf(it.quantity.toLong()) }
  }
}
