package de.xehmer.bitsideshoppingbasket.persistence

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull
import org.springframework.data.jpa.repository.JpaRepository
import java.util.*

@Entity
@Table(indexes = [Index(columnList = "uuid", unique = true)])
class BasketEntity(

    @NotNull
    val uuid: UUID,

    @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "basket")
    val entries: MutableList<BasketEntryEntity> = mutableListOf()

) : BaseEntity()

@Entity
class BasketEntryEntity(

    @ManyToOne(optional = false)
    var basket: BasketEntity,

    @ManyToOne(optional = false)
    var product: ProductEntity,

    @NotNull
    var quantity: Int

) : BaseEntity()

interface BasketRepository : JpaRepository<BasketEntity, Long> {
    fun findByUuid(uuid: UUID): BasketEntity?
}

interface BasketEntryRepository : JpaRepository<BasketEntryEntity, Long>
