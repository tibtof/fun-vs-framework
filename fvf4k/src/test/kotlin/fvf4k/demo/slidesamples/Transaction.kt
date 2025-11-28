package fvf4k.demo.slidesamples

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe
import java.math.BigDecimal
import java.util.*


data class Transaction(
    val id: String,
    val clientId: String,
    val accountId: String,
    val amount: BigDecimal,
    val currencyCode: String,
    val mcc: String
)


val x = Transaction(
    "tx12345",
    "client1234",
    "acc98765",
    BigDecimal("250.75"),
    "EUR",
    "5411"
)

fun main() {
    println(Currency.getInstance("EUR").symbol)
    println(Currency.getInstance("EUR").numericCode)
    println(Currency.getInstance("RON").symbol)
    println(Currency.getInstance("RON").numericCode)
    println(Currency.getInstance("USD").symbol)
}

@JvmInline value class MerchantCategoryCode(val value: String) {
    companion object {
        private val MCC_PATTERN = Regex("^\\d{4}$")
    }

    init {
        require(MCC_PATTERN.matches(value)) {
            "Invalid merchant category code: '$value'"
        }
    }
}


class MerchantCategoryCodeSpec : FreeSpec({
    "should create a valid MerchantCategoryCode for a valid 4-digit code" {
        val mcc = MerchantCategoryCode("5411")
        mcc.value shouldBe "5411"
    }

    "should be invalid when non-numeric code is provided" {
        val e = shouldThrow<IllegalArgumentException> {
            MerchantCategoryCode("12A4")
        }

        e.message shouldBe "Invalid merchant category code: '12A4'"
    }
})

