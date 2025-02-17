package de.xehmer.bitsideshoppingbasket.service

import de.xehmer.bitsideshoppingbasket.dto.BasketDTO
import de.xehmer.bitsideshoppingbasket.dto.BasketEntryDTO
import de.xehmer.bitsideshoppingbasket.persistence.*
import org.springframework.stereotype.Service
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
    val basket = basketRepository.findByUuid(uuid) ?: throw IllegalArgumentException("Basket with id $uuid not found")
    return convertBasket(basket)
  }

  fun addEntry(basketUuid: UUID, productCode: String, quantity: Int): BasketEntryDTO {
    val basket = basketRepository.findByUuid(basketUuid)
      ?: throw IllegalArgumentException("Basket with id $basketUuid not found")
    val product = productRepository.findByProductCode(productCode)
      ?: throw IllegalArgumentException("Product with code $productCode not found")

    val existingEntryForProduct = basket.entries.find { it.product.id == product.id }
    if (existingEntryForProduct != null) {
      existingEntryForProduct.quantity += quantity
      basketEntryRepository.save(existingEntryForProduct)
      return convertEntry(existingEntryForProduct)
    } else {
      val entryToInsert = BasketEntryEntity(basket = basket, product = product, quantity = quantity)
      val createdEntry = basketEntryRepository.save(entryToInsert)
      return convertEntry(createdEntry)
    }
  }

  fun convertBasket(source: BasketEntity): BasketDTO {
    return BasketDTO(
      uuid = source.uuid,
      entries = source.entries.map(::convertEntry),
      totalPrice = basketCalculationService.calculateBasketTotal(source)
    )
  }

  fun convertEntry(entry: BasketEntryEntity): BasketEntryDTO {
    return BasketEntryDTO(
      quantity = entry.quantity,
      product = productService.convertProduct(entry.product)
    )
  }
}
