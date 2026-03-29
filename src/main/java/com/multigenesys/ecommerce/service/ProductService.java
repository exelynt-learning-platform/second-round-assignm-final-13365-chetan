
package com.multigenesys.ecommerce.service;

import com.multigenesys.ecommerce.dto.product.ProductRequest;
import com.multigenesys.ecommerce.entity.Product;

import java.util.List;

public interface ProductService {

    Product create(ProductRequest request);

    List<Product> getAll();

    Product getById(Long id);

    Product update(Long id, ProductRequest request);

    void delete(Long id);
}