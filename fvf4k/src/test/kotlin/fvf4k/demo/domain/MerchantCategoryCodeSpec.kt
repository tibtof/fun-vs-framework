package fvf4k.demo.domain

import arrow.core.left
import arrow.core.raise.eagerEffect
import arrow.core.raise.either
import arrow.core.raise.merge
import fvf4k.demo.domain.failure.InvalidMerchantCategoryPattern
import fvf4k.demo.domain.failure.NullMerchantCategoryCode
import fvf4k.demo.domain.model.MerchantCategoryCode
import io.kotest.assertions.AssertionErrorBuilder.Companion.fail
import io.kotest.assertions.arrow.core.shouldBeRight
import io.kotest.core.spec.style.FreeSpec
import io.kotest.matchers.shouldBe


class MerchantCategoryCodeSpec : FreeSpec({

    "should create a valid MerchantCategoryCode for a valid 4-digit code" {
        val mcc = either {
            MerchantCategoryCode("1234")
        }

        mcc shouldBeRight { "1234 " }
    }

    "should be invalid when null code is provided" {
        val error = either {
            MerchantCategoryCode(null)
            fail("should not get here")
        }

        error shouldBe NullMerchantCategoryCode.left()
    }

    "should be invalid when non-numeric code is provided" {
        val error = eagerEffect {
            MerchantCategoryCode("12A4")
            fail("should not get here")
        }.merge()

        error shouldBe InvalidMerchantCategoryPattern("12A4")
    }

    "should be invalid when code is less that 4 digits" {
        val error = eagerEffect {
            MerchantCategoryCode("123")
            fail("should not get here")
        }.merge()

        error shouldBe InvalidMerchantCategoryPattern("123")
    }

    "should be invalid when code is more that 4 digits" {
        val error = eagerEffect {
            MerchantCategoryCode("12345")
            fail("should not get here")
        }.merge()

        error shouldBe InvalidMerchantCategoryPattern("12345")
    }
})
