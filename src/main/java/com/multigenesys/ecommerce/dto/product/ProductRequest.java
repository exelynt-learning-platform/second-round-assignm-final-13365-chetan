
package com.multigenesys.ecommerce.dto.product;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class ProductRequest {

    @NotBlank(message = "Name is required")
    public String name;

    @NotBlank(message = "Description is required")
    public String description;

    @Positive(message = "Price must be greater than 0")
    public double price;

    @Min(value = 0, message = "Stock quantity cannot be negative")
    public int stockQuantity;

    @NotBlank(message = "Image URL is required")
    public String imageUrl;
}
