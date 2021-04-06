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
 * Wrapper class containing product ids in a purchase order request.
 */
@Data
public class ProductIds
{
    private List<Integer> ids;
}
