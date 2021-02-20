package edu.finalyearproject.imsresourceserver.requests;

import lombok.Data;

import java.util.List;

/**
 * Model class for processing JSON from front end application for creating a new Purchase Order.
 */
@Data
public class PurchaseOrderRequest
{
    private String supplier;
    private List<ProductItem> products;


}
