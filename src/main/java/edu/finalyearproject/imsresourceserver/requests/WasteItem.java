/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Wrapper class for a product to be wasted in the waste report.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WasteItem
{
    private int sku;
    private String reason;
    private int quantity;
    private String name = null;
}
