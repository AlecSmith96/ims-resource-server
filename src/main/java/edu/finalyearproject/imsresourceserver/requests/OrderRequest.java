/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
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
