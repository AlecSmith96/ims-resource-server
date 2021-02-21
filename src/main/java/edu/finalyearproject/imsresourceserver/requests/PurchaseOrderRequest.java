/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
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
