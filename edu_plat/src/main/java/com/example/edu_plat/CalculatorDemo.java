package com.example.edu_plat;

import com.example.calculator.PriceCalculator;
import java.math.BigDecimal;

public class CalculatorDemo {
    public static void main(String[] args) {
        System.out.println("=== Тестирование подключенной библиотеки Price Calculator ===");

        // Пример 1: Из задания (5000 со скидкой 10%)
        BigDecimal price1 = new BigDecimal("5000");
        BigDecimal discount1 = new BigDecimal("10");
        BigDecimal result1 = PriceCalculator.calculateDiscountedPrice(price1, discount1);
        System.out.println("Исходная цена: " + price1 + ", Скидка: " + discount1 + "% => Итоговая цена: " + result1);

        // Пример 2: Граничное значение (Скидка 0%)
        BigDecimal price2 = new BigDecimal("1250.50");
        BigDecimal discount2 = BigDecimal.ZERO;
        BigDecimal result2 = PriceCalculator.calculateDiscountedPrice(price2, discount2);
        System.out.println("Исходная цена: " + price2 + ", Скидка: " + discount2 + "% => Итоговая цена: " + result2);

        // Пример 3: (Скидка 80%)
        BigDecimal price3 = new BigDecimal("9999.99");
        BigDecimal discount3 = new BigDecimal("80");
        BigDecimal result3 = PriceCalculator.calculateDiscountedPrice(price3, discount3);
        System.out.println("Исходная цена: " + price3 + ", Скидка: " + discount3 + "% => Итоговая цена: " + result3);
        
        // Пример 4: (Скидка 100%)
        BigDecimal price4 = new BigDecimal("1000");
        BigDecimal discount4 = new BigDecimal("100");
        BigDecimal result4 = PriceCalculator.calculateDiscountedPrice(price4, discount4);
        System.out.println("Исходная цена: " + price4 + ", Скидка: " + discount4 + "% => Итоговая цена: " + result4);

        // Пример 5: Проверка обработки ошибок (Недопустимая скидка)
        try {
            System.out.println("\nПопытка установить скидку 105%...");
            PriceCalculator.calculateDiscountedPrice(new BigDecimal("100"), new BigDecimal("105"));
        } catch (IllegalArgumentException e) {
            System.out.println("Успешно отклонено! Ошибка: " + e.getMessage());
        }
    }
}
