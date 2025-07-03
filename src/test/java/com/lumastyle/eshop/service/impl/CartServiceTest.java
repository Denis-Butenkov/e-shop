package com.lumastyle.eshop.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
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

import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {CartServiceImpl.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class CartServiceTest {
    @MockitoBean
    private CartMapper cartMapper;

    @MockitoBean
    private CartRepository cartRepository;

    @Autowired
    private CartServiceImpl cartServiceImpl;

    @MockitoBean
    private UserService userService;

    /**
     * Test {@link CartServiceImpl#addToCart(CartRequest)}.
     *
     * <p>Method under test: {@link CartServiceImpl#addToCart(CartRequest)}
     */
    @Test
    @DisplayName("Test addToCart(CartRequest)")
    @Tag("ContributionFromDiffblue")
    @MethodsUnderTest({
            "com.lumastyle.eshop.dto.cart.CartResponse CartServiceImpl.addToCart(CartRequest)"
    })
    void testAddToCart() {
        // Arrange
        when(cartRepository.findByUserId(Mockito.<String>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));
        when(userService.getCurrentUserId()).thenReturn("42");

        // Act and Assert
        assertThrows(
                ResourceNotFoundException.class, () -> cartServiceImpl.addToCart(new CartRequest()));
        verify(cartRepository).findByUserId(eq("42"));
        verify(userService).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#addToCart(CartRequest)}.
     *
     * <p>Method under test: {@link CartServiceImpl#addToCart(CartRequest)}
     */
    @Test
    @DisplayName("Test addToCart(CartRequest)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.addToCart(CartRequest)"})
    void testAddToCart2() {
        // Arrange
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId("42");
        cartEntity.setItems(new HashMap<>());
        cartEntity.setUserId("42");

        CartEntity cartEntity2 = new CartEntity();
        cartEntity2.setId("42");
        cartEntity2.setItems(new HashMap<>());
        cartEntity2.setUserId("42");
        Optional<CartEntity> ofResult = Optional.of(cartEntity2);
        when(cartRepository.save(Mockito.<CartEntity>any())).thenReturn(cartEntity);
        when(cartRepository.findByUserId(Mockito.<String>any())).thenReturn(ofResult);
        when(userService.getCurrentUserId()).thenReturn("42");
        when(cartMapper.toResponse(Mockito.<CartEntity>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(
                ResourceNotFoundException.class, () -> cartServiceImpl.addToCart(new CartRequest()));
        verify(cartMapper).toResponse(isA(CartEntity.class));
        verify(cartRepository).findByUserId(eq("42"));
        verify(userService).getCurrentUserId();
        verify(cartRepository).save(isA(CartEntity.class));
    }

    /**
     * Test {@link CartServiceImpl#addToCart(CartRequest)}.
     *
     * <ul>
     *   <li>Given {@link CartRepository} {@link CartRepository#findByUserId(String)} return empty.
     *   <li>Then return Id is {@code 42}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#addToCart(CartRequest)}
     */
    @Test
    @DisplayName(
            "Test addToCart(CartRequest); given CartRepository findByUserId(String) return empty; then return Id is '42'")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.addToCart(CartRequest)"})
    void testAddToCart_givenCartRepositoryFindByUserIdReturnEmpty_thenReturnIdIs42() {
        // Arrange
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId("42");
        cartEntity.setItems(new HashMap<>());
        cartEntity.setUserId("42");
        when(cartRepository.save(Mockito.<CartEntity>any())).thenReturn(cartEntity);
        Optional<CartEntity> emptyResult = Optional.empty();
        when(cartRepository.findByUserId(Mockito.<String>any())).thenReturn(emptyResult);
        when(userService.getCurrentUserId()).thenReturn("42");
        CartResponse buildResult = CartResponse.builder().id("42").userId("42").build();
        when(cartMapper.toResponse(Mockito.<CartEntity>any())).thenReturn(buildResult);

        // Act
        CartResponse actualAddToCartResult = cartServiceImpl.addToCart(new CartRequest());

        // Assert
        verify(cartMapper).toResponse(isA(CartEntity.class));
        verify(cartRepository).findByUserId(eq("42"));
        verify(userService).getCurrentUserId();
        verify(cartRepository).save(isA(CartEntity.class));
        assertEquals("42", actualAddToCartResult.getId());
        assertEquals("42", actualAddToCartResult.getUserId());
        assertTrue(actualAddToCartResult.getItems().isEmpty());
    }

    /**
     * Test {@link CartServiceImpl#addToCart(CartRequest)}.
     *
     * <ul>
     *   <li>Given {@link CartRepository} {@link CartRepository#findByUserId(String)} return of {@link
     *       CartEntity#CartEntity()}.
     *   <li>Then return Id is {@code 42}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#addToCart(CartRequest)}
     */
    @Test
    @DisplayName(
            "Test addToCart(CartRequest); given CartRepository findByUserId(String) return of CartEntity(); then return Id is '42'")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.addToCart(CartRequest)"})
    void testAddToCart_givenCartRepositoryFindByUserIdReturnOfCartEntity_thenReturnIdIs42() {
        // Arrange
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId("42");
        cartEntity.setItems(new HashMap<>());
        cartEntity.setUserId("42");

        CartEntity cartEntity2 = new CartEntity();
        cartEntity2.setId("42");
        cartEntity2.setItems(new HashMap<>());
        cartEntity2.setUserId("42");
        Optional<CartEntity> ofResult = Optional.of(cartEntity2);
        when(cartRepository.save(Mockito.<CartEntity>any())).thenReturn(cartEntity);
        when(cartRepository.findByUserId(Mockito.<String>any())).thenReturn(ofResult);
        when(userService.getCurrentUserId()).thenReturn("42");
        CartResponse buildResult = CartResponse.builder().id("42").userId("42").build();
        when(cartMapper.toResponse(Mockito.<CartEntity>any())).thenReturn(buildResult);

        // Act
        CartResponse actualAddToCartResult = cartServiceImpl.addToCart(new CartRequest());

        // Assert
        verify(cartMapper).toResponse(isA(CartEntity.class));
        verify(cartRepository).findByUserId(eq("42"));
        verify(userService).getCurrentUserId();
        verify(cartRepository).save(isA(CartEntity.class));
        assertEquals("42", actualAddToCartResult.getId());
        assertEquals("42", actualAddToCartResult.getUserId());
        assertTrue(actualAddToCartResult.getItems().isEmpty());
    }

    /**
     * Test {@link CartServiceImpl#addToCart(CartRequest)}.
     *
     * <ul>
     *   <li>Given {@link CartRepository}.
     *   <li>Then throw {@link ResourceNotFoundException}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#addToCart(CartRequest)}
     */
    @Test
    @DisplayName(
            "Test addToCart(CartRequest); given CartRepository; then throw ResourceNotFoundException")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.addToCart(CartRequest)"})
    void testAddToCart_givenCartRepository_thenThrowResourceNotFoundException() {
        // Arrange
        when(userService.getCurrentUserId())
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(
                ResourceNotFoundException.class, () -> cartServiceImpl.addToCart(new CartRequest()));
        verify(userService).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#getCart()}.
     *
     * <p>Method under test: {@link CartServiceImpl#getCart()}
     */
    @Test
    @DisplayName("Test getCart()")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.getCart()"})
    void testGetCart() {
        // Arrange
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId("42");
        cartEntity.setItems(new HashMap<>());
        cartEntity.setUserId("42");
        Optional<CartEntity> ofResult = Optional.of(cartEntity);
        when(cartRepository.findByUserId(Mockito.<String>any())).thenReturn(ofResult);
        when(userService.getCurrentUserId()).thenReturn("42");
        when(cartMapper.toResponse(Mockito.<CartEntity>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> cartServiceImpl.getCart());
        verify(cartMapper).toResponse(isA(CartEntity.class));
        verify(cartRepository).findByUserId(eq("42"));
        verify(userService, atLeast(1)).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#getCart()}.
     *
     * <ul>
     *   <li>Given {@link CartRepository}.
     *   <li>Then throw {@link ResourceNotFoundException}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#getCart()}
     */
    @Test
    @DisplayName("Test getCart(); given CartRepository; then throw ResourceNotFoundException")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.getCart()"})
    void testGetCart_givenCartRepository_thenThrowResourceNotFoundException() {
        // Arrange
        when(userService.getCurrentUserId())
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> cartServiceImpl.getCart());
        verify(userService).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#getCart()}.
     *
     * <ul>
     *   <li>Then calls {@link UserRepository#findByEmail(String)}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#getCart()}
     */
    @Test
    @DisplayName("Test getCart(); then calls findByEmail(String)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.getCart()"})
    void testGetCart_thenCallsFindByEmail() {
        // Arrange
        CartRepository repository = mock(CartRepository.class);
        when(repository.findByUserId(Mockito.<String>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("jane.doe@example.org");
        userEntity.setFullName("Dr Jane Doe");
        userEntity.setId("42");
        userEntity.setPassword("iloveyou");
        Optional<UserEntity> ofResult = Optional.of(userEntity);
        UserRepository repository2 = mock(UserRepository.class);
        when(repository2.findByEmail(Mockito.<String>any())).thenReturn(ofResult);
        AuthFacade authFacade = mock(AuthFacade.class);
        when(authFacade.getAuthentication())
                .thenReturn(OneTimeTokenAuthenticationToken.unauthenticated("ABC123"));
        UserMapperImpl mapper = new UserMapperImpl();

        // Act and Assert
        assertThrows(
                ResourceNotFoundException.class,
                () ->
                        new CartServiceImpl(
                                repository,
                                new UserServiceImpl(
                                        repository2, mapper, authFacade, new BCryptPasswordEncoder()),
                                mock(CartMapper.class))
                                .getCart());
        verify(repository).findByUserId(eq("42"));
        verify(repository2, atLeast(1)).findByEmail(eq(""));
        verify(authFacade, atLeast(1)).getAuthentication();
    }

    /**
     * Test {@link CartServiceImpl#getCart()}.
     *
     * <ul>
     *   <li>Then return Id is {@code 42}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#getCart()}
     */
    @Test
    @DisplayName("Test getCart(); then return Id is '42'")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.getCart()"})
    void testGetCart_thenReturnIdIs42() {
        // Arrange
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId("42");
        cartEntity.setItems(new HashMap<>());
        cartEntity.setUserId("42");
        Optional<CartEntity> ofResult = Optional.of(cartEntity);
        when(cartRepository.findByUserId(Mockito.<String>any())).thenReturn(ofResult);
        when(userService.getCurrentUserId()).thenReturn("42");
        CartResponse buildResult = CartResponse.builder().id("42").userId("42").build();
        when(cartMapper.toResponse(Mockito.<CartEntity>any())).thenReturn(buildResult);

        // Act
        CartResponse actualCart = cartServiceImpl.getCart();

        // Assert
        verify(cartMapper).toResponse(isA(CartEntity.class));
        verify(cartRepository).findByUserId(eq("42"));
        verify(userService, atLeast(1)).getCurrentUserId();
        assertEquals("42", actualCart.getId());
        assertEquals("42", actualCart.getUserId());
        assertTrue(actualCart.getItems().isEmpty());
    }

    /**
     * Test {@link CartServiceImpl#cleanCart()}.
     *
     * <p>Method under test: {@link CartServiceImpl#cleanCart()}
     */
    @Test
    @DisplayName("Test cleanCart()")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void CartServiceImpl.cleanCart()"})
    void testCleanCart() {
        // Arrange
        doThrow(new ResourceNotFoundException("An error occurred"))
                .when(cartRepository)
                .deleteByUserId(Mockito.<String>any());
        when(userService.getCurrentUserId()).thenReturn("42");

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> cartServiceImpl.cleanCart());
        verify(cartRepository).deleteByUserId(eq("42"));
        verify(userService).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#cleanCart()}.
     *
     * <ul>
     *   <li>Given {@link CartRepository} {@link CartRepository#deleteByUserId(String)} does nothing.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#cleanCart()}
     */
    @Test
    @DisplayName("Test cleanCart(); given CartRepository deleteByUserId(String) does nothing")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void CartServiceImpl.cleanCart()"})
    void testCleanCart_givenCartRepositoryDeleteByUserIdDoesNothing() {
        // Arrange
        doNothing().when(cartRepository).deleteByUserId(Mockito.<String>any());
        when(userService.getCurrentUserId()).thenReturn("42");

        // Act
        cartServiceImpl.cleanCart();

        // Assert
        verify(cartRepository).deleteByUserId(eq("42"));
        verify(userService).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#cleanCart()}.
     *
     * <ul>
     *   <li>Given {@link CartRepository}.
     *   <li>Then throw {@link ResourceNotFoundException}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#cleanCart()}
     */
    @Test
    @DisplayName("Test cleanCart(); given CartRepository; then throw ResourceNotFoundException")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"void CartServiceImpl.cleanCart()"})
    void testCleanCart_givenCartRepository_thenThrowResourceNotFoundException() {
        // Arrange
        when(userService.getCurrentUserId())
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(ResourceNotFoundException.class, () -> cartServiceImpl.cleanCart());
        verify(userService).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#removeFromCart(CartRequest)}.
     *
     * <p>Method under test: {@link CartServiceImpl#removeFromCart(CartRequest)}
     */
    @Test
    @DisplayName("Test removeFromCart(CartRequest)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.removeFromCart(CartRequest)"})
    void testRemoveFromCart() {
        // Arrange
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId("42");
        cartEntity.setItems(new HashMap<>());
        cartEntity.setUserId("42");
        Optional<CartEntity> ofResult = Optional.of(cartEntity);
        when(cartRepository.findByUserId(Mockito.<String>any())).thenReturn(ofResult);
        when(userService.getCurrentUserId()).thenReturn("42");
        when(cartMapper.toResponse(Mockito.<CartEntity>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(
                ResourceNotFoundException.class, () -> cartServiceImpl.removeFromCart(new CartRequest()));
        verify(cartMapper).toResponse(isA(CartEntity.class));
        verify(cartRepository).findByUserId(eq("42"));
        verify(userService, atLeast(1)).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#removeFromCart(CartRequest)}.
     *
     * <ul>
     *   <li>Given {@link CartRepository} {@link CartRepository#findByUserId(String)} return empty.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#removeFromCart(CartRequest)}
     */
    @Test
    @DisplayName(
            "Test removeFromCart(CartRequest); given CartRepository findByUserId(String) return empty")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.removeFromCart(CartRequest)"})
    void testRemoveFromCart_givenCartRepositoryFindByUserIdReturnEmpty() {
        // Arrange
        Optional<CartEntity> emptyResult = Optional.empty();
        when(cartRepository.findByUserId(Mockito.<String>any())).thenReturn(emptyResult);
        when(userService.getCurrentUserId()).thenReturn("42");

        // Act and Assert
        assertThrows(
                ResourceNotFoundException.class, () -> cartServiceImpl.removeFromCart(new CartRequest()));
        verify(cartRepository).findByUserId(eq("42"));
        verify(userService, atLeast(1)).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#removeFromCart(CartRequest)}.
     *
     * <ul>
     *   <li>Given {@link CartRepository}.
     *   <li>Then throw {@link ResourceNotFoundException}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#removeFromCart(CartRequest)}
     */
    @Test
    @DisplayName(
            "Test removeFromCart(CartRequest); given CartRepository; then throw ResourceNotFoundException")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.removeFromCart(CartRequest)"})
    void testRemoveFromCart_givenCartRepository_thenThrowResourceNotFoundException() {
        // Arrange
        when(userService.getCurrentUserId())
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        // Act and Assert
        assertThrows(
                ResourceNotFoundException.class, () -> cartServiceImpl.removeFromCart(new CartRequest()));
        verify(userService).getCurrentUserId();
    }

    /**
     * Test {@link CartServiceImpl#removeFromCart(CartRequest)}.
     *
     * <ul>
     *   <li>Then calls {@link UserRepository#findByEmail(String)}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#removeFromCart(CartRequest)}
     */
    @Test
    @DisplayName("Test removeFromCart(CartRequest); then calls findByEmail(String)")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.removeFromCart(CartRequest)"})
    void testRemoveFromCart_thenCallsFindByEmail() {
        // Arrange
        CartRepository repository = mock(CartRepository.class);
        when(repository.findByUserId(Mockito.<String>any()))
                .thenThrow(new ResourceNotFoundException("An error occurred"));

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail("jane.doe@example.org");
        userEntity.setFullName("Dr Jane Doe");
        userEntity.setId("42");
        userEntity.setPassword("iloveyou");
        Optional<UserEntity> ofResult = Optional.of(userEntity);
        UserRepository repository2 = mock(UserRepository.class);
        when(repository2.findByEmail(Mockito.<String>any())).thenReturn(ofResult);
        AuthFacade authFacade = mock(AuthFacade.class);
        when(authFacade.getAuthentication())
                .thenReturn(OneTimeTokenAuthenticationToken.unauthenticated("ABC123"));
        UserMapperImpl mapper = new UserMapperImpl();
        CartServiceImpl cartServiceImpl =
                new CartServiceImpl(
                        repository,
                        new UserServiceImpl(repository2, mapper, authFacade, new BCryptPasswordEncoder()),
                        mock(CartMapper.class));

        // Act and Assert
        assertThrows(
                ResourceNotFoundException.class, () -> cartServiceImpl.removeFromCart(new CartRequest()));
        verify(repository).findByUserId(eq("42"));
        verify(repository2, atLeast(1)).findByEmail(eq(""));
        verify(authFacade, atLeast(1)).getAuthentication();
    }

    /**
     * Test {@link CartServiceImpl#removeFromCart(CartRequest)}.
     *
     * <ul>
     *   <li>Then return Id is {@code 42}.
     * </ul>
     *
     * <p>Method under test: {@link CartServiceImpl#removeFromCart(CartRequest)}
     */
    @Test
    @DisplayName("Test removeFromCart(CartRequest); then return Id is '42'")
    @Tag("ContributionFromDiffblue")
    @ManagedByDiffblue
    @MethodsUnderTest({"CartResponse CartServiceImpl.removeFromCart(CartRequest)"})
    void testRemoveFromCart_thenReturnIdIs42() {
        // Arrange
        CartEntity cartEntity = new CartEntity();
        cartEntity.setId("42");
        cartEntity.setItems(new HashMap<>());
        cartEntity.setUserId("42");
        Optional<CartEntity> ofResult = Optional.of(cartEntity);
        when(cartRepository.findByUserId(Mockito.<String>any())).thenReturn(ofResult);
        when(userService.getCurrentUserId()).thenReturn("42");
        CartResponse buildResult = CartResponse.builder().id("42").userId("42").build();
        when(cartMapper.toResponse(Mockito.<CartEntity>any())).thenReturn(buildResult);

        // Act
        CartResponse actualRemoveFromCartResult = cartServiceImpl.removeFromCart(new CartRequest());

        // Assert
        verify(cartMapper).toResponse(isA(CartEntity.class));
        verify(cartRepository).findByUserId(eq("42"));
        verify(userService, atLeast(1)).getCurrentUserId();
        assertEquals("42", actualRemoveFromCartResult.getId());
        assertEquals("42", actualRemoveFromCartResult.getUserId());
        assertTrue(actualRemoveFromCartResult.getItems().isEmpty());
    }
}
