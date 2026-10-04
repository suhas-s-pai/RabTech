package com.rabtech.task04.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabtech.task04.entity.Product;
import com.rabtech.task04.service.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    @DisplayName("GET /api/products should return empty list initially")
    void testGetAllProductsEmpty() throws Exception {
        when(productService.getAllProducts()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @DisplayName("POST /api/products should create product and return 201 Created")
    void testCreateProduct() throws Exception {
        Product input = new Product("Laptop", "Electronics", 55000.0, 10);
        Product created = new Product("Laptop", "Electronics", 55000.0, 10);
        created.setId(1L);

        when(productService.createProduct(any(Product.class))).thenReturn(created);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Laptop"))
                .andExpect(jsonPath("$.category").value("Electronics"))
                .andExpect(jsonPath("$.price").value(55000.0))
                .andExpect(jsonPath("$.quantity").value(10));
    }

    @Test
    @DisplayName("GET /api/products/{id} should return product if found")
    void testGetProductByIdFound() throws Exception {
        Product p = new Product("Monitor", "Electronics", 12000.0, 3);
        p.setId(1L);

        when(productService.getProductById(1L)).thenReturn(Optional.of(p));

        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Monitor"));
    }
}

