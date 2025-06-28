package com.lumastyle.delivery.service;

import com.lumastyle.delivery.dto.product.ProductRequest;
import com.lumastyle.delivery.dto.product.ProductResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponse addProduct(ProductRequest request, MultipartFile file);

    List<ProductResponse> readProducts();

    ProductResponse readProduct(String id);

    void deleteProduct(String id);

}
