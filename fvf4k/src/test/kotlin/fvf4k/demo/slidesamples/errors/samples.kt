package fvf4k.demo.slidesamples.errors

//
//error object NullMerchantCategoryCode
//
//error class InvalidMerchantCategoryPattern(val value: String)
//
//typealias MccValidationFailed = NullMerchantCategoryCode | InvalidMerchantCategoryPattern
//
//@JvmInline value class MerchantCategoryCode private constructor(val value: String) {
//    companion object {
//        private val MCC_PATTERN = Regex("^\\d{4}$")
//
//        context(_: Raise<ValidationFailed>)
//        operator fun invoke(value: String?): MerchantCategoryCode | MccValidationFailed = when {
//            value == null -> NullMerchantCategoryCode
//            !MCC_PATTERN.matches(value) -> InvalidMerchantCategoryPattern(value)
//            else -> MerchantCategoryCode(value)
//        }
//    }
//}
//
//class MerchantCategoryCodeSpec : FreeSpec({
//    "should be invalid when non-numeric code is provided" {
//        val mcc = MerchantCategoryCode("12A4")
//
//        when(mcc) {
//            is MerchantCategoryCode -> fail("should not get here")
//            is NullMerchantCategoryCode -> fail("should not get here")
//            is InvalidMerchantCategoryPattern -> mcc shouldBe InvalidMerchantCategoryPattern("12A4")
//        }
//    }
//})
