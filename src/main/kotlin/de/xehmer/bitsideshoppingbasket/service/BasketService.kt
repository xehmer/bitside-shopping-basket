package de.xehmer.bitsideshoppingbasket.service

import de.xehmer.bitsideshoppingbasket.dto.BasketDTO
import de.xehmer.bitsideshoppingbasket.dto.BasketEntryDTO
import de.xehmer.bitsideshoppingbasket.persistence.*
import org.springframework.stereotype.Service
import java.math.RoundingMode
import java.util.*

@Service
class BasketService(
    private val basketRepository: BasketRepository,
    private val basketEntryRepository: BasketEntryRepository,
    private val productService: ProductService,
    private val productRepository: ProductRepository,
    private val basketCalculationService: BasketCalculationService,
) {
    fun createBasket(): BasketDTO {
        val createdBasket = basketRepository.save(BasketEntity(UUID.randomUUID()))
        return convertBasket(createdBasket)
    }

    fun getBasket(uuid: UUID): BasketDTO {
        val basket =
            basketRepository.findByUuid(uuid) ?: throw IllegalArgumentException("Basket with id $uuid not found")
        return convertBasket(basket)
    }

    fun addEntry(basketUuid: UUID, productCode: String, quantity: Int) {
        val basket = basketRepository.findByUuid(basketUuid)
            ?: throw IllegalArgumentException("Basket with id $basketUuid not found")
        val product = productRepository.findByProductCode(productCode)
            ?: throw IllegalArgumentException("Product with code $productCode not found")

        val existingEntryForProduct = basket.entries.find { it.product.id == product.id }
        if (existingEntryForProduct != null) {
            existingEntryForProduct.quantity += quantity
            basketEntryRepository.save(existingEntryForProduct)
        } else {
            val entryToInsert = BasketEntryEntity(basket = basket, product = product, quantity = quantity)
            basketEntryRepository.save(entryToInsert)
        }
    }

    fun convertBasket(source: BasketEntity): BasketDTO {
        val basketDTO = BasketDTO(
            uuid = source.uuid,
            entries = source.entries.map(::convertEntry).toMutableList(),
            totalPrice = basketCalculationService.calculateBaseBasketTotal(source)
        )
        basketCalculationService.applyPromotions(basketDTO)
        basketDTO.totalPrice = basketDTO.totalPrice.setScale(2, RoundingMode.HALF_UP)
        basketDTO.entries.forEach { entry ->
            entry.product.price = entry.product.price.setScale(2, RoundingMode.HALF_UP)
        }
        return basketDTO
    }

    fun convertEntry(entry: BasketEntryEntity): BasketEntryDTO {
        return BasketEntryDTO(
            quantity = entry.quantity,
            product = productService.convertProduct(entry.product)
        )
    }
}
