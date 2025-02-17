package de.xehmer.bitsideshoppingbasket.persistence

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import org.springframework.data.repository.CrudRepository
import java.math.BigDecimal

@Entity
@Table(indexes = [Index(columnList = "productCode", unique = true)])
class ProductEntity(
  @Column(nullable = false, unique = true)
  var productCode: String,
  @Column(nullable = false)
  var price: BigDecimal,
) : BaseEntity()

interface ProductRepository : CrudRepository<ProductEntity, Long> {
  fun findByProductCode(productId: String): ProductEntity?
}
