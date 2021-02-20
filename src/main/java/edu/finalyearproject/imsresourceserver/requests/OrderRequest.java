package edu.finalyearproject.imsresourceserver.requests;

import lombok.Data;

/**
 * Model class for processing JSON from external application requesting a new customer order.
 */
@Data
public class OrderRequest
{
    private int customer_id;
    private ProductRequest[] products;
}
