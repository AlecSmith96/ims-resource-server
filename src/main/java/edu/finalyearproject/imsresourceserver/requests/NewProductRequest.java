/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.requests;

import lombok.Data;

/**
 * Class representing data passed to '/product/add' endpoint in order to add a new Product record to the database.
 */
@Data
public class NewProductRequest
{
    private String name;
    private String sku;
    private int inventoryOnHand;
    private int reorderThreshold;
    private float price;
    private int reorderQuantity;
    private String supplierName;
}
