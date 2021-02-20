package edu.finalyearproject.imsresourceserver.requests;

import lombok.Data;

@Data
public class ProductRequest
{
    private String sku;
    private int quantity;
}
