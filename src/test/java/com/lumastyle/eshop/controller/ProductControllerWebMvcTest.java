package com.lumastyle.eshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumastyle.eshop.dto.product.ProductRequest;
import com.lumastyle.eshop.exception.FileStorageException;
import com.lumastyle.eshop.exception.ResourceNotFoundException;
import com.lumastyle.eshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerWebMvcTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void readProduct_notFound_returns404() throws Exception {
        doThrow(new ResourceNotFoundException("not found"))
                .when(productService).readProduct("bad");

        mockMvc.perform(get("/api/products/bad"))
                .andExpect(status().isNotFound());
    }

    @Test
    void addProduct_missingName_returns400() throws Exception {
        ProductRequest req = new ProductRequest("", "d", BigDecimal.ONE, "cat");
        MockMultipartFile json = new MockMultipartFile("product", "", "application/json",
                objectMapper.writeValueAsBytes(req));
        MockMultipartFile file = new MockMultipartFile("file", "test.png", "image/png", new byte[1]);

        mockMvc.perform(multipart("/api/products")
                        .file(json)
                        .file(file))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteProduct_storageError_returns500() throws Exception {
        doThrow(new FileStorageException("fail"))
                .when(productService).deleteProduct("1");

        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isInternalServerError());
    }
}