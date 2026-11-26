package com.example.calculator;

import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import static org.junit.jupiter.api.Assertions.*;

class PriceCalculatorTest {

    @Test
    void shouldCalculateCorrectPriceForStandardDiscount() {
        BigDecimal basePrice = new BigDecimal("5000");
        BigDecimal discount = new BigDecimal("10");
        
        BigDecimal result = PriceCalculator.calculateDiscountedPrice(basePrice, discount);
        
        assertEquals(new BigDecimal("4500.00"), result);
    }

    @Test
    void shouldReturnOriginalPriceWhenDiscountIsZero() {
        BigDecimal basePrice = new BigDecimal("1500");
        BigDecimal discount = BigDecimal.ZERO;

        BigDecimal result = PriceCalculator.calculateDiscountedPrice(basePrice, discount);

        assertEquals(new BigDecimal("1500.00"), result);
    }

    @Test
    void shouldReturnZeroWhenDiscountIsHundredPercent() {
        BigDecimal basePrice = new BigDecimal("2999.99");
        BigDecimal discount = new BigDecimal("100");

        BigDecimal result = PriceCalculator.calculateDiscountedPrice(basePrice, discount);

        assertEquals(new BigDecimal("0.00"), result);
    }

    @Test
    void shouldThrowExceptionWhenDiscountIsInvalid() {
        BigDecimal basePrice = new BigDecimal("100");
        
        // Тест скидки больше 100
        assertThrows(IllegalArgumentException.class, () -> 
            PriceCalculator.calculateDiscountedPrice(basePrice, new BigDecimal("100.01"))
        );

        // Тест отрицательной скидки
        assertThrows(IllegalArgumentException.class, () -> 
            PriceCalculator.calculateDiscountedPrice(basePrice, new BigDecimal("-5"))
        );
    }
}
