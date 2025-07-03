package com.lumastyle.eshop.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumastyle.eshop.dto.product.ProductRequest;
import com.lumastyle.eshop.dto.product.ProductResponse;
import com.lumastyle.eshop.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("test")
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @Test
    void readProducts_returnsList() throws Exception {
        when(productService.readProducts()).thenReturn(List.of(new ProductResponse()));

        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    @Test
    void addProduct_returnsCreated() throws Exception {
        ProductRequest req = new ProductRequest("n", "d", BigDecimal.ONE, "cat");
        when(productService.addProduct(any(ProductRequest.class), any())).thenReturn(new ProductResponse());
        MockMultipartFile json = new MockMultipartFile("product", "", "application/json",
                objectMapper.writeValueAsBytes(req));
        MockMultipartFile file = new MockMultipartFile("file", "img.png", "image/png", new byte[1]);

        mockMvc.perform(multipart("/api/products")
                        .file(json)
                        .file(file))
                .andExpect(status().isCreated());
    }
}
