package org.springframework.samples.petclinic.customers.web;

import io.micrometer.core.aop.TimedAspect;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.samples.petclinic.customers.model.OwnerRepository;
import org.springframework.samples.petclinic.customers.model.PetRepository;
import org.springframework.samples.petclinic.customers.model.PetType;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {
    
    @Bean
    @Primary
    public MeterRegistry meterRegistry() {
        return new SimpleMeterRegistry();
    }

    @Bean
    public TimedAspect timedAspect(MeterRegistry registry) {
        return new TimedAspect(registry);
    }
    
    @Bean
    public OwnerRepository ownerRepository() {
        return mock(OwnerRepository.class);
    }
    
    @Bean
    public PetRepository petRepository() {
        return mock(PetRepository.class);
    }
    
    @Bean
    public PetType petTypeRepository() {
        return mock(PetType.class);
    }
}