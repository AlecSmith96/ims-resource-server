package edu.finalyearproject.imsresourceserver.requests;

import lombok.Data;

import java.util.List;

@Data
public class PurchaseOrderRequest
{
    private String supplier;
    private List<ProductItem> products;


}
