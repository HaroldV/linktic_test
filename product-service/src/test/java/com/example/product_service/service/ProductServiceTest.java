package com.example.product_service.service;

import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product product;

    @BeforeEach
    void setUp() {
        product = new Product(1L, "Smartphone", 799.99);
    }

    @Test
    void whenCreateProduct_thenProductIsSaved() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product createdProduct = productService.createProduct(new Product(null, "Smartphone", 799.99));

        assertNotNull(createdProduct);
        assertEquals("Smartphone", createdProduct.getName());
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void whenUpdateProduct_thenProductIsUpdated() {

        Product updatedProductDetails = new Product(1L, "Smartphone Pro", 899.99);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProductDetails);

        Product result = productService.updateProduct(1L, updatedProductDetails);

        assertEquals("Smartphone Pro", result.getName());
        assertEquals(899.99, result.getPrice());
        verify(productRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void whenUpdateProduct_withInvalidId_thenThrowsException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(EntityNotFoundException.class, () -> productService.updateProduct(99L, new Product()));
    }

    @Test
    void whenDeleteProduct_thenProductIsDeleted() {
        when(productRepository.existsById(1L)).thenReturn(true);
        doNothing().when(productRepository).deleteById(1L);

        productService.deleteProduct(1L);

        verify(productRepository, times(1)).existsById(1L);
        verify(productRepository, times(1)).deleteById(1L);
    }

}
