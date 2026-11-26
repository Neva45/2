package com.example.calculator;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PriceCalculator {

    /**
     * Рассчитывает стоимость товара с учётом скидки.
     *
     * @param basePrice        исходная цена (должна быть >= 0)
     * @param discountPercent  скидка в процентах (от 0 до 100 включительно)
     * @return цена со скидкой, округленная до 2 знаков после запятой
     * @throws IllegalArgumentException если цена отрицательная или скидка вне диапазона [0; 100]
     */
    public static BigDecimal calculateDiscountedPrice(BigDecimal basePrice, BigDecimal discountPercent) {
        if (basePrice == null || discountPercent == null) {
            throw new IllegalArgumentException("Базовая цена и процент скидки не должны быть null");
        }

        if (basePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Базовая цена не может быть отрицательной");
        }

        if (discountPercent.compareTo(BigDecimal.ZERO) < 0 || discountPercent.compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException("Скидка должна быть в диапазоне от 0 до 100 процентов включительно");
        }

        // Формула: basePrice * (1 - discountPercent / 100)
        BigDecimal discountFactor = BigDecimal.ONE.subtract(
                discountPercent.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP)
        );

        return basePrice.multiply(discountFactor).setScale(2, RoundingMode.HALF_UP);
    }
}
