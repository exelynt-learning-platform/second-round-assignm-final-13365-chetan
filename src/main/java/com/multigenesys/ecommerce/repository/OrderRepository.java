
package com.multigenesys.ecommerce.repository;

import com.multigenesys.ecommerce.entity.Order;
import com.multigenesys.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByUser(User user);
}