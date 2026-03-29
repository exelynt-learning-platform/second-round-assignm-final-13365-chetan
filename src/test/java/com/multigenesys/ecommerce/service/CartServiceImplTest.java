package com.multigenesys.ecommerce.service;

import com.multigenesys.ecommerce.dto.cart.UpdateCartItemRequest;
import com.multigenesys.ecommerce.entity.*;
import com.multigenesys.ecommerce.repository.CartRepository;
import com.multigenesys.ecommerce.repository.ProductRepository;
import com.multigenesys.ecommerce.repository.UserRepository;
import com.multigenesys.ecommerce.service.impl.CartServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private com.multigenesys.ecommerce.repository.CartItemRepository cartItemRepository;

    @InjectMocks
    private CartServiceImpl cartService;

    @Test
    void updateItemChangesQuantityForOwnCart() {
        User user = new User();
        user.setEmail("test@example.com");

        Product product = new Product();
        product.setName("Phone");
        setId(product, 1L);

        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(1);

        Cart cart = new Cart();
        cart.setUser(user);
        cart.setItems(new ArrayList<>(List.of(item)));

        UpdateCartItemRequest request = new UpdateCartItemRequest();
        request.productId = 1L;
        request.quantity = 3;

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUser(user)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Cart updated = cartService.updateItem(1L, request);

        assertEquals(3, updated.getItems().get(0).getQuantity());

        ArgumentCaptor<Cart> captor = ArgumentCaptor.forClass(Cart.class);
        verify(cartRepository).save(captor.capture());
        assertEquals(3, captor.getValue().getItems().get(0).getQuantity());
    }

    private void setId(Product product, Long id) {
        try {
            Field field = Product.class.getDeclaredField("id");
            field.setAccessible(true);
            field.set(product, id);
        } catch (ReflectiveOperationException ex) {
            throw new RuntimeException(ex);
        }
    }
}
