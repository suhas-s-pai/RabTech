package com.rabtech.task04.repository;

import com.rabtech.task04.entity.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    @DisplayName("Should save and find product by ID")
    void testSaveAndFindById() {
        Product product = new Product("Test Laptop", "Electronics", 49999.99, 5);
        Product savedProduct = productRepository.save(product);

        assertThat(savedProduct.getId()).isNotNull();

        Optional<Product> foundProduct = productRepository.findById(savedProduct.getId());
        assertThat(foundProduct).isPresent();
        assertThat(foundProduct.get().getName()).isEqualTo("Test Laptop");
    }

    @Test
    @DisplayName("Should find all products")
    void testFindAll() {
        Product p1 = new Product("Phone", "Electronics", 20000.0, 10);
        Product p2 = new Product("Headphones", "Audio", 3000.0, 20);
        productRepository.save(p1);
        productRepository.save(p2);

        List<Product> products = productRepository.findAll();
        assertThat(products).hasSize(2);
    }
}

