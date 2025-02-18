package de.xehmer.bitsideshoppingbasket.promotion.impl

import de.xehmer.bitsideshoppingbasket.dto.BasketDTO
import de.xehmer.bitsideshoppingbasket.persistence.ProductDiscountPromotionEntity
import de.xehmer.bitsideshoppingbasket.persistence.PromotionEntity
import de.xehmer.bitsideshoppingbasket.promotion.PromotionHandler
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class ProductDiscountPromotionHandler : PromotionHandler {
    override fun canHandle(promotion: PromotionEntity): Boolean {
        return promotion is ProductDiscountPromotionEntity
    }

    override fun apply(promotion: PromotionEntity, basket: BasketDTO) {
        require(promotion is ProductDiscountPromotionEntity)
        basket.entries
            .filter { it.product.productCode == promotion.product.productCode && it.product.price > BigDecimal.ZERO }
            .forEach { entry ->
                val discountFactor = BigDecimal(promotion.discountPercent).divide(BigDecimal.valueOf(100))
                val newProductPrice = entry.product.price * (BigDecimal.ONE - discountFactor)
                val entryPriceDifference = (entry.product.price - newProductPrice) * BigDecimal(entry.quantity)
                basket.totalPrice -= entryPriceDifference
                entry.product.price = newProductPrice
            }
    }
}
