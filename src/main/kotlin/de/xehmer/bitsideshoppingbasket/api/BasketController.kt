package de.xehmer.bitsideshoppingbasket.api

import de.xehmer.bitsideshoppingbasket.dto.BasketDTO
import de.xehmer.bitsideshoppingbasket.dto.BasketEntryDTO
import de.xehmer.bitsideshoppingbasket.service.BasketService
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import java.util.*

@RestController
@RequestMapping("/api/v1/basket")
class BasketController(
  private val basketService: BasketService
) {

  @PostMapping
  fun createBasket(): ResponseEntity<Unit> {
    val basket = basketService.createBasket()
    val basketUri = ServletUriComponentsBuilder.fromCurrentRequestUri().path("/${basket.uuid}").build().toUri()
    return ResponseEntity.created(basketUri).build()
  }

  @GetMapping("/{basketUuid}", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun getBasket(@PathVariable basketUuid: UUID): ResponseEntity<BasketDTO> {
    val basket = basketService.getBasket(basketUuid)
    return ResponseEntity.ok(basket)
  }

  @PostMapping("/{basketUuid}/entry", produces = [MediaType.APPLICATION_JSON_VALUE])
  fun addEntry(
    @PathVariable basketUuid: UUID,
    @RequestParam productCode: String,
    @RequestParam quantity: Int
  ): ResponseEntity<BasketEntryDTO> {
    val entry = basketService.addEntry(basketUuid, productCode, quantity)
    return ResponseEntity.ok(entry)
  }
}
