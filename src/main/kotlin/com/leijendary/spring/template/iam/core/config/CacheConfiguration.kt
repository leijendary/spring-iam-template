package com.leijendary.spring.template.iam.core.config

import org.springframework.boot.autoconfigure.cache.CacheManagerCustomizer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.transaction.AbstractTransactionSupportingCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.convert.converter.Converter
import org.springframework.data.redis.core.convert.RedisCustomConversions

@Configuration
@EnableCaching
class CacheConfiguration {
    @Bean
    fun redisCustomConversions(converters: List<Converter<*, *>>): RedisCustomConversions {
        return RedisCustomConversions(converters)
    }

    @Bean
    fun cacheManagerCustomizer(): CacheManagerCustomizer<AbstractTransactionSupportingCacheManager> {
        return CacheManagerCustomizer {
            it.isTransactionAware = true
        }
    }
}
