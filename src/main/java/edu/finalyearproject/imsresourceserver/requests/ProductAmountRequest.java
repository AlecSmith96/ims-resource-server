package edu.finalyearproject.imsresourceserver.requests;

import lombok.Data;

/**
 * Request Object for updating the Reorder Amount for a product.
 */
@Data
public class ProductAmountRequest
{
    private int newAmount;
}
