package de.xehmer.bitsideshoppingbasket.persistence

import jakarta.persistence.Entity
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType
import jakarta.persistence.ManyToOne
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotNull
import org.springframework.data.jpa.repository.JpaRepository

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
abstract class PromotionEntity : BaseEntity() {

    @NotNull
    var priority: Int = 0
}

@Entity
class BuyXGetOneFreePromotionEntity(

    @ManyToOne(optional = false)
    val product: ProductEntity,

    @NotNull
    val necessaryQuantity: Int

) : PromotionEntity()

@Entity
class ProductDiscountPromotionEntity(

    @ManyToOne(optional = false)
    val product: ProductEntity,

    @NotNull
    @Min(1)
    @Max(100)
    val discountPercent: Int

) : PromotionEntity()

interface PromotionRepository : JpaRepository<PromotionEntity, Int>
