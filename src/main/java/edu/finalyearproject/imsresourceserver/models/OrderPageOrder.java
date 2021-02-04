package edu.finalyearproject.imsresourceserver.models;

import lombok.Data;

import java.sql.Date;

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
