
package com.multigenesys.ecommerce.service.impl;

import com.multigenesys.ecommerce.dto.product.ProductRequest;
import com.multigenesys.ecommerce.entity.Product;
import com.multigenesys.ecommerce.exception.BadRequestException;
import com.multigenesys.ecommerce.exception.ResourceNotFoundException;
import com.multigenesys.ecommerce.repository.ProductRepository;
import com.multigenesys.ecommerce.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Override
    public Product create(ProductRequest request) {
        validate(request);

        Product product = new Product();
        product.setName(request.name);
        product.setDescription(request.description);
        product.setPrice(request.price);
        product.setStockQuantity(request.stockQuantity);
        product.setImageUrl(request.imageUrl);

        return productRepository.save(product);
    }

    @Override
    public List<Product> getAll() {
        return productRepository.findAll();
    }

    @Override
    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));
    }

    @Override
    public Product update(Long id, ProductRequest request) {
        validate(request);

        Product product = getById(id);

        product.setName(request.name);
        product.setDescription(request.description);
        product.setPrice(request.price);
        product.setStockQuantity(request.stockQuantity);
        product.setImageUrl(request.imageUrl);

        return productRepository.save(product);
    }

    @Override
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Product not found");
        }
        productRepository.deleteById(id);
    }

    private void validate(ProductRequest request) {
        if (request == null) {
            throw new BadRequestException("Product data is required");
        }
        if (request.name == null || request.name.isBlank()) {
            throw new BadRequestException("Name is required");
        }
        if (request.description == null || request.description.isBlank()) {
            throw new BadRequestException("Description is required");
        }
        if (request.price <= 0) {
            throw new BadRequestException("Price must be greater than 0");
        }
        if (request.stockQuantity < 0) {
            throw new BadRequestException("Stock quantity cannot be negative");
        }
        if (request.imageUrl == null || request.imageUrl.isBlank()) {
            throw new BadRequestException("Image URL is required");
        }
    }
}
