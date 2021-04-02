package edu.finalyearproject.imsresourceserver.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request Object for updating the Reorder Amount for a product.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAmountRequest
{
    private int newAmount;
}
