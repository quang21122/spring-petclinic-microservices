package org.springframework.samples.petclinic.vets.system;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ContextConfiguration(classes = {CacheConfig.class, VetsProperties.class})
@ActiveProfiles("test")
class CacheConfigTest {

    @Autowired
    private CacheManager cacheManager;

    @Test
    void shouldCreateCacheManager() {
        assertThat(cacheManager).isNotNull();
        assertThat(cacheManager.getCacheNames()).isNotEmpty();
    }

    @Test
    void shouldHaveVetsCache() {
        assertThat(cacheManager.getCache("vets")).isNotNull();
    }
} 