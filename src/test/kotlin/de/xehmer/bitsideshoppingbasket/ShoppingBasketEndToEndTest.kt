package de.xehmer.bitsideshoppingbasket

import de.xehmer.bitsideshoppingbasket.persistence.*
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.AfterEach
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

    @Autowired
    lateinit var promotionRepository: PromotionRepository

    private lateinit var product1: ProductEntity
    private lateinit var product2: ProductEntity

    @BeforeAll
    fun setUp() {
        product1 = productRepository.save(ProductEntity(productCode = "A0001", price = BigDecimal("12.99")))
        product2 = productRepository.save(ProductEntity(productCode = "A0002", price = BigDecimal("3.99")))
    }

    @Test
    fun `should add single product`() {
        val basketUri = createBasket()

        createEntry(basketUri, product1.productCode)

        verifyBasket(basketUri, "12.99")
    }

    @Test
    fun `should merge entries with same product`() {
        val basketUri = createBasket()

        createEntry(basketUri, product1.productCode)
        createEntry(basketUri, product2.productCode)
        createEntry(basketUri, product1.productCode)

        verifyBasket(basketUri, "29.97") {
            jsonPath("$.entries", hasSize<Any>(2))
        }
    }

    @Test
    fun `should apply free article promotion`() {
        // given
        promotionRepository.save(BuyXGetOneFreePromotionEntity(product = product2, necessaryQuantity = 1))

        // when
        val basketUri = createBasket()

        createEntry(basketUri, product2.productCode)
        createEntry(basketUri, product1.productCode)
        createEntry(basketUri, product2.productCode)

        // then
        verifyBasket(basketUri, "16.98")
    }

    @Test
    fun `should apply product discount promotion`() {
        // given
        promotionRepository.save(ProductDiscountPromotionEntity(product = product1, discountPercent = 10))

        // when
        val basketUri = createBasket()

        createEntry(basketUri, product2.productCode)
        createEntry(basketUri, product1.productCode)
        createEntry(basketUri, product2.productCode)

        // then
        verifyBasket(basketUri, "19.67")
    }

    @Test
    fun `should apply both promotions`() {
        // given
        promotionRepository.save(
            ProductDiscountPromotionEntity(
                product = product2,
                discountPercent = 10
            ).apply { this.priority = 100 })
        promotionRepository.save(BuyXGetOneFreePromotionEntity(product = product2, necessaryQuantity = 3))

        // when
        val basketUri = createBasket()

        createEntry(basketUri, product2.productCode, 7)

        // then
        verifyBasket(basketUri, "21.55")
    }

    @Test
    fun `should apply both promotions with swapped priority`() {
        // given
        promotionRepository.save(ProductDiscountPromotionEntity(product = product2, discountPercent = 10))
        promotionRepository.save(
            BuyXGetOneFreePromotionEntity(
                product = product2,
                necessaryQuantity = 3
            ).apply { this.priority = 100 })

        // when
        val basketUri = createBasket()

        createEntry(basketUri, product2.productCode, 7)

        // then
        verifyBasket(basketUri, "21.55")
    }

    @AfterEach
    fun tearDown() {
        promotionRepository.deleteAll()
    }

    private fun createBasket(): String {
        val basketCreationResult = mockMvc.post("/api/v1/basket")
            .andExpectAll {
                status { isCreated() }
                header { exists(HttpHeaders.LOCATION) }
            }.andReturn()
        return basketCreationResult.response.getHeader(HttpHeaders.LOCATION)!!
    }

    private fun createEntry(basketUri: String, productCode: String, quantity: Int = 1) {
        mockMvc.post("$basketUri/entry") {
            queryParam("productCode", productCode)
            queryParam("quantity", quantity.toString())
        }.andExpect {
            status { isCreated() }
        }
    }

    private fun verifyBasket(
        basketUri: String,
        expectedTotal: String?,
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
