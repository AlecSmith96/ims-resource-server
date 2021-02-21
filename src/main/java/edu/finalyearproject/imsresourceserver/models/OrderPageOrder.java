/**
 * Copyright (C) Alec R. C. Smith - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Alec Smith <alec.smith@uea.ac.uk>, 2020-2021
 */
package edu.finalyearproject.imsresourceserver.models;

import lombok.Data;

/**
 * Class for formatting Order objects to a format that can be displayed in the Customer Orders table
 */
@Data
public class OrderPageOrder
{
    private String id;
    private String customer;
    private String order_date;
    private String arrival_date;
}
