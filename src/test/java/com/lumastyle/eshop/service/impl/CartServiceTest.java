package com.lumastyle.eshop.service.impl;

import com.lumastyle.eshop.dto.cart.CartRequest;
import com.lumastyle.eshop.dto.cart.CartResponse;
import com.lumastyle.eshop.entity.CartEntity;
import com.lumastyle.eshop.entity.UserEntity;
import com.lumastyle.eshop.exception.ResourceNotFoundException;
import com.lumastyle.eshop.mapper.CartMapper;
import com.lumastyle.eshop.mapper.UserMapperImpl;
import com.lumastyle.eshop.repository.CartRepository;
import com.lumastyle.eshop.repository.UserRepository;
import com.lumastyle.eshop.service.AuthFacade;
import com.lumastyle.eshop.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

/**
 * Unit tests for {@link CartServiceImpl}, covering add, get, clean and remove operations.
 */
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private CartMapper cartMapper;
    @Mock private UserService userService;
    @Mock private AtomicInteger cartItemsGauge;

    @InjectMocks
    private CartServiceImpl cartServiceImpl;

    /**
     * Test addToCart propagates an exception when repository.findByUserId fails.
     */
    @Test
    @DisplayName("addToCart throws when repository findByUserId fails")
    @Tag("Unit")
    void testAddToCart_repositoryThrows() {
        when(cartRepository.findByUserId(anyString()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));
        when(userService.getCurrentUserId()).thenReturn("42");

        assertThrows(ResourceNotFoundException.class,
                () -> cartServiceImpl.addToCart(new CartRequest()));

        verify(userService).getCurrentUserId();
        verify(cartRepository).findByUserId("42");
    }

    /**
     * Test addToCart propagates an exception when mapper.toResponse fails after save.
     */
    @Test
    @DisplayName("addToCart throws when mapper.toResponse fails")
    @Tag("Unit")
    void testAddToCart_mapperThrows() {
        CartEntity saved = new CartEntity(); saved.setId("42"); saved.setItems(new HashMap<>()); saved.setUserId("42");
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.of(saved));
        when(cartRepository.save(any(CartEntity.class))).thenReturn(saved);
        when(userService.getCurrentUserId()).thenReturn("42");
        when(cartMapper.toResponse(any(CartEntity.class)))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        assertThrows(ResourceNotFoundException.class,
                () -> cartServiceImpl.addToCart(new CartRequest()));

        verify(cartRepository).findByUserId("42");
        verify(cartRepository).save(isA(CartEntity.class));
        verify(cartMapper).toResponse(isA(CartEntity.class));
    }

    /**
     * Test addToCart returns response when repository initially empty.
     */
    @Test
    @DisplayName("addToCart returns new cart when none exists")
    @Tag("Unit")
    void testAddToCart_createsNewCart() {
        CartEntity saved = new CartEntity(); saved.setId("42"); saved.setItems(new HashMap<>()); saved.setUserId("42");
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(cartRepository.save(any(CartEntity.class))).thenReturn(saved);
        when(userService.getCurrentUserId()).thenReturn("42");
        CartResponse expected = CartResponse.builder().id("42").userId("42").items(new HashMap<>()).build();
        when(cartMapper.toResponse(any(CartEntity.class))).thenReturn(expected);

        CartResponse actual = cartServiceImpl.addToCart(new CartRequest());

        verify(cartRepository).findByUserId("42");
        verify(cartRepository).save(isA(CartEntity.class));
        verify(cartMapper).toResponse(isA(CartEntity.class));
        assertEquals("42", actual.getId());
        assertEquals("42", actual.getUserId());
        assertTrue(actual.getItems().isEmpty());
    }

    /**
     * Test getCart propagates exception when mapper.toResponse fails.
     */
    @Test
    @DisplayName("getCart throws when mapper.toResponse fails")
    @Tag("Unit")
    void testGetCart_mapperThrows() {
        CartEntity existing = new CartEntity(); existing.setId("42"); existing.setItems(new HashMap<>()); existing.setUserId("42");
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.of(existing));
        when(userService.getCurrentUserId()).thenReturn("42");
        when(cartMapper.toResponse(any(CartEntity.class)))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        assertThrows(ResourceNotFoundException.class, () -> cartServiceImpl.getCart());

        verify(cartRepository).findByUserId("42");
        verify(cartMapper).toResponse(isA(CartEntity.class));
    }

    /**
     * Test getCart propagates an exception when userService.getCurrentUserId fails.
     */
    @Test
    @DisplayName("getCart throws when userService fails")
    @Tag("Unit")
    void testGetCart_userServiceThrows() {
        when(userService.getCurrentUserId())
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        assertThrows(ResourceNotFoundException.class, () -> cartServiceImpl.getCart());
        verify(userService).getCurrentUserId();
    }

    /**
     * Test getCart integrates with UserRepository, AuthFacade and UserServiceImpl when cart not found.
     */
    @Test
    @DisplayName("getCart loads user and handles missing cart")
    @Tag("Unit")
    void testGetCart_callsFindByEmail() {
        CartRepository repo = mock(CartRepository.class);
        when(repo.findByUserId(anyString()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        UserEntity user = new UserEntity(); user.setId("42"); user.setEmail("jane.doe@example.org");
        UserRepository userRepo = mock(UserRepository.class);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        AuthFacade auth = mock(AuthFacade.class);
        when(auth.getAuthentication())
                .thenReturn(OneTimeTokenAuthenticationToken.unauthenticated("ABC123"));
        UserService userSvcImpl = new UserServiceImpl(userRepo, new UserMapperImpl(), auth, new BCryptPasswordEncoder());
        CartServiceImpl svc = new CartServiceImpl(
                repo,
                userSvcImpl,
                mock(CartMapper.class),
                new AtomicInteger()
        );


        assertThrows(ResourceNotFoundException.class, svc::getCart);
        verify(repo).findByUserId("42");
        verify(userRepo, times(2)).findByEmail("");
        verify(auth, times(2)).getAuthentication();
    }

    /**
     * Test getCart returns response when cart exists.
     */
    @Test
    @DisplayName("getCart returns existing cart")
    @Tag("Unit")
    void testGetCart_returnsCart() {
        CartEntity existing = new CartEntity(); existing.setId("42"); existing.setItems(new HashMap<>()); existing.setUserId("42");
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.of(existing));
        when(userService.getCurrentUserId()).thenReturn("42");
        CartResponse expected = CartResponse.builder().id("42").userId("42").items(new HashMap<>()).build();
        when(cartMapper.toResponse(any(CartEntity.class))).thenReturn(expected);

        CartResponse actual = cartServiceImpl.getCart();

        verify(cartRepository, atLeast(1)).findByUserId("42");
        verify(cartMapper).toResponse(isA(CartEntity.class));
        assertEquals("42", actual.getId());
        assertEquals("42", actual.getUserId());
        assertTrue(actual.getItems().isEmpty());
    }

    /**
     * Test cleanCart propagates an exception when repository.deleteByUserId fails.
     */
    @Test
    @DisplayName("cleanCart throws when repository fails")
    @Tag("Unit")
    void testCleanCart_repositoryThrows() {
        doThrow(new ResourceNotFoundException("An error occurred"))
                .when(cartRepository).deleteByUserId(anyString());
        when(userService.getCurrentUserId()).thenReturn("42");

        assertThrows(ResourceNotFoundException.class, () -> cartServiceImpl.cleanCart());
        verify(cartRepository).deleteByUserId("42");
        verify(userService).getCurrentUserId();
    }

    /**
     * Test cleanCart succeeds when repository.deleteByUserId does nothing.
     */
    @Test
    @DisplayName("cleanCart does nothing on success")
    @Tag("Unit")
    void testCleanCart_success() {
        doNothing().when(cartRepository).deleteByUserId(anyString());
        when(userService.getCurrentUserId()).thenReturn("42");

        cartServiceImpl.cleanCart();

        verify(cartRepository).deleteByUserId("42");
        verify(userService).getCurrentUserId();
    }

    /**
     * Test cleanCart propagates an exception when userService.getCurrentUserId fails.
     */
    @Test
    @DisplayName("cleanCart throws when userService fails")
    @Tag("Unit")
    void testCleanCart_userServiceThrows() {
        when(userService.getCurrentUserId())
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        assertThrows(ResourceNotFoundException.class, () -> cartServiceImpl.cleanCart());
        verify(userService).getCurrentUserId();
    }

    /**
     * Test removeFromCart propagates exception when mapper.toResponse fails.
     */
    @Test
    @DisplayName("removeFromCart throws when mapper.toResponse fails")
    @Tag("Unit")
    void testRemoveFromCart_mapperThrows() {
        CartEntity existing = new CartEntity(); existing.setId("42"); existing.setItems(new HashMap<>()); existing.setUserId("42");
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.of(existing));
        when(userService.getCurrentUserId()).thenReturn("42");
        when(cartMapper.toResponse(any(CartEntity.class)))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        assertThrows(ResourceNotFoundException.class,
                () -> cartServiceImpl.removeFromCart(new CartRequest()));

        verify(cartMapper).toResponse(isA(CartEntity.class));
        verify(cartRepository).findByUserId("42");
    }

    /**
     * Test removeFromCart propagates an exception when repository.findByUserId returns empty.
     */
    @Test
    @DisplayName("removeFromCart throws when repository returns empty")
    @Tag("Unit")
    void testRemoveFromCart_emptyRepoThrows() {
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.empty());
        when(userService.getCurrentUserId()).thenReturn("42");

        assertThrows(ResourceNotFoundException.class,
                () -> cartServiceImpl.removeFromCart(new CartRequest()));

        verify(cartRepository).findByUserId("42");
        verify(userService, times(2)).getCurrentUserId();
    }

    /**
     * Test removeFromCart integrates with UserRepository, AuthFacade and UserServiceImpl when cart not found.
     */
    @Test
    @DisplayName("removeFromCart loads user and handles missing cart")
    @Tag("Unit")
    void testRemoveFromCart_callsFindByEmail() {
        CartRepository repo = mock(CartRepository.class);
        when(repo.findByUserId(anyString()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        UserEntity user = new UserEntity(); user.setId("42"); user.setEmail("jane.doe@example.org");
        UserRepository userRepo = mock(UserRepository.class);
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(user));
        AuthFacade auth = mock(AuthFacade.class);
        when(auth.getAuthentication())
                .thenReturn(OneTimeTokenAuthenticationToken.unauthenticated("ABC123"));
        UserService userSvcImpl = new UserServiceImpl(userRepo, new UserMapperImpl(), auth, new BCryptPasswordEncoder());
        CartServiceImpl svc = new CartServiceImpl(
                repo,
                userSvcImpl,
                mock(CartMapper.class),
                new AtomicInteger()
        );

        assertThrows(ResourceNotFoundException.class,
                () -> svc.removeFromCart(new CartRequest()));

        verify(repo).findByUserId("42");
        verify(userRepo, times(2)).findByEmail("");
        verify(auth, times(2)).getAuthentication();
    }

    /**
     * Test removeFromCart returns response when cart exists.
     */
    @Test
    @DisplayName("removeFromCart returns existing cart")
    @Tag("Unit")
    void testRemoveFromCart_returnsCart() {
        CartEntity existing = new CartEntity(); existing.setId("42"); existing.setItems(new HashMap<>()); existing.setUserId("42");
        when(cartRepository.findByUserId(anyString())).thenReturn(Optional.of(existing));
        when(userService.getCurrentUserId()).thenReturn("42");
        CartResponse expected = CartResponse.builder().id("42").userId("42").items(new HashMap<>()).build();
        when(cartMapper.toResponse(any(CartEntity.class))).thenReturn(expected);

        CartResponse actual = cartServiceImpl.removeFromCart(new CartRequest());

        verify(cartRepository).findByUserId("42");
        verify(cartMapper).toResponse(isA(CartEntity.class));
        assertEquals("42", actual.getId());
        assertEquals("42", actual.getUserId());
        assertTrue(actual.getItems().isEmpty());
    }
}
