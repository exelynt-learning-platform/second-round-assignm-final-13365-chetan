package com.multigenesys.ecommerce.dto.order;

import java.util.List;

public class OrderResponse {

    public Long orderId;
    public String shippingAddress;
    public double totalPrice;
    public String status;
    public String paymentStatus;
    public List<String> items;
}
