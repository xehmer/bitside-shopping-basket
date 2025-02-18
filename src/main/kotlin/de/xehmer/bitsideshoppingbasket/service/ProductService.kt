package de.xehmer.bitsideshoppingbasket.service

import de.xehmer.bitsideshoppingbasket.dto.ProductDTO
import de.xehmer.bitsideshoppingbasket.persistence.ProductEntity
import de.xehmer.bitsideshoppingbasket.persistence.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(
    private val productRepository: ProductRepository,
) {
    fun getProduct(productId: String): ProductDTO {
        val product = productRepository.findByProductCode(productId)
            ?: throw IllegalArgumentException("Product with id $productId not found")
        return convertProduct(product)
    }

    fun convertProduct(source: ProductEntity): ProductDTO {
        return ProductDTO(
            productCode = source.productCode,
            price = source.price
        )
    }
}
