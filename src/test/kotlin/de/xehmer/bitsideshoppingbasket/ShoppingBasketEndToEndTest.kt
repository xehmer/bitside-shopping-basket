package de.xehmer.bitsideshoppingbasket

import de.xehmer.bitsideshoppingbasket.persistence.ProductEntity
import de.xehmer.bitsideshoppingbasket.persistence.ProductRepository
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcResultMatchersDsl
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.post
import java.math.BigDecimal

@SpringBootTest
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ShoppingBasketEndToEndTest {
  @Autowired
  lateinit var mockMvc: MockMvc

  @Autowired
  lateinit var productRepository: ProductRepository

  @BeforeAll
  fun setUp() {
    productRepository.saveAll(
      listOf(
        ProductEntity(productCode = "A0001", price = BigDecimal("12.99")),
        ProductEntity(productCode = "A0002", price = BigDecimal("3.99")),
        ProductEntity(productCode = "A0003", price = BigDecimal("9.99")),
      )
    )
  }

  @Test
  fun `should add single product`() {
    val basketUri = createBasket()

    createEntry(basketUri, "A0001", 1)

    verifyBasket(basketUri, "12.99")
  }

  @Test
  fun `should merge entries with same product`() {
    val basketUri = createBasket()

    createEntry(basketUri, "A0001", 1)
    createEntry(basketUri, "A0002", 1)
    createEntry(basketUri, "A0001", 1)

    verifyBasket(basketUri, "29.97") {
      jsonPath("$.entries", hasSize<Any>(2))
    }
  }

  private fun createBasket(): String {
    val basketCreationResult = mockMvc.post("/api/v1/basket")
      .andExpectAll {
        status { isCreated() }
        header { exists(HttpHeaders.LOCATION) }
      }.andReturn()
    return basketCreationResult.response.getHeader(HttpHeaders.LOCATION)!!
  }

  private fun createEntry(basketUri: String, productCode: String, quantity: Int) {
    mockMvc.post("$basketUri/entry") {
      queryParam("productCode", productCode)
      queryParam("quantity", quantity.toString())
    }.andExpect {
      status { isOk() }
    }
  }

  private fun verifyBasket(
    basketUri: String,
    expectedTotal: String? = null,
    additionalAssertions: (MockMvcResultMatchersDsl.() -> Unit)? = null
  ) {
    mockMvc.get(basketUri).andExpectAll {
      status { isOk() }
      content { contentType(MediaType.APPLICATION_JSON) }
      expectedTotal?.let { jsonPath("$.totalPrice") { value(BigDecimal(it)) } }
      additionalAssertions?.invoke(this)
    }
  }
}
