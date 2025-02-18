package de.xehmer.bitsideshoppingbasket.service

import de.xehmer.bitsideshoppingbasket.dto.BasketDTO
import de.xehmer.bitsideshoppingbasket.persistence.BasketEntity
import de.xehmer.bitsideshoppingbasket.persistence.PromotionEntity
import de.xehmer.bitsideshoppingbasket.persistence.PromotionRepository
import de.xehmer.bitsideshoppingbasket.promotion.PromotionHandler
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import java.math.BigDecimal

private val PROMOTION_SORT =
    Sort.sort(PromotionEntity::class.java).by(PromotionEntity::priority).descending()

@Service
class BasketCalculationService(
    private val promotionRepository: PromotionRepository,
    private val promotionHandlers: List<PromotionHandler>,
) {

    fun calculateBaseBasketTotal(basket: BasketEntity): BigDecimal {
        return basket.entries.sumOf { (it.product.price * BigDecimal(it.quantity)) }
    }

    fun applyPromotions(basket: BasketDTO) {
        promotionRepository.findAll(PROMOTION_SORT).forEach { promotion ->
            promotionHandlers.filter { it.canHandle(promotion) }.forEach { it.apply(promotion, basket) }
        }
    }
}
