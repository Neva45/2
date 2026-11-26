package com.example.edu_plat;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;

public class ModuleArchitectureTest {

    @Test
    public void verifyArchitecture() {
        // Сканируем модули на уровне пакета, где лежит EduPlatApplication
        ApplicationModules modules = ApplicationModules.of(EduPlatApplication.class);
        
        // Выводим схему модулей в консоль
        System.out.println(modules);
        
        // Проверяем правила изоляции
        modules.verify();
    }
}
