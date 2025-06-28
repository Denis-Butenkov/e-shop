package com.lumastyle.delivery.controller;

import com.lumastyle.delivery.dto.product.ProductRequest;
import com.lumastyle.delivery.dto.product.ProductResponse;
import com.lumastyle.delivery.service.ProductService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@Validated
@RequiredArgsConstructor
@RequestMapping("/api/products")
@CrossOrigin(origins = "*")
public class ProductController {

    private static final String NAME_VALIDATION_PATTERN = "^[A-Za-z0-9\\-]+$";
    private final ProductService productService;


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse addProduct(@Valid @RequestPart("product") ProductRequest request,
                                      @RequestPart("file") MultipartFile file) {
        log.info("Received request to add a new product: {}", request);
        return productService.addProduct(request, file);
    }

    @GetMapping
    public List<ProductResponse> readProducts() {
        log.info("Received request for reading all foods");
        return productService.readProducts();
    }

    @GetMapping("/{id}")
    public ProductResponse readProduct(@PathVariable
                                 @Pattern(regexp = NAME_VALIDATION_PATTERN, message = "Invalid product id") String id) {
        log.info("Received request for reading a product with id: {}", id);
        return productService.readProduct(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@PathVariable @Pattern(regexp = NAME_VALIDATION_PATTERN,
            message = "Invalid product id") String id) {
        log.info("Received request for deleting a product with id: {}", id);
        productService.deleteProduct(id);
    }
}
