package com.example.product_service.controller;

import com.example.product_service.dto.JsonApiData;
import com.example.product_service.dto.JsonApiResponse;
import com.example.product_service.dto.ProductAttributes;
import com.example.product_service.model.Product;
import com.example.product_service.service.ProductService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @Operation(summary = "Create a new product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Product created"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping
    public ResponseEntity<JsonApiResponse<JsonApiData<ProductAttributes>>> createProduct(@Valid @RequestBody Product product) {
        Product createdProduct = productService.createProduct(product);
        JsonApiData<ProductAttributes> data = toJsonApiData(createdProduct);
        return new ResponseEntity<>(new JsonApiResponse<>(data), HttpStatus.CREATED);
    }

    @Operation(summary = "Get a product by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product found"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<JsonApiResponse<JsonApiData<ProductAttributes>>> getProductById(@PathVariable Long id) {
        return productService.getProductById(id)
                .map(product -> new ResponseEntity<>(new JsonApiResponse<>(toJsonApiData(product)), HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));

    }

    @Operation(summary = "Update a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product updated"),
            @ApiResponse(responseCode = "404", description = "Product not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PutMapping("/{id}")
    public ResponseEntity<JsonApiResponse<JsonApiData<ProductAttributes>>> updateProduct(@PathVariable Long id, @Valid @RequestBody Product productDetails) {
        try {
            Product updatedProduct = productService.updateProduct(id, productDetails);
            JsonApiData<ProductAttributes> data = toJsonApiData(updatedProduct);
            return new ResponseEntity<>(new JsonApiResponse<>(data), HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Delete a product")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Product deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        try {
            productService.deleteProduct(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (EntityNotFoundException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Get all products (paginated)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved")
    })
    @GetMapping
    public ResponseEntity<JsonApiResponse<List<JsonApiData<ProductAttributes>>>> getAllProducts(Pageable pageable) {
        Page<Product> products = productService.getAllProducts(pageable);
        List<JsonApiData<ProductAttributes>> dataList = products.getContent()
                .stream()
                .map(this::toJsonApiData)
                .toList();

        JsonApiResponse<List<JsonApiData<ProductAttributes>>> response = new JsonApiResponse<>();
        response.setDataList(Collections.singletonList(dataList));

        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    private JsonApiData<ProductAttributes> toJsonApiData(Product product) {
        ProductAttributes attributes = new ProductAttributes();
        attributes.setName(product.getName());
        attributes.setPrice(product.getPrice());

        JsonApiData<ProductAttributes> data = new JsonApiData<>();
        data.setId(String.valueOf(product.getId()));
        data.setType("products");
        data.setAttributes(attributes);
        return data;
    }
}
