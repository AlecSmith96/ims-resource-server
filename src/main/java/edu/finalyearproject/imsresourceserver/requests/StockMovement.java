package edu.finalyearproject.imsresourceserver.requests;

import edu.finalyearproject.imsresourceserver.models.Product;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Date;
import java.text.SimpleDateFormat;

/**
 * Wrapper class to put customer and supplier orders in same object type for Stock Movement report.
 */
@Data
@AllArgsConstructor
public class StockMovement implements Comparable<StockMovement>
{
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd-MM-yyyy");

    private int id;
    private boolean supplierOrder;  // supplier or customer order
    private Date dateOfMovement;
    private String name;            // supplier name or customer name
    private Product product;
    private int quantity;           // reorder_amount is supplier order or 1 if customer order

    public String getDateOfMovement()
    {
        return DATE_FORMATTER.format(dateOfMovement);
    }

    public Date getDateOfMovementObject()
    {
        return dateOfMovement;
    }

    @Override
    public int compareTo(StockMovement stockMovement)
    {
        return dateOfMovement.compareTo(stockMovement.getDateOfMovementObject());
    }
}
