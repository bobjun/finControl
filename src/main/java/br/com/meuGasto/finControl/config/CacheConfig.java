package br.com.meuGasto.finControl.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.util.Objects;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("gastos", "usuarios","tendencias","estatisticasPorCategoria","estatisticasMensais");
        Caffeine<Object, Object> caffeineSpec = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(1000)
                .recordStats();
        cacheManager.setCaffeine(Objects.requireNonNull(caffeineSpec));
        return cacheManager;
    }
}
