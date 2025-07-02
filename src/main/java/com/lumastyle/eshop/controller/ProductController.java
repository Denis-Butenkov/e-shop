package com.lumastyle.eshop.controller;

import com.lumastyle.eshop.dto.product.ProductRequest;
import com.lumastyle.eshop.dto.product.ProductResponse;
import com.lumastyle.eshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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

    @Operation(summary = "Add a new product",
            description = "Creates a product with metadata and an image upload.")
    @ApiResponses({
            @ApiResponse(responseCode = "201",
                    description = "Product created",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse addProduct(@Valid @RequestBody(
                                              description = "Product data", required = true,
                                              content = @Content(schema = @Schema(implementation = ProductRequest.class)))
                                      @RequestPart("product") ProductRequest request,
                                      @RequestPart("file") MultipartFile file) {
        log.info("Received request to add a new product: {}", request);
        return productService.addProduct(request, file);
    }

    @Operation(summary = "Get all products",
            description = "Retrieves all available products.")
    @ApiResponse(responseCode = "200",
            description = "List of products",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ProductResponse.class)))
    @GetMapping
    public List<ProductResponse> readProducts() {
        log.info("Received request for reading all foods");
        return productService.readProducts();
    }

    @Operation(summary = "Get product by ID",
            description = "Retrieves a product by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200",
                    description = "Product found",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ProductResponse.class))),
            @ApiResponse(responseCode = "404", description = "Product not found", content = @Content)
    })
    @GetMapping("/{id}")
    public ProductResponse readProduct(@Parameter(description = "ID of the product",
            required = true,
            schema = @Schema(pattern = NAME_VALIDATION_PATTERN))
                                       @PathVariable @Pattern(
            regexp = NAME_VALIDATION_PATTERN,
            message = "Invalid product id") String id) {
        log.info("Received request for reading a product with id: {}", id);
        return productService.readProduct(id);
    }

    @Operation(summary = "Delete product",
            description = "Deletes a product by its ID.")
    @ApiResponse(responseCode = "204",
            description = "Product deleted")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteProduct(@Parameter(
            description = "ID of the product to delete",
            required = true,
            schema = @Schema(pattern = NAME_VALIDATION_PATTERN))
                              @PathVariable @Pattern(regexp = NAME_VALIDATION_PATTERN,
            message = "Invalid product id") String id) {
        log.info("Received request for deleting a product with id: {}", id);
        productService.deleteProduct(id);
    }
}
