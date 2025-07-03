package com.lumastyle.eshop.controller;

import com.diffblue.cover.annotations.MethodsUnderTest;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lumastyle.eshop.dto.user.UserRequest;
import com.lumastyle.eshop.dto.user.UserResponse;
import com.lumastyle.eshop.exception.GlobalExceptionHandler;
import com.lumastyle.eshop.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.result.StatusResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {UserController.class, GlobalExceptionHandler.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class UserControllerTest {
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Autowired
    private UserController userController;

    @MockitoBean
    private UserService userService;

    /**
     * Test {@link UserController#register(UserRequest)}.
     *
     * <ul>
     *   <li>Given {@code Dr Jane Doe}.
     *   <li>Then status {@link StatusResultMatchers#isCreated()}.
     * </ul>
     *
     * <p>Method under test: {@link UserController#register(UserRequest)}
     */
    @Test
    @DisplayName("Test register(UserRequest); given 'Dr Jane Doe'; then status isCreated()")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({"UserResponse UserController.register(UserRequest)"})
    void testRegister_givenDrJaneDoe_thenStatusIsCreated() throws Exception {
        // Arrange
        UserResponse buildResult =
                UserResponse.builder()
                        .email("jane.doe@example.org")
                        .fullName("Dr Jane Doe")
                        .id("42")
                        .build();
        when(userService.registerUser(Mockito.<UserRequest>any())).thenReturn(buildResult);

        UserRequest userRequest = new UserRequest();
        userRequest.setEmail("jane.doe@example.org");
        userRequest.setFullName("Dr Jane Doe");
        userRequest.setPassword("iloveyou");
        String content = new ObjectMapper().writeValueAsString(userRequest);
        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.post("/api/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(userController)
                .setControllerAdvice(globalExceptionHandler)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(
                        MockMvcResultMatchers.content()
                                .string(
                                        "{\"id\":\"42\",\"fullName\":\"Dr Jane Doe\",\"email\":\"jane.doe@example.org\"}"));
    }
}
