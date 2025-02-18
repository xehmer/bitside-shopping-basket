package de.xehmer.bitsideshoppingbasket.promotion

import de.xehmer.bitsideshoppingbasket.dto.BasketDTO
import de.xehmer.bitsideshoppingbasket.persistence.PromotionEntity

interface PromotionHandler {
    fun canHandle(promotion: PromotionEntity): Boolean
    fun apply(promotion: PromotionEntity, basket: BasketDTO)
}
