package com.rabtech.task04.service;

import com.rabtech.task04.entity.Product;
import com.rabtech.task04.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("Should return all products")
    void testGetAllProducts() {
        Product p = new Product("Mouse", "Accessories", 500.0, 15);
        when(productRepository.findAll()).thenReturn(List.of(p));

        List<Product> result = productService.getAllProducts();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Mouse");
        verify(productRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should create new product")
    void testCreateProduct() {
        Product p = new Product("Keyboard", "Accessories", 1500.0, 8);
        when(productRepository.save(any(Product.class))).thenReturn(p);

        Product created = productService.createProduct(p);

        assertThat(created.getName()).isEqualTo("Keyboard");
        verify(productRepository, times(1)).save(p);
    }

    @Test
    @DisplayName("Should throw exception when updating non-existent product")
    void testUpdateProductNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        Product updateDetails = new Product("Unknown", "Other", 100.0, 1);

        assertThatThrownBy(() -> productService.updateProduct(99L, updateDetails))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Product not found with id: 99");
    }
}

