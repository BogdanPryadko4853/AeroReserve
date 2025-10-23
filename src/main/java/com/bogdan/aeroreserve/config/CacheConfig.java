package com.bogdan.aeroreserve.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

/**
 * Конфигурация кэширования приложения с использованием Caffeine.
 * Настраивает менеджер кэша для улучшения производительности приложения.
 */
@Configuration
@EnableCaching
public class CacheConfig {

    /**
     * Создает и настраивает менеджер кэша для приложения.
     * Использует Caffeine в качестве реализации кэша.
     *
     * @return сконфигурированный менеджер кэша
     */
    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("flights", "seats");
        cacheManager.setCaffeine(Caffeine.newBuilder()
                // Максимальное количество записей в кэше
                .maximumSize(1000)
                // Время жизни записи в кэше после записи
                .expireAfterWrite(10, TimeUnit.MINUTES)
                // Включение сбора статистики по кэшу
                .recordStats());
        return cacheManager;
    }
}