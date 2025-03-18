package org.springframework.samples.petclinic.customers;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CustomersServiceApplicationTest {

    @Test
    void contextLoads() {
        // This test verifies that the Spring context loads successfully
    }

    @Test
    void mainMethodStartsApplication() {
        // This test verifies that the main method can be called without throwing exceptions
        CustomersServiceApplication.main(new String[]{});
    }
} 