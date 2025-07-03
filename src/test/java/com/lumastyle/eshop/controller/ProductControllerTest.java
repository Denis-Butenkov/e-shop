package com.lumastyle.eshop.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import com.lumastyle.eshop.dto.product.ProductRequest;
import com.lumastyle.eshop.dto.product.ProductResponse;
import com.lumastyle.eshop.dto.product.ProductResponse.ProductResponseBuilder;
import com.lumastyle.eshop.exception.GlobalExceptionHandler;
import com.lumastyle.eshop.service.ProductService;

import java.math.BigDecimal;
import java.util.ArrayList;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

@ContextConfiguration(classes = {ProductController.class, GlobalExceptionHandler.class})
@DisabledInAotMode
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
class ProductControllerTest {
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @InjectMocks
    private ProductController productController;

    @Autowired
    private ProductController productController2;

    @MockitoBean
    private ProductService productService;

    /**
     * Test {@link ProductController#readProducts()}.
     *
     * <p>Method under test: {@link ProductController#readProducts()}
     */
    @Test
    @DisplayName("Test readProducts()")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"java.util.List ProductController.readProducts()"})
    void testReadProducts() throws Exception {
        // Arrange
        when(productService.readProducts()).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/api/products");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(productController2)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    /**
     * Test {@link ProductController#addProduct(ProductRequest, MultipartFile)}.
     *
     * <p>Method under test: {@link ProductController#addProduct(ProductRequest, MultipartFile)}
     */
    @Test
    @DisplayName("Test addProduct(ProductRequest, MultipartFile)")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({
            "com.lumastyle.eshop.dto.product.ProductResponse ProductController.addProduct(ProductRequest, MultipartFile)"
    })
     void testAddProduct() throws Exception {
        // Arrange
        MockMultipartHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.multipart("/api/products");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(productController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().is(400));
    }

    /**
     * Test {@link ProductController#readProduct(String)}.
     *
     * <p>Method under test: {@link ProductController#readProduct(String)}
     */
    @Test
    @DisplayName("Test readProduct(String)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"ProductResponse ProductController.readProduct(String)"})
    void testReadProduct() throws Exception {
        // Arrange
        ProductResponseBuilder nameResult =
                ProductResponse.builder()
                        .category("Category")
                        .description("The characteristics of someone or something")
                        .id("42")
                        .imageUrl("https://example.org/example")
                        .name("Name");
        ProductResponse buildResult = nameResult.price(new BigDecimal("2.3")).build();
        when(productService.readProduct(Mockito.<String>any())).thenReturn(buildResult);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.get("/api/products/{id}", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(productController2)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string(
                                        "{\"id\":\"42\",\"name\":\"Name\",\"description\":\"The characteristics of someone or something\",\"imageUrl\":\"https"
                                                + "://example.org/example\",\"price\":2.3,\"category\":\"Category\"}"));
    }

    /**
     * Test {@link ProductController#deleteProduct(String)}.
     *
     * <p>Method under test: {@link ProductController#deleteProduct(String)}
     */
    @Test
    @DisplayName("Test deleteProduct(String)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void ProductController.deleteProduct(String)"})
    void testDeleteProduct() throws Exception {
        // Arrange
        doNothing().when(productService).deleteProduct(Mockito.<String>any());
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/api/products/{id}", "42");

        // Act and Assert
        MockMvcBuilders.standaloneSetup(productController2)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isNoContent());
    }
}
