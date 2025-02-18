package de.xehmer.bitsideshoppingbasket.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.validation.constraints.NotNull
import org.springframework.data.jpa.repository.JpaRepository
import java.math.BigDecimal

@Entity
@Table(indexes = [Index(columnList = "productCode", unique = true)])
class ProductEntity(

    @NotNull
    var productCode: String,

    @NotNull
    var price: BigDecimal

) : BaseEntity()

interface ProductRepository : JpaRepository<ProductEntity, Long> {
    fun findByProductCode(productId: String): ProductEntity?
}
