
package com.multigenesys.ecommerce.repository;

import com.multigenesys.ecommerce.entity.Cart;
import com.multigenesys.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByUser(User user);
}