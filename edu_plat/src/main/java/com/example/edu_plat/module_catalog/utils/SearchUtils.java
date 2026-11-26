package com.example.edu_plat.module_catalog.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import java.math.BigDecimal;

public class SearchUtils {

    // Экранирование спецсимволов для LIKE оператора
    public static String escapeLikePattern(String value) {
        if (value == null || value.isEmpty()) {
            return null;
        }
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }

    // Проверка бизнес-ограничений на цены (HTTP 400 Bad Request)
    public static void validatePriceParams(BigDecimal minPrice, BigDecimal maxPrice) {
        if (minPrice != null && minPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Минимальная цена не может быть отрицательной");
        }
        if (maxPrice != null && maxPrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Максимальная цена не может быть отрицательной");
        }
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Минимальная цена не может быть больше максимальной");
        }
    }
}
