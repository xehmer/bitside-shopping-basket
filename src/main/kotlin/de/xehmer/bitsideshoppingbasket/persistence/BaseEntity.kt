package de.xehmer.bitsideshoppingbasket.persistence

import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.MappedSuperclass

@MappedSuperclass
class BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    val id: Long? = null
}
