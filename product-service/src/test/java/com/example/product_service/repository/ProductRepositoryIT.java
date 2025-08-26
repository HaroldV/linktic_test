package com.example.product_service.repository;

import com.example.product_service.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class ProductRepositoryIT {

    @Container
    public static PostgreSQLContainer<?> postgresSQLContainer = new PostgreSQLContainer<>("postgres:13-alpine");

    @Autowired
    private ProductRepository productRepository;

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgresSQLContainer::getPassword);
    }

    @Test
    void whenSave_thenProductCanBeFoundById() {
        Product product = new Product(null, "Laptop", 1200.00);

        Product savedProduct = productRepository.save(product);
        Product foundProduct = productRepository.findById(savedProduct.getId()).orElse(null);

        assertThat(foundProduct).isNotNull();
        assertThat(foundProduct.getName()).isEqualTo("Laptop");
    }
}
