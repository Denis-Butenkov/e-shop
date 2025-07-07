package com.lumastyle.eshop.controller;

import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumastyle.eshop.dto.auth.AuthRequest;
import com.lumastyle.eshop.exception.GlobalExceptionHandler;
import com.lumastyle.eshop.service.impl.AppUserDetailsService;
import com.lumastyle.eshop.util.JwtUtil;

import java.util.ArrayList;

import io.micrometer.core.instrument.Counter;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ContextConfiguration(classes = {AuthController.class, GlobalExceptionHandler.class})
@DisabledInAotMode
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@PropertySource("classpath:application-test.properties")
class AuthControllerTest {
    @MockitoBean
    private AppUserDetailsService appUserDetailsService;

    @Autowired
    private AuthController authController;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private Counter authFailedCounter;

    /**
     * Test {@link AuthController#login(AuthRequest)}.
     *
     * <ul>
     *   <li>Given {@code jane.doe@example.org}.
     *   <li>Then status {@link StatusResultMatchers#isOk()}.
     * </ul>
     *
     * <p>Method under test: {@link AuthController#login(AuthRequest)}
     */
    @Test
    @DisplayName("Test login(AuthRequest); given 'jane.doe@example.org'; then status isOk()")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"com.lumastyle.eshop.dto.auth.AuthResponse AuthController.login(AuthRequest)"})
    void testLogin_givenJaneDoeExampleOrg_thenStatusIsOk() throws Exception {
        // Arrange
        when(appUserDetailsService.loadUserByUsername(Mockito.<String>any()))
                .thenReturn(new User("janedoe", "iloveyou", new ArrayList<>()));
        when(jwtUtil.generateToken(Mockito.<UserDetails>any())).thenReturn("ABC123");
        when(authenticationManager.authenticate(Mockito.<Authentication>any()))
                .thenReturn(OneTimeTokenAuthenticationToken.unauthenticated("ABC123"));

        AuthRequest authRequest = new AuthRequest();
        authRequest.setEmail("jane.doe@example.org");
        authRequest.setPassword("iloveyou");
        String content = new ObjectMapper().writeValueAsString(authRequest);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string("{\"email\":\"jane.doe@example.org\",\"token\":\"ABC123\"}"));
    }
}
