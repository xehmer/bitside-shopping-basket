package de.xehmer.bitsideshoppingbasket.persistence

import jakarta.persistence.*
import org.springframework.data.repository.CrudRepository
import java.util.*

@Entity
class BasketEntity(
  val uuid: UUID,
  @OneToMany(cascade = [(CascadeType.ALL)], mappedBy = "basket")
  val entries: MutableList<BasketEntryEntity> = mutableListOf(),
) : BaseEntity()

@Entity
class BasketEntryEntity(
  @ManyToOne(optional = false)
  var basket: BasketEntity,
  @ManyToOne(optional = false)
  var product: ProductEntity,
  @Column(nullable = false)
  var quantity: Int,
) : BaseEntity()

interface BasketRepository : CrudRepository<BasketEntity, Long> {
  fun findByUuid(uuid: UUID): BasketEntity?
}

interface BasketEntryRepository : CrudRepository<BasketEntryEntity, Long>
