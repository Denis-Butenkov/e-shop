package com.lumastyle.eshop.service;

import com.lumastyle.eshop.dto.product.ProductRequest;
import com.lumastyle.eshop.dto.product.ProductResponse;
import com.lumastyle.eshop.exception.FileStorageException;
import com.lumastyle.eshop.exception.ResourceNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * CRUD operations for product catalog (including image handling).
 */
public interface ProductService {

    /**
     * Create a new product along with its image.
     *
     * @param request the product metadata (name, price, etc.)
     * @param file    the image to associate with the product
     * @return the newly created product DTO with its generated ID & URL
     */
    ProductResponse addProduct(ProductRequest request, MultipartFile file);

    /**
     * Read all products in the catalog.
     *
     * @return list of all products
     */
    List<ProductResponse> readProducts();

    /**
     * Read a single product, by its ID.
     *
     * @param id the product’s database ID
     * @return the matching product DTO
     * @throws ResourceNotFoundException if no such product exists
     */
    ProductResponse readProduct(String id);

    /**
     * Delete a product (and its stored image) by ID.
     *
     * @param id the product’s database ID
     * @throws FileStorageException if no such product exists
     */
    void deleteProduct(String id);

}
