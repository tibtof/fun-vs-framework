package fun.vs.fw.demo.domain;

import fun.vs.fw.demo.domain.Transaction.MerchantCategoryCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MerchantCategoryCodeTest {

    @DisplayName("Should create a valid MerchantCategoryCode for a valid 4-digit code")
    @Test
    void should_create_valid_merchant_category_code_when_given_valid_4_digit_code() {
        MerchantCategoryCode mcc = new MerchantCategoryCode("1234");
        assertEquals("1234", mcc.value(), "The value should be '1234'");
    }

    @DisplayName("Should throw NullPointerException when null value is provided")
    @Test
    void should_throw_null_pointer_exception_when_null_value_provided_for_merchant_category_code() {
        NullPointerException exception = assertThrows(NullPointerException.class, () -> {
            new MerchantCategoryCode(null);
        });
        assertEquals("Merchant category code cannot be null", exception.getMessage());
    }

    @DisplayName("Should throw IllegalArgumentException when non-numeric code is provided")
    @Test
    void should_throw_illegal_argument_exception_when_non_numeric_code_provided_for_merchant_category_code() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new MerchantCategoryCode("12A4");
        });
        assertEquals("Merchant category code must be exactly 4 digits", exception.getMessage());
    }

    @DisplayName("Should throw IllegalArgumentException when code is less than 4 digits")
    @Test
    void should_throw_illegal_argument_exception_when_code_length_is_less_than_4_digits() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new MerchantCategoryCode("123");
        });
        assertEquals("Merchant category code must be exactly 4 digits", exception.getMessage());
    }

    @DisplayName("Should throw IllegalArgumentException when code is more than 4 digits")
    @Test
    void should_throw_illegal_argument_exception_when_code_length_is_more_than_4_digits() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            new MerchantCategoryCode("12345");
        });
        assertEquals("Merchant category code must be exactly 4 digits", exception.getMessage());
    }
}