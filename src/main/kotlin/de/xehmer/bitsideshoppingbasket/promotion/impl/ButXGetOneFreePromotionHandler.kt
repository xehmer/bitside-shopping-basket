package de.xehmer.bitsideshoppingbasket.promotion.impl

import de.xehmer.bitsideshoppingbasket.dto.BasketDTO
import de.xehmer.bitsideshoppingbasket.dto.BasketEntryDTO
import de.xehmer.bitsideshoppingbasket.persistence.BuyXGetOneFreePromotionEntity
import de.xehmer.bitsideshoppingbasket.persistence.PromotionEntity
import de.xehmer.bitsideshoppingbasket.promotion.PromotionHandler
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ButXGetOneFreePromotionHandler : PromotionHandler {
    override fun canHandle(promotion: PromotionEntity): Boolean {
        return promotion is BuyXGetOneFreePromotionEntity
    }

    override fun apply(promotion: PromotionEntity, basket: BasketDTO) {
        require(promotion is BuyXGetOneFreePromotionEntity)
        basket.entries
            .filter { it.product.productCode == promotion.product.productCode }
            .filter { it.quantity > promotion.necessaryQuantity }
            .forEach { entry ->
                val freeItemsCount = entry.quantity / (promotion.necessaryQuantity + 1)
                val discount = entry.product.price * BigDecimal(freeItemsCount)
                basket.totalPrice -= discount

                // "separate" the free and the paid entries into two to make it clear what happened downstream
                entry.quantity -= freeItemsCount
                basket.entries.add(
                    BasketEntryDTO(
                        quantity = freeItemsCount,
                        product = entry.product.copy(price = BigDecimal.ZERO.setScale(2))
                    )
                )
            }
    }
}
