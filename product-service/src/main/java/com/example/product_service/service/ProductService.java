package com.example.product_service.service;

import com.example.product_service.model.Product;
import com.example.product_service.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Creates and saves a new product.
     * @param product Product to create
     * @return Created product
     */
    public Product createProduct(Product product) {
        return productRepository.save(product);
    }

    /**
     * Retrieves a product by its ID.
     * @param id Product ID
     * @return Optional containing the product if found, empty otherwise
     */
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }

    /**
     * Updates an existing product.
     * @param id Product ID
     * @param productDetails Product details to update
     * @return Updated product
     * @throws IllegalArgumentException if product not found
     */
    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Product not found with id: " + id));
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());

        return productRepository.save(product);
    }

    /**
     * Deletes a product by its ID.
     * @param id Product ID
     * @throws IllegalArgumentException if product not found
     */
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
    }

    /**
     * Retrieves all products with pagination.
     * @param pageable Pagination information
     * @return Page of products
     */
    public Page<Product> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable);
    }
}
